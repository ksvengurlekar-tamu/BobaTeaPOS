package green.gongchapos.cashierView;

import green.gongchapos.LoginController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import java.io.IOException;
import java.util.Objects;

import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static green.gongchapos.GongCha.getSQLConnection;


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
    public Label Time;

    @FXML
    public ListView<Drink> cartView;
    public VBox cartPane;
    public Text taxNumber;
    public Text subTotalNumber;
    public Text totalNumber;
    public boolean isPaneVisible = false; // Initial visibility state

    // Global drink adding variables
    public float price = 0;
    public String name = "";
    public boolean isLarge = false;

    // Global total variables
    public float subtotal = 0;
    public float tax = 0;
    public HBox topLeftHBox;
    public HBox topRightHBox;
    public HBox bottomHBox;
    public Text seriesNameText;

    // Customize button values
    public Button mediumSize;
    public Button largeSize;
    public Button noIce;
    public Button lightIce;
    public Button regularIce;
    public Button extraIce;
    public Button zeroSugar;
    public Button quarterSugar;
    public Button halfSugar;
    public Button normalSugar;
    public Button tapiocaPearls;
    public Button pudding;
    public Button herbalJelly;
    public Button whitePearls;
    public Button oreoCrumbs;
    public Button coconutJelly;
    public Button milkFoam;
    public Button basilSeeds;
    public Button aiyuJelly;
    public String seriesName = "";

    // Toppings in cart
    public float toppingPrice = 0;
    public String toppingName = "";
    public boolean toppingAdded = false;

    public ArrayList<Drink> cart = new ArrayList<>();


    /** Initializes the application's user interface by setting up a digital clock
     * that displays the current time, updating every second.
     *
     * This method should be called when the application starts.
     */
    public void initialize() {
        Platform.runLater(() -> {
            // Create a SimpleDateFormat to format the time
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

            Font.loadFont(getClass().getResourceAsStream("Amerigo BT.ttf"), 14);

            // Use a Timeline to update the time label every second
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                Date currentTime = new Date();
                String formattedTime = dateFormat.format(currentTime);

                Time.setStyle("-fx-font-size: 20;"); // Change the size here
                Time.setText(formattedTime);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });
    }


    /** Sets the Cashier View controller's primary stage to the cashier view.
     *
     * @param primaryStage The stage that is associated with the Cashier View.
     */
    public void setCashierViewController(Stage primaryStage) { this.cashierViewStage = primaryStage; }


    /** Drink class acts as a struct for a menu item that is being ordered
     * Contains a name, boolean to represent if it's large, and price
     *
     * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
     */
    public static class Drink {
        String name;

        String Topping;
        boolean isLarge;
        float price;

        Drink (String name, String Topping, boolean isLarge, float price) {
            this.name = name;
            this.Topping = Topping;
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


    /** Populates the user interface with a list of drinks based on the
     * series name that is clicked.
     *
     * @param event The Action Event trigged by the button click.
     * @return count The number of menu items in the Gong Cha database.
     */
    protected int generalSeriesPress(ActionEvent event) {
        subDrinkPane.getChildren().clear();

        Button sourceButton = (Button) event.getSource();
        seriesName = sourceButton.getText();
        seriesNameText.setText(seriesName + " Series");
        int count = 0;
        Boolean inStock = false;

        try {
            Connection conn = getSQLConnection();
            String getDrinks = "SELECT * FROM menuItems WHERE menuItemCategory = ?";
            try(PreparedStatement drinkStatement = conn.prepareStatement(getDrinks)) {
                drinkStatement.setString(1, seriesName);
                ResultSet resultSet = drinkStatement.executeQuery();

                subDrinkPane.setAlignment(Pos.TOP_LEFT);
                subDrinkPane.setHgap(10.0);
                subDrinkPane.setVgap(10.0);
                subDrinkPane.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));

                while(resultSet.next()) {
                    inStock = resultSet.getBoolean("menuiteminstock");

                    Text text = new Text(resultSet.getString("menuItemName"));
                    if(!inStock){
                        text.setStyle("-fx-opacity: 0.3");
                    }
                    text.setTextAlignment(TextAlignment.CENTER);
                    Button drinkButton = new Button();
                    drinkButton.setMinSize(161, 120);
                    drinkButton.setMaxSize(161, 120);
                    text.wrappingWidthProperty().bind(drinkButton.widthProperty());
                    drinkButton.setGraphic(text);

                    String drinkColor;
                    if (inStock) {
                        drinkColor = resultSet.getString("color");
                    } else {
                        drinkColor = "rgba(192, 192, 192, 0.7);";
                    }
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
                            // System.out.println("Error getting drinks.");
                            e.printStackTrace();
                        }

                        drinkPane.setDisable(true);
                        drinkPane.setOpacity(0.5);
                        bottomHBox.setDisable(true);
                        bottomHBox.setOpacity(0.5);
                        topRightHBox.setDisable(true);
                        topRightHBox.setOpacity(0.5);
                        topLeftHBox.setDisable(true);
                        topLeftHBox.setOpacity(0.5);

                        drinkPopUp.setDisable(false);
                        drinkPopUp.setVisible(true);
                        drinkPopUp.setOpacity(1);
                    });

                    drinkButton.setStyle (
                            "-fx-background-color: " + drinkColor + "; "
                    );

                    if (inStock) {
                        drinkButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {
                                // Mouse is hovering over the button
                                drinkButton.setStyle("-fx-background-color: " + drinkColor + "; -fx-border-color: #0099ff;" + "-fx-border-width: 2px;" + "-fx-cursor: hand;");
                            } else {
                                // Mouse is not hovering over the button
                                drinkButton.setStyle("-fx-background-color: " + drinkColor + "; -fx-border-width: 0px;");
                            }
                        });
                    }
                    subDrinkPane.getChildren().add(drinkButton);
                    count++;

                }

            } catch (SQLException e) {
                // System.out.println("Error getting drinks.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            // System.out.println("Error accessing database.");
            e.printStackTrace();
        }
        return count;
    }


    /** Handle the action triggered by selecting a series (e.g., category) of drinks.
     *
     * This method is responsible for populating a UI element (likely a container)
     * with buttons and text elements representing drink items fetched from a database.
     * It is typically invoked when a series button (e.g., category button) is clicked,
     * triggering an ActionEvent.
     *
     * @param event The ActionEvent triggered by selecting a series button.
     */
    @FXML
    public void seriesPress(ActionEvent event) {
        mainMenuPane.setDisable(true);
        mainMenuPane.setVisible(false);

        drinkPane.setDisable(false);
        drinkPane.setVisible(true);
        int count = generalSeriesPress(event);

        while(count != 20) {
            Button drinkButton = new Button();
            drinkButton.setMinSize(161, 120);
            drinkButton.setMaxSize(161, 120);
            drinkButton.setStyle("-fx-background-color: #ffffff;");
            subDrinkPane.getChildren().add(drinkButton);
            count++;
        }
    }


    /** Reverts the state of the drink pop-up customization interface back to the previous stage.
     *
     * @param event The ActionEvent that triggers this event is a click of the back button when on the drink customization.
     */
    public void drinkPopUpBack(ActionEvent event) {
        drinkPane.setDisable(false);
        drinkPane.setOpacity(1);
        bottomHBox.setDisable(false);
        bottomHBox.setOpacity(1);
        topRightHBox.setDisable(false);
        topRightHBox.setOpacity(1);
        topLeftHBox.setDisable(false);
        topLeftHBox.setOpacity(1);

        drinkPopUp.setDisable(true);
        drinkPopUp.setVisible(false);
        drinkPopUp.setOpacity(0);
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

        drinkPopUp.setDisable(true);
        drinkPopUp.setVisible(false);
        drinkPopUp.setOpacity(0);
        drinkPane.setDisable(true);
        drinkPane.setVisible(false);
        seriesNameText.setText("Bubble Tea Series");
    }


    /** Finds the corresponding topping menu item from the button selected and calculates the
     *  running total of the price into the global.
     *
     *  @param actionEvent The action event triggered by the button press of a topping button.
     */
    public void toppingButton(ActionEvent actionEvent) {
        if (toppingAdded) {
            price -= toppingPrice;
            toppingAdded = false;
        }

        Button sourceButton = (Button) actionEvent.getSource();
        toppingName = sourceButton.getText();

        tapiocaPearls.getStyleClass().clear();
        pudding.getStyleClass().clear();
        herbalJelly.getStyleClass().clear();
        whitePearls.getStyleClass().clear();
        oreoCrumbs.getStyleClass().clear();
        coconutJelly.getStyleClass().clear();
        milkFoam.getStyleClass().clear();
        basilSeeds.getStyleClass().clear();
        aiyuJelly.getStyleClass().clear();

        tapiocaPearls.getStyleClass().add("popupButton");
        pudding.getStyleClass().add("popupButton");
        herbalJelly.getStyleClass().add("popupButton");
        whitePearls.getStyleClass().add("popupButton");
        oreoCrumbs.getStyleClass().add("popupButton");
        coconutJelly.getStyleClass().add("popupButton");
        milkFoam.getStyleClass().add("popupButton");
        basilSeeds.getStyleClass().add("popupButton");
        aiyuJelly.getStyleClass().add("popupButton");
        sourceButton.getStyleClass().remove("popupButton");
        sourceButton.getStyleClass().add("popupButtonDef");

        try {
            Connection conn = getSQLConnection();
            String getTopping = "SELECT menuitemprice FROM menuItems WHERE menuitemname = ?";
            try(PreparedStatement toppingStatement = conn.prepareStatement(getTopping)) {
                toppingStatement.setString(1, toppingName);
                ResultSet resultSet = toppingStatement.executeQuery();
                while(resultSet.next()) {
                    toppingPrice = resultSet.getFloat("menuitemprice");
                    price += toppingPrice;
                    toppingAdded = true;
                }
                // System.out.println(price);
            } catch(SQLException e) {
                // System.out.println("Error getting toppings.");
                e.printStackTrace();
            }
        } catch(SQLException e) {
            // System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }


    /** Resets the topping selection so that it is cleared for the
     * next drink that may or may not be added to the cart.
     *
     * This method clears confusion for the cashier in case the next drink does not
     * include a topping.
     */
    private void resetToppingSelection() {
        toppingName = "";
        toppingAdded = false;

        tapiocaPearls.getStyleClass().clear();
        pudding.getStyleClass().clear();
        herbalJelly.getStyleClass().clear();
        whitePearls.getStyleClass().clear();
        oreoCrumbs.getStyleClass().clear();
        coconutJelly.getStyleClass().clear();
        milkFoam.getStyleClass().clear();
        basilSeeds.getStyleClass().clear();
        aiyuJelly.getStyleClass().clear();

        tapiocaPearls.getStyleClass().add("popupButton");
        pudding.getStyleClass().add("popupButton");
        herbalJelly.getStyleClass().add("popupButton");
        whitePearls.getStyleClass().add("popupButton");
        oreoCrumbs.getStyleClass().add("popupButton");
        coconutJelly.getStyleClass().add("popupButton");
        milkFoam.getStyleClass().add("popupButton");
        basilSeeds.getStyleClass().add("popupButton");
        aiyuJelly.getStyleClass().add("popupButton");
    }


    /** Handles the action event triggered by the button press of the "Large" button during drink customization.
     *
     * This method is responsible for updating the item size and adjusting the price based on the selection
     * of the "Large" option. It detects the button that triggered the event and increases the item price and
     * sets the size to large.
     *
     * @param actionEvent The action event triggered by the button press of large button.
     */
    public void isLarge(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();
        String size = sourceButton.getText();

        mediumSize.getStyleClass().clear();
        largeSize.getStyleClass().clear();

        mediumSize.getStyleClass().add("popupButton");
        largeSize.getStyleClass().add("popupButton");

        if (size.contains("Large") && !isLarge) {
            price += 0.75F;
            isLarge = true;
            largeSize.getStyleClass().remove("popupButton");
            largeSize.getStyleClass().add("popupButtonDef");
        } else if (!size.contains("Large") && isLarge) {
            isLarge = false;
            price -= 0.75F;
            mediumSize.getStyleClass().remove("popupButton");
            mediumSize.getStyleClass().add("popupButtonDef");
        } else if (size.contains("Large") && isLarge) {
            isLarge = true;
            largeSize.getStyleClass().remove("popupButton");
            largeSize.getStyleClass().add("popupButtonDef");
        } else if (!size.contains("Large") && !isLarge) {
            isLarge = false;
            mediumSize.getStyleClass().remove("popupButton");
            mediumSize.getStyleClass().add("popupButtonDef");
        }
        // System.out.println(price);
    }


    /** Handles selecting a different ice level and updates the user
     * interface to avoid confusion for the cashier/manager.
     *
     * This method is responsible for managing the interactions between the
     * button press of the different ice levels and the interface.
     *
     * @param actionEvent This ActionEvent is triggered by the button press of
     * one of the ice level buttons.
     */
    public void iceLevelButton(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();
        String iceLevel = sourceButton.getText();

        noIce.getStyleClass().clear();
        lightIce.getStyleClass().clear();
        regularIce.getStyleClass().clear();
        extraIce.getStyleClass().clear();

        noIce.getStyleClass().add("popupButton");
        lightIce.getStyleClass().add("popupButton");
        regularIce.getStyleClass().add("popupButton");
        extraIce.getStyleClass().add("popupButton");

        sourceButton.getStyleClass().remove("popupButton");
        sourceButton.getStyleClass().add("popupButtonDef");
    }


    /** Handles selecting a different sugar level and updates the user
     * interface to avoid confusion for the cashier/manager.
     *
     * This method is responsible for managing the interactions between the
     * button press of the different sugar levels and the interface.
     *
     * @param actionEvent This ActionEvent is triggered by the button press of
     * one of the sugar level buttons.
     */
    public void sugarLevelButton(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();
        String sugarLevel = sourceButton.getText();

        zeroSugar.getStyleClass().clear();
        quarterSugar.getStyleClass().clear();
        halfSugar.getStyleClass().clear();
        normalSugar.getStyleClass().clear();

        zeroSugar.getStyleClass().add("popupButton");
        quarterSugar.getStyleClass().add("popupButton");
        halfSugar.getStyleClass().add("popupButton");
        normalSugar.getStyleClass().add("popupButton");

        sourceButton.getStyleClass().remove("popupButton");
        sourceButton.getStyleClass().add("popupButtonDef");
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
        // System.out.println(price);
        cart.add(new Drink(name, toppingName, isLarge, price));

        drinkPane.setDisable(false);
        drinkPane.setOpacity(1);
        bottomHBox.setDisable(false);
        bottomHBox.setOpacity(1);
        topRightHBox.setDisable(false);
        topRightHBox.setOpacity(1);
        topLeftHBox.setDisable(false);
        topLeftHBox.setOpacity(1);

        drinkPopUp.setDisable(true);
        drinkPopUp.setVisible(false);

        Text drinkNameText = new Text(name);
        Text drinkPriceText = new Text(String.format("$%.2f", price-toppingPrice));

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

        VBox combinedVBox = new VBox(mainHBox);

        // Adding the topping if it's added
        if (toppingAdded) {
            Text toppingNameText = new Text(toppingName);

            // Indentation for toppingNameText
            toppingNameText.setTranslateX(20);

            Text toppingPriceText = new Text(String.format("+$%.2f", toppingPrice));  // Assuming you have toppingPrice variable

            // Setting style with opacity and indentation for the topping name
            toppingNameText.setStyle("-fx-font-size: 20px; -fx-opacity: 0.5; -fx-padding: 0 0 0 20px;");

            // Setting style with opacity for the topping price
            toppingPriceText.setStyle("-fx-font-size: 20px; -fx-opacity: 0.5;");

            HBox toppingNameBox = new HBox(toppingNameText);
            toppingNameBox.setAlignment(Pos.CENTER_LEFT);

            HBox toppingPriceBox = new HBox(toppingPriceText);
            toppingPriceBox.setAlignment(Pos.CENTER_RIGHT);

            Pane spacerForTopping = new Pane();
            HBox.setHgrow(spacerForTopping, Priority.ALWAYS);

            HBox toppingHBox = new HBox(toppingNameBox, spacerForTopping, toppingPriceBox);

            combinedVBox.getChildren().add(toppingHBox);
        }
        cartPane.getChildren().add(combinedVBox);

        totalCharge();

        price = 0;

        resetToppingSelection();
    }


    /** Updates the total charges, including the subtotal, tax, and total, based
     * on the current price.
     *
     * This method performs the following: it adds the current price to subtotal,
     * updates the subtotal display field with the new subtotal value, calculates the
     * tax by multiplying by 6.25% sales tax in Texas, updates the overall total display
     * field with the calculations.
     */
    public void totalCharge() {
        subtotal += price;
        subTotalNumber.setText(String.format("%.2f", subtotal));
        tax += price * 0.0625;
        taxNumber.setText(String.format("%.2f", subtotal * 0.0625));
        totalNumber.setText(String.format("%.2f", subtotal * 1.0625));
    }


    /** Places an order for items in the cart into the sales database and updates inventory ingredients.
    *
    * This method retrieves necessary information from the cart, including
    * item details, and inserts them into the sales table in the database.
    * It also updates the order ID, ensuring uniqueness for each order, and
    * subtracts the used inventory ingredients from the inventory.
    *
    * @throws SQLException if there's an issue with the SQL database operations.
    */
    @FXML
    public void charge() {
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

                    // System.out.println("orderID: " + orderID + "\norderNo: " + orderNo);
                }

                orderID++;
                orderNo++;
            } catch(SQLException e) {
                // System.out.println("Error accessing order number.");
                e.printStackTrace();
            }

            for (Drink d : cart) {
                String menuIDQuery = "SELECT menuItemID FROM menuItems WHERE menuItemName = ?";
                try (PreparedStatement orderStatement = conn.prepareStatement(menuIDQuery)) {
                    orderStatement.setString(1, d.name);
                    ResultSet resultSet = orderStatement.executeQuery();
                    while (resultSet.next()) {
                        menuItemID = resultSet.getInt("menuItemID");
                        String measurementString = "SELECT * FROM menuItems_inventory WHERE menuitemid = ?";
                        PreparedStatement measurementSTMT = conn.prepareStatement(measurementString);
                        measurementSTMT.setInt(1, menuItemID);
                        ResultSet measurementSet = measurementSTMT.executeQuery();
                        while (measurementSet.next()) {
                            float quantity = measurementSet.getFloat("measurement");
                            int inventoryid = measurementSet.getInt("inventoryid");

                            System.out.println(quantity);
                            System.out.println(inventoryid);


                            String inventoryDecreaseString = "UPDATE inventory SET inventoryQuantity = inventoryQuantity - ? WHERE inventoryid = ?";
                            PreparedStatement inventoryDecreaseSTMT = conn.prepareStatement(inventoryDecreaseString);
                            if(d.isLarge){
                                quantity = quantity * 1.5F;
                            }
                            inventoryDecreaseSTMT.setFloat(1, quantity);
                            inventoryDecreaseSTMT.setInt(2, inventoryid);
                            inventoryDecreaseSTMT.executeUpdate();


                            String inventoryCheckString = "SELECT inventoryQuantity FROM inventory WHERE inventoryid = ?";
                            PreparedStatement inventoryCheckSTMT = conn.prepareStatement(inventoryCheckString);
                            inventoryCheckSTMT.setInt(1, inventoryid);
                            ResultSet inventoryCheckSet = inventoryCheckSTMT.executeQuery();
                            while (inventoryCheckSet.next()) {
                                if (inventoryCheckSet.getInt("inventoryQuantity") <= 50) { // Restock value, Could be changed later
                                    String inventoryInStockString = "UPDATE inventory SET inventoryinstock = ? WHERE inventoryid = ?";
                                    PreparedStatement inventoryInStockSTMT = conn.prepareStatement(inventoryInStockString);

                                    inventoryInStockSTMT.setBoolean(1, false);
                                    inventoryInStockSTMT.setInt(2, inventoryid);
                                    inventoryInStockSTMT.executeUpdate();
                                }
                                if (inventoryCheckSet.getInt("inventoryQuantity") <= 11) { // MAX measurement used
                                    String menuItemInStockString = "UPDATE menuitems SET menuiteminstock = ? WHERE menuitemid = ?";
                                    PreparedStatement menuItemInStockSTMT = conn.prepareStatement(menuItemInStockString);
                                    menuItemInStockSTMT.setBoolean(1, false);
                                    menuItemInStockSTMT.setInt(2, menuItemID);
                                    menuItemInStockSTMT.executeUpdate();
                                    backButton(null);
                                    break;
                                }
                            }
                        }

                    }
                } catch(SQLException e) {
                    // System.out.println("Error getting menu item ID.");
                    e.printStackTrace();
                }

                // Topping Decrement
                String toppingString = "UPDATE inventory SET inventoryQuantity = inventoryQuantity - ? WHERE inventoryName = ?";
                System.out.println(d.Topping);
                PreparedStatement toppingSTMT = conn.prepareStatement(toppingString);
                toppingSTMT.setFloat(1, 2);
                toppingSTMT.setString(2, d.Topping);
                toppingSTMT.executeUpdate();


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
                    // System.out.println("Successfully placed order");
                } catch(SQLException e) {
                    // System.out.println("Error placing order.");
                    e.printStackTrace();
                }

                orderID++; // orderID is a primary key and must be unique

                // Fetch ingredients and amounts used for the selected menu item
                String inventoryQuery = "SELECT mi.inventoryID, mi.measurement, i.inventoryName " +
                                        "FROM menuItems_Inventory mi " +
                                        "JOIN Inventory i ON mi.inventoryID = i.inventoryID " +
                                        "WHERE mi.menuItemID = ?";
                try (PreparedStatement inventoryStatement = conn.prepareStatement(inventoryQuery)) {
                    inventoryStatement.setInt(1, menuItemID);
                    ResultSet inventoryResult = inventoryStatement.executeQuery();
                    while (inventoryResult.next()) {
                        int inventoryID = inventoryResult.getInt("inventoryID");
                        float measurement = inventoryResult.getFloat("measurement");
                        String inventoryName = inventoryResult.getString("inventoryName");

                        // Update the inventory by subtracting the used amount
//                        updateInventoryQuantity(conn, inventoryID, measurement);

                        System.out.println("Used " + measurement + " of " + inventoryName);
                    }
                } catch (SQLException e) {
                    // System.out.println("Error updating inventory.");
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            // System.out.println("Error accessing database.");
            e.printStackTrace();
        }

        cartPane.getChildren().clear();
        cart.clear();

        subtotal = 0;
        subTotalNumber.setText(String.format("%.2f", subtotal));
        tax = 0;
        taxNumber.setText(String.format("%.2f", subtotal));
        totalNumber.setText(String.format("%.2f", subtotal));
    }


    /** Logs an employee out of the POS system.
     * This method logs an employee out when the logout button is pressed.
     * After logging out, an alert will pop up to let the employee know that
     * they have logged out successfully.
     *
     *  @param event The event triggered by the button press of the logout button.
     */
    @FXML
    public void logoutButton(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Successful logout");
        alert.setContentText("You have logged out successfully!");
        alert.showAndWait();

        Stage cashierStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        cashierStage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/green/gongchapos/Login.fxml"));
        Stage viewStage = new Stage();

        Scene logInScene = new Scene(loader.load());
        LoginController controller = loader.getController();
        controller.setLogInStage(viewStage);

        viewStage.setTitle("GongChaPOS");
        viewStage.setScene(logInScene);
        Image Logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/green/gongchapos/images/GongChaLogo.png")));
        viewStage.getIcons().add(Logo);
        viewStage.show();
    }
}
