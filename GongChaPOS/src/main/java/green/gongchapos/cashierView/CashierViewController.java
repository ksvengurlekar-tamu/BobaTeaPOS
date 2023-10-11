package green.gongchapos.cashierView;

import green.gongchapos.dbSetup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static green.gongchapos.GongCha.getSQLConnection;
import static green.gongchapos.GongCha.main;


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
    
    public Pane drinkPopUp;

    private boolean isPaneVisible = false; // Initial visibility state

    private float price = 0;
    private String name = "";
    private boolean isLarge = false;



    public static class Drink {
        String name;
        boolean isLarge;
        float price;

        Drink (String name, boolean isLarge, float price) {
            this.name = name;
            this.isLarge = isLarge;
            this.price = price;
        }
    }

    // checkout cart of Drink data types; each add to order button press will add to this
    private ArrayList<Drink> cart = new ArrayList<>();
    private ObservableList<String> observableCart = FXCollections.observableArrayList(); // ObservableList for cart
    private ListView<String> cartView = new ListView<>(observableCart); // element for viewing cart data in GUI

    @FXML
    private String seriesName;

    public void setCashierViewController(Stage primaryStage) { this.cashierViewStage = primaryStage; }

    @FXML
    private void seriesPress(ActionEvent event) throws SQLException {
        subDrinkPane.getChildren().clear();
        Scene scene = cashierViewStage.getScene();
        mainMenuPane.setDisable(true);
        mainMenuPane.setVisible(false);

        drinkPane.setDisable(false);
        drinkPane.setVisible(true);


        Connection conn = getSQLConnection();
        Button sourceButton = (Button) event.getSource();
        String drinkName = sourceButton.getText();

        try {
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
                        // TODO FIX THIS
                        Text oneText = (Text) Button.lookup(".text");
                        String name = oneText.getText();
                        System.out.println(name);
                        String getOneDrink = "SELECT menuitemprice FROM menuItems WHERE menuItemName = ?";
                        try(PreparedStatement onedrinkStatment = conn.prepareStatement(getOneDrink)) {
                            onedrinkStatment.setString(1, name);
                            ResultSet drinkResultSet = onedrinkStatment.executeQuery();
                            while(drinkResultSet.next()){
                                price += drinkResultSet.getFloat("menuitemprice");
                            }
                            System.out.println(price);

                        }
                        catch(SQLException e){
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

                }
                while(count != 20) {
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

        }
        catch (Exception e){
            System.out.println("Error getting drinks");
            e.printStackTrace();
        }
    }

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

    public void backButton(ActionEvent actionEvent) {
        mainMenuPane.setDisable(false);
        mainMenuPane.setVisible(true);
        drinkPane.setDisable(true);
        drinkPane.setVisible(false);

    }

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
        }
        catch(SQLException e) {
            System.out.println("Error getting drinks");
            e.printStackTrace();
        }

    }
    public void isLarge(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();
        String size = sourceButton.getText();
        if(size.contains("Large")) {
            price += 0.75;
            isLarge = true;
        }
        else{
            isLarge = false;
        }
        System.out.println(price);

        // TODO: deselect large option

    }

    public void addButton(ActionEvent actionEvent) {

        System.out.println(price);
        cart.add(new Drink(name, isLarge, price));
        price = 0;

        mainMenuPane.setDisable(false);
        mainMenuPane.setVisible(true);
        mainMenuPane.setOpacity(1);

        drinkPopUp.setDisable(true);
        drinkPopUp.setVisible(false);

//        observableCart.setAll(cart);
//

    }

    private void totalCharge() {
        double totalNoTax = 0.0;
        double tax = totalNoTax * 0.0625;
        double totalWithTax = totalNoTax + tax;

        // TODO: Make this an event handler, activating placeOrder() after charge button pressed

    }

    private void placeOrder() {
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

            String orderIDQuery = "SELECT MAX(orderID), MAX(orderNo) FROM sales";
            try(PreparedStatement orderIDStatement = conn.prepareStatement(orderIDQuery)) {
                ResultSet resultSet = orderIDStatement.executeQuery();
                while(resultSet.next()) {
                    orderID = resultSet.getInt("orderID");
                    orderNo = resultSet.getInt("orderNo");
                }
            }

            for (Drink d : cart) {
                String menuIDQuery = "SELECT menuItemID FROM menuItems WHERE menuItemName = " + d.name;
                try(PreparedStatement orderStatement = conn.prepareStatement(menuIDQuery)) {
                    ResultSet resultSet = orderStatement.executeQuery();
                    while(resultSet.next()) {
                        menuItemID = resultSet.getInt("menuItemID");
                    }
                }
                catch(SQLException e) {
                    System.out.println("Error getting menu item ID");
                    e.printStackTrace();
                }

                // run insert sql command
                String placeOrderSQL = "INSERT INTO sales (" + orderID + ", " + orderNo + ", " + _date + ", " + _time +
                    ", " + d.price + ", " + d.isLarge + ", " + menuItemID + ")";
                try (PreparedStatement orderStatement = conn.prepareStatement(placeOrderSQL)) {
                    System.out.println("Successfully placed order");
                } catch(SQLException e){
                    System.out.println("Error placing order");
                    e.printStackTrace();
                }

                orderID++; // orderID is a primary key and must be unique
            }

        }
        catch (SQLException e) {
            System.out.println("Error accessing order number.");
            e.printStackTrace();
        }

        cart.clear();
    }
}
