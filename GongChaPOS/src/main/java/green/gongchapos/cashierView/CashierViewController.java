package green.gongchapos.cashierView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Label Time;

    @FXML
    public ListView<Drink> cartView;
    public VBox cartPane;
    public Text taxNumber;
    public Text subTotalNumber;
    public Text totalNumber;

    //public ListView<String> cartView;

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

    public float toppingPrice = 0;
    public String toppingName = "";
    public boolean toppingAdded = false;

    public ArrayList<Drink> cart = new ArrayList<>();


    public void initialize() {
        Platform.runLater(() -> {
            // Create a SimpleDateFormat to format the time
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

            // Use a Timeline to update the time label every second
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                Date currentTime = new Date();
                String formattedTime = dateFormat.format(currentTime);
                Time.setText(formattedTime);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });
    }


    public void setCashierViewController(Stage primaryStage) { this.cashierViewStage = primaryStage; }


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
    public void seriesPress(ActionEvent event) throws SQLException {
        subDrinkPane.getChildren().clear();

//        Scene scene = cashierViewStage.getScene();
        mainMenuPane.setDisable(true);
        mainMenuPane.setVisible(false);

        drinkPane.setDisable(false);
        drinkPane.setVisible(true);

        Button sourceButton = (Button) event.getSource();
        String seriesName = sourceButton.getText();
        seriesNameText.setText(seriesName + " Series");

        try {
            Connection conn = getSQLConnection();
            String getDrinks = "SELECT * FROM menuItems WHERE menuItemCategory = ?";
            try(PreparedStatement drinkStatment = conn.prepareStatement(getDrinks)) {
                drinkStatment.setString(1, seriesName);
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
                            System.out.println("Error getting drinks.");
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
                System.out.println("Error getting drinks.");
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
        drinkPopUp.setDisable(true);
        drinkPopUp.setVisible(false);
        drinkPopUp.setOpacity(0);
        
        drinkPane.setDisable(true);
        drinkPane.setVisible(false);
        seriesNameText.setText("Bubble Tea Series");
    }


    /** Finds the corresponding topping menu item from the button selected and calcultes the
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
                System.out.println(price);
            } catch(SQLException e) {
                System.out.println("Error getting toppings.");
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
        System.out.println(price);
    }


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
        System.out.println(price);
        cart.add(new Drink(name, isLarge, price));

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


    /**
    * Places an order for items in the cart into the sales database and updates inventory ingredients.
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
                    System.out.println("Error getting menu item ID.");
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
                    System.out.println("Error placing order.");
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
                        updateInventoryQuantity(conn, inventoryID, measurement);

                        System.out.println("Used " + measurement + " of " + inventoryName);
                    }
                } catch (SQLException e) {
                    System.out.println("Error updating inventory.");
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            System.out.println("Error accessing database.");
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

    /**
    * Update inventory quantities and in-stock status based on used amounts.
    *
    * @param conn         Connection to the database.
    * @param inventoryID  The ID of the inventory item to update.
    * @param usedAmount   The amount of inventory item used in the order.
    */
    private void updateInventoryQuantity(Connection conn, int inventoryID, float usedAmount) {
        String updateQuery = "UPDATE Inventory SET inventoryQuantity = inventoryQuantity - ? WHERE inventoryID = ?";
        //update inventory quantities and the inStock status
        try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
            updateStatement.setFloat(1, usedAmount);
            updateStatement.setInt(2, inventoryID);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Check if the inventory quantity is now 0
                String checkQuantityQuery = "SELECT inventoryQuantity FROM Inventory WHERE inventoryID = ?";
                try (PreparedStatement checkQuantityStatement = conn.prepareStatement(checkQuantityQuery)) {
                    checkQuantityStatement.setInt(1, inventoryID);
                    ResultSet quantityResult = checkQuantityStatement.executeQuery();

                    if (quantityResult.next()) {
                        int updatedQuantity = quantityResult.getInt("inventoryQuantity");

                        if (updatedQuantity == 0) {
                            // If the quantity is now 0, update inventoryInStock to false
                            String updateInStockQuery = "UPDATE Inventory SET inventoryInStock = false WHERE inventoryID = ?";
                            try (PreparedStatement inStockUpdateStatement = conn.prepareStatement(updateInStockQuery)) {
                                inStockUpdateStatement.setInt(1, inventoryID);
                                inStockUpdateStatement.executeUpdate();
                            }
                        }
                    }
                }

                System.out.println("Inventory updated successfully.");
            } else {
                System.out.println("No rows updated. Inventory not found or insufficient quantity.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating inventory.");
            e.printStackTrace();
        }
    }



    /** Logs an employee out of the POS system.
     * This method logs an employee out when the logout button is pressed.
     * After logging out, an alert will pop up to let the employee know that
     * they have logged out successfully.
     *
     *  @param event The event triggered by the button press of the logout button.
     */
    @FXML
    public void logoutButton(ActionEvent event) {
    //     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    //     alert.setTitle("Success");
    //     alert.setHeaderText("Successful logout");
    //     alert.setContentText("You have logged out successfully!");
    //     alert.showAndWait();

    //     FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));

    //     Image Logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/GongChaLogo.png")));
    //     viewStage.getIcons().add(Logo);

    //     Stage viewStage = new Stage();
    //     Scene scene = new Scene(fxmlLoader.load());

    //     LoginController controller = fxmlLoader.getController();
    //     viewStage.setTitle("GongChaPOS");
    //     viewStage.setScene(scene);
    //     controller.setLogInStage(viewStage);
    //     viewStage.show();
    }
}
