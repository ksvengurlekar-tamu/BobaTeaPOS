package green.gongchapos.cashierView;

import green.gongchapos.dbSetup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static green.gongchapos.GongCha.getSQLConnection;
import static green.gongchapos.GongCha.main;


/** Class for the CashierViewController, which controls the cashierView.fxml file and holds all
 * attributes and methods for the cashier view of the GongChaPOS system.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class CashierViewController {

    @FXML
    public GridPane mainMenuPane;

    @FXML
    public GridPane drinkPane;

    @FXML
    public TilePane subDrinkPane;

    @FXML
    public VBox rightVBox;

    @FXML
    public Stage cashierViewStage;

    @FXML
    public Pane drinkPopUp;

    @FXML
    public ListView<Drink> cartView;
    public VBox cartPane;
    public Text taxNumber;
    public Text subTotalNumber;
    public Text totalNumber;

    //public ListView<String> cartView;

    private boolean isPaneVisible = false; // Initial visibility state

    // Global drink adding variables
    private float price = 0;
    private String name = "";
    private boolean isLarge = false;

    // Global total variables
    private float total = 0;
    private float subtotal = 0;
    private float tax = 0;

    /** Drink class acts as a struct for a menu item that is being ordered
     * Contains a name, boolean to represent if its large, and price
     *
     * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
     */
    public static class Drink {
        String name;
        boolean isLarge;
        float price;

        Drink (String name, boolean isLarge, float price) {
            this.name = name;
            this.isLarge = isLarge;
            this.price = price;
        }

        @Override
        /** Overriden class toString method to display the contents of the Drink object
         *
         * @return a string displaying the name and price of a drink to be seen in the cart menu
         */
        public String toString() {
           return String.format("%s: $%.2f", name, price);
        }
    }


    private ArrayList<Drink> cart = new ArrayList<>();
    private ObservableList<Drink> observableCart;
    // private ArrayList<String> cart = new ArrayList<>();
    // private ObservableList<String> observableCart = FXCollections.observableArrayList();   cartView = new ListView<Drink>(observableCart);

    @FXML
    private String seriesName;

    public void setCashierViewController(Stage primaryStage) { this.cashierViewStage = primaryStage; }

    /** Handle the action triggered by selecting a series (e.g., category) of drinks.
     *
     * This method is responsible for populating a UI element (likely a container)
     * with buttons and text elements representing drink items fetched from a database.
     * It is typically invoked when a series button (e.g., category button) is clicked,
     * triggering an ActionEvent.
     *
     * @param event The ActionEvent triggered by selecting a series button.
     * @throws SQLException If a database error occurs during the process of fetching drink data.
     */
    @FXML
    private void seriesPress(ActionEvent event) throws SQLException {
        subDrinkPane.getChildren().clear();

        Scene scene = cashierViewStage.getScene();
        mainMenuPane.setDisable(true);
        mainMenuPane.setVisible(false);

        drinkPane.setDisable(false);
        drinkPane.setVisible(true);

        Button sourceButton = (Button) event.getSource();
        String drinkName = sourceButton.getText();

        try {
            Connection conn = getSQLConnection();
            String getDrinks = "SELECT * FROM menuItems WHERE menuItemCategory = ?";
            try(PreparedStatement drinkStatment = conn.prepareStatement(getDrinks)) {
                drinkStatment.setString(1, drinkName);
                ResultSet resultSet = drinkStatment.executeQuery();

                subDrinkPane.setAlignment(Pos.TOP_LEFT);
                subDrinkPane.setHgap(10.0);
                subDrinkPane.setVgap(10.0);
                subDrinkPane.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));

                int count = 0;

                while(resultSet.next()) {
                    Text text = new Text(resultSet.getString("menuItemName"));
                    text.setTextAlignment(TextAlignment.CENTER);
                    Button drinkButton = new Button();
                    drinkButton.setMinSize(161, 120);
                    drinkButton.setMaxSize(161, 120);
                    text.wrappingWidthProperty().bind(drinkButton.widthProperty());
                    drinkButton.setGraphic(text);

                    String drinkColor = resultSet.getString("color");
                    drinkButton.setId("drinkButton");

                    drinkButton.setOnAction(drinkButtonEvent -> {
                        Button Button = (Button) drinkButtonEvent.getSource();

                        Text oneText = (Text) Button.lookup("Text");
                        name = oneText.getText();

                        String getOneDrink = "SELECT menuitemprice FROM menuItems WHERE menuItemName = ?";
                        try(PreparedStatement onedrinkStatement = conn.prepareStatement(getOneDrink)) {
                            onedrinkStatement.setString(1, name);
                            ResultSet drinkResultSet = onedrinkStatement.executeQuery();
                            while(drinkResultSet.next()) {
                                price += drinkResultSet.getFloat("menuitemprice");
                            }

                        } catch(SQLException e) {
                            System.out.println("Error getting drinks");
                            e.printStackTrace();
                        }

                        mainMenuPane.setDisable(true);
                        mainMenuPane.setVisible(false);
                        mainMenuPane.setOpacity(0.3);

                        drinkPopUp.setDisable(false);
                        drinkPopUp.setVisible(true);
                        drinkPopUp.setOpacity(1);
                    });

                    drinkButton.setStyle(
                        "-fx-background-color: " + drinkColor + "; " +
                        "-fx-cursor: hand;"
                    );

                    drinkButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            // Mouse is hovering over the button
                            drinkButton.setStyle("-fx-background-color: " + drinkColor + "; -fx-border-color: #0099ff;" + "-fx-border-width: 2px;");
                        } else {
                            // Mouse is not hovering over the button
                            drinkButton.setStyle("-fx-background-color: " + drinkColor + "; -fx-border-width: 0px;");
                        }
                    });

                    subDrinkPane.getChildren().add(drinkButton);
                    count++;

                } while(count != 20) {
                    Button drinkButton = new Button();
                    drinkButton.setMinSize(161, 120);
                    drinkButton.setMaxSize(161, 120);
                    drinkButton.setStyle("-fx-background-color: #ffffff;");
                    subDrinkPane.getChildren().add(drinkButton);
                    count++;
                }

            } catch (SQLException e) {
                System.out.println("Error getting drinks");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }

    /** Handles the checkout action, toggling the visibility of a UI component.
     * If the component is visible, it will be hidden, and vice versa.
     *
     * @param actionEvent The event triggering the checkout action.
     */
    public void onCheckout(ActionEvent actionEvent) {
        if (isPaneVisible) {
            rightVBox.setDisable(true);
            rightVBox.setVisible(false);
            isPaneVisible = false;
        } else {
            rightVBox.setDisable(false);
            rightVBox.setVisible(true);
            isPaneVisible = true;
        }
    }


    /** Controls the scene display; when clicked, will return the scene to the previous iteration.
     *
     * @param actionEvent The action event triggered by the button press of back button.
     */
    public void backButton(ActionEvent actionEvent) {
        mainMenuPane.setDisable(false);
        mainMenuPane.setVisible(true);
        drinkPane.setDisable(true);
        drinkPane.setVisible(false);

    }


    /** Finds the corresponding topping menu item from the button selected and calcultes the
     *  running total of the price into the global.
     *
     *  @param actionEvent The action event triggered by the button press of a topping button.
     */
    public void toppingButton(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();
        String toppingName = sourceButton.getText();

        try {
            Connection conn = getSQLConnection();
            String getTopping = "SELECT menuitemprice FROM menuItems WHERE menuitemname = ?";
            try(PreparedStatement toppingStatement = conn.prepareStatement(getTopping)) {
                toppingStatement.setString(1, toppingName);
                ResultSet resultSet = toppingStatement.executeQuery();
                while(resultSet.next()) {
                    price += resultSet.getFloat("menuitemprice");
                }
                System.out.println(price);
            } catch(SQLException e) {
                System.out.println("Error getting toppings");
                e.printStackTrace();
            }
        } catch(SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }

    }


    /** Handles the action event triggered by the button press of the "Large" button during drink customization.
     * This method is responsible for updating the item size and adjusting the price based on the selection
     * of the "Large" option. It detects the button that triggered the event and increases the item price and
     * sets the size to large.
     *
     * @param actionEvent The action event triggered by the button press of large button.
     */
    public void isLarge(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();
        String size = sourceButton.getText();
        if(size.contains("Large")) {
            price += 0.75;
            isLarge = true;
        }
        else {
            isLarge = false;
        }
        System.out.println(price);

        // TODO: deselect large option
        // sourceButton.setDisable(true);
    }


    /** Adds a selected drink to the shopping cart.
     *
     * This method adds a new Drink object to the shopping cart with the specified name,
     * size, and price. After adding the item to the cart, it resets the price to zero
     * and adjusts the visibility and opacity of UI panes accordingly.
     *
     * @param actionEvent The ActionEvent triggered by the "Add to Cart" button.
     */
    @FXML
    public void addButton(ActionEvent actionEvent) {
        System.out.println(price);
        cart.add(new Drink(name, isLarge, price));

        mainMenuPane.setDisable(false);
        mainMenuPane.setVisible(true);
        mainMenuPane.setOpacity(1);

        drinkPopUp.setDisable(true);
        drinkPopUp.setVisible(false);

        Text drinkNameText = new Text(name);
        Text drinkPriceText = new Text(String.format("$%.2f", price));
        drinkNameText.setWrappingWidth(200);
        drinkPriceText.setStyle("-fx-font-size: 20px");
        drinkNameText.setStyle("-fx-font-size: 20px");
        
        HBox drinkNameBox = new HBox(drinkNameText);
        drinkNameBox.setAlignment(Pos.CENTER_LEFT);
        drinkNameBox.setMaxWidth(100);
        
        HBox drinkPriceBox = new HBox(drinkPriceText);
        drinkPriceBox.setAlignment(Pos.CENTER_RIGHT);
        
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox mainHBox = new HBox(drinkNameBox, spacer, drinkPriceBox);

        cartPane.getChildren().add(mainHBox);

        totalCharge();

        price = 0;
    }

    /** Updates the total charges, including the subtotal, tax, and total, based
     * on the current price.
     *
     * This method performs the following: it adds the current price to subtotal,
     * updates the subtotal display field with the new subtotal value, calculates the
     * tax by multiplying by 6.25% sales tax in Texas, updates the overall total display
     * field with the calculations.
     */
    private void totalCharge() {
        subtotal += price;
        subTotalNumber.setText(String.format("%.2f", subtotal));
        tax += price * 0.0625;
        taxNumber.setText(String.format("%.2f", subtotal * 0.0625));
        totalNumber.setText(String.format("%.2f", subtotal * 1.0625));
    }


    /** Places an order for items in the cart into the sales database.
     * This method retrieves necessary information from the cart, including
     * item details, and inserts them into the sales table in the database.
     * It also updates the order ID, ensuring uniqueness for each order.
     *
     * @throws SQLException if there's an issue with the SQL database operations.
     */
    @FXML
    private void charge() {
        int orderID = -1;
        int orderNo = -1;
        int menuItemID = -1;
        float price = 0;

        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the date and time format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Format the date and time
        String _date = currentDateTime.format(dateFormatter);
        String _time = currentDateTime.format(timeFormatter);

        try {
            Connection conn = getSQLConnection();

            // String orderIDQuery = "SELECT MAX(orderID), MAX(orderNo) FROM sales";
            String orderIDQuery = "SELECT * FROM sales WHERE orderID = (SELECT MAX(orderID) FROM sales)";

            // String orderIDQuery = "SELECT MAX(orderID) as orderid, MAX(orderNo) as orderno FROM sales";
            try(PreparedStatement orderIDStatement = conn.prepareStatement(orderIDQuery)) {
                ResultSet resultSet = orderIDStatement.executeQuery();
                while (resultSet.next()) {
                    orderID = resultSet.getInt("orderid");
                    orderNo = resultSet.getInt("orderNo");

                    System.out.println("orderID: " + orderID + "\norderNo: " + orderNo);
                }

                orderID++;
                orderNo++;
            } catch(SQLException e) {
                System.out.println("Error accessing order number.");
                e.printStackTrace();
            }

            for (Drink d : cart) {
                String menuIDQuery = "SELECT menuItemID FROM menuItems WHERE menuItemName = ?";
                try(PreparedStatement orderStatement = conn.prepareStatement(menuIDQuery)) {
                    orderStatement.setString(1, d.name);
                    ResultSet resultSet = orderStatement.executeQuery();
                    while(resultSet.next()) {
                        menuItemID = resultSet.getInt("menuItemID");
                    }
                } catch(SQLException e) {
                    System.out.println("Error getting menu item ID");
                    e.printStackTrace();
                }

                // run insert sql command
                String placeOrderSQL = "INSERT INTO sales (orderid, orderNo, saleDate, saleTime, employeeID, salePrice, isLarge, menuItemID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement orderStatement = conn.prepareStatement(placeOrderSQL)) {
                    orderStatement.setInt(1, orderID);
                    orderStatement.setInt(2, orderNo);

                    java.sql.Date sqlDate = java.sql.Date.valueOf(_date); // assuming _date is in "YYYY-MM-DD" format
                    java.sql.Time sqlTime = java.sql.Time.valueOf(_time); // assuming _time is in "HH:MM:SS" format
                    orderStatement.setDate(3, sqlDate);
                    orderStatement.setTime(4, sqlTime);

                    orderStatement.setInt(5, 0); // TODO fix the employeeID
                    orderStatement.setFloat(6, d.price);
                    orderStatement.setBoolean(7, d.isLarge);
                    orderStatement.setInt(8, menuItemID);

                    orderStatement.executeUpdate();
                    System.out.println("Successfully placed order");
                } catch(SQLException e) {
                    System.out.println("Error placing order");
                    e.printStackTrace();
                }

                orderID++; // orderID is a primary key and must be unique
            }

        } catch (SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
       /*
        finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
       */

        cart.clear();
    }
}
