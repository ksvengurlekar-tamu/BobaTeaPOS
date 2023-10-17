package green.gongchapos.managerView;

import green.gongchapos.cashierView.CashierViewController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static green.gongchapos.GongCha.getSQLConnection;


/**
 * Class for the ManagerViewController, which controls the managerView.fxml file and holds all
 * attributes and methods for the manager view of the GongChaPOS system.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class ManagerViewController extends CashierViewController {
    public StackPane drinkStackPane;
    public HBox ingredient1Box;
    AutoCompleteTextBox autoCompleteIngredient1 = new AutoCompleteTextBox();
    public TextField ingredient1Quantity;
    public HBox ingredient2Box;
    AutoCompleteTextBox autoCompleteIngredient2 = new AutoCompleteTextBox();
    public TextField ingredient2Quantity;
    public HBox ingredient3Box;
    AutoCompleteTextBox autoCompleteIngredient3 = new AutoCompleteTextBox();
    public TextField ingredient3Quantity;
    public HBox ingredient4Box;
    AutoCompleteTextBox autoCompleteIngredient4 = new AutoCompleteTextBox();
    public TextField ingredient4Quantity;
    public HBox ingredient5Box;
    AutoCompleteTextBox autoCompleteIngredient5 = new AutoCompleteTextBox();
    public TextField ingredient5Quantity;
    AutoCompleteTextBox autoCompleteDrinkName = new AutoCompleteTextBox();
    public TextField menuItemPrice;
    public TextField menuItemCalories;
    public TextField menuItemCategory;
    public TextField hasCaffeine;
    public String color = "";
    public HBox drinkNameHbox;
    public Pane addDrinkPane;


    /** Initializes the ManagerViewController and sets up the user interface.
    * 
    * This method is called when the Manager View is loaded and initializes various UI elements,
    * including auto-complete text boxes and a real-time clock for displaying the current time.
    */
    @Override
    public void initialize() {
        this.backButton(null);
        drinkNameHbox.getChildren().add(autoCompleteDrinkName);
        ingredient1Box.getChildren().add(autoCompleteIngredient1);

        ingredient2Box.getChildren().add(autoCompleteIngredient2);
        ingredient3Box.getChildren().add(autoCompleteIngredient3);
        ingredient4Box.getChildren().add(autoCompleteIngredient4);
        ingredient5Box.getChildren().add(autoCompleteIngredient5);

        Platform.runLater(() -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

            Font.loadFont(getClass().getResourceAsStream("Amerigo BT.ttf"), 14);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                Date currentTime = new Date();
                String formattedTime = dateFormat.format(currentTime);

                Time.setStyle("-fx-font-size: 20;");
                Time.setText(formattedTime);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });
    }


    /** Handles the "Inventory" button click event by navigating to the inventory view.
    * 
    * @param actionEvent The event triggered by clicking the "Inventory" button.
    * @throws IOException If there is an issue with loading the inventory view.
    */
    public void inventoryClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/green/gongchapos/managerView/inventoryView/inventoryView.fxml"));

        Scene inventoryScene = new Scene(loader.load());
        InventoryViewController controller = loader.getController();
        controller.setInvViewController(cashierViewStage);
        controller.displayTable(false);

        cashierViewStage.setScene(inventoryScene);
        cashierViewStage.show();
    }


    /** Handles the click event for series buttons, displaying a series of drinks in a sub-pane.
    * 
    * This method populates the sub-pane with drink buttons based on the selected series.
    *
    * @param event The event triggered by clicking a series button.
    */
    @Override
    public void seriesPress(ActionEvent event) {

        subDrinkPane.getChildren().clear();

        mainMenuPane.setDisable(true);
        mainMenuPane.setVisible(false);

        drinkStackPane.setDisable(false);
        drinkStackPane.setVisible(true);
        drinkStackPane.setOpacity(1);


        drinkPane.setDisable(false);
        drinkPane.setVisible(true);
        drinkPane.setOpacity(1);

        addDrinkPane.setVisible(false);
        addDrinkPane.setDisable(true);


        int start_count = generalSeriesPress(event);
        int count = start_count;

        while (count != 20) {
            Button drinkButton = new Button();
            drinkButton.setMinSize(161, 120);
            drinkButton.setMaxSize(161, 120);
            drinkButton.setStyle("-fx-background-color: #ffffff;");
            if (count == start_count) {
                drinkButton.setText("+");
                drinkButton.setStyle(
                        "-fx-background-color: #eeeeee;" +
                        "-fx-font-size: 50px;" +
                        "-fx-font-weight: 900;"
                );

                drinkButton.setOnAction(event2 -> {
                    try {
                        addDrink(event2);
                    } catch (SQLException e) {
                        e.printStackTrace(); // or handle the exception in some other manner
                    }
                });

                drinkButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        // Mouse is hovering over the button
                        drinkButton.setStyle("-fx-background-color: #eeeeee;" + "-fx-border-color: #0099ff;" + "-fx-border-width: 2px;" + "-fx-cursor: hand;");
                    } else {
                        // Mouse is not hovering over the button
                        drinkButton.setStyle("-fx-background-color: #eeeeee;" + "; -fx-border-width: 0px;");
                    }
                });
            }
            subDrinkPane.getChildren().add(drinkButton);
            count++;
        }
    }


    /** Navigates back to the main menu stage once we're in the Series Press stage.
    *
    * This method is called when the "Back" button is clicked, returning to the main menu view.
    *
    * @param actionEvent The event triggered by clicking the "Back" button.
    */
    @Override
    public void backButton(ActionEvent actionEvent) {
        mainMenuPane.setDisable(false);
        mainMenuPane.setVisible(true);

        drinkPopUp.setDisable(true);
        drinkPopUp.setVisible(false);
        drinkPopUp.setOpacity(0);
        drinkStackPane.setDisable(true);
        drinkStackPane.setVisible(false);
        seriesNameText.setText("Bubble Tea Series");

        autoCompleteDrinkName.setText("");
        menuItemPrice.setText("");
        menuItemCalories.setText("");
        seriesNameText.setText("");
        hasCaffeine.setText("");
    }


    /** Allows the manager to add a new drink item to the menu, specifying ingredients and details.
    * 
    * This method displays a form for adding a new drink item to the menu.
    *
    * @param actionEvent The event triggered by clicking the "+" button to add a drink item.
    * @throws SQLException If there is an issue with the database connection or SQL queries.
    */
    private void addDrink(ActionEvent actionEvent) throws SQLException {
        addDrinkPane.setDisable(false);
        addDrinkPane.setVisible(true);

        drinkPane.setDisable(true);
        drinkPane.setOpacity(0.5);

        ArrayList<String> drinkList = new ArrayList<>();

        try {
            Connection conn = getSQLConnection();

            String query = "SELECT menuItemName FROM menuItems WHERE menuitemcategory = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, seriesNameText.getText().replace(" Series", ""));
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                drinkList.add(resultSet.getString("menuItemName"));
            }

        } catch(SQLException e) {
//            System.out.println("Error accessing database.");
        }

        autoCompleteDrinkName.getEntries().addAll(drinkList);

        menuItemCategory.setText(seriesNameText.getText());

        // need complete list of ingredients for autofill
        ArrayList<String> ingredientList = new ArrayList<>();

        try {
            Connection conn = getSQLConnection();

            String query = "SELECT inventoryname FROM inventory";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                ingredientList.add(resultSet.getString("inventoryname"));
            }

        } catch(SQLException e) {
        //    System.out.println("Error accessing database.");
        }

        autoCompleteIngredient1.getEntries().addAll(ingredientList);
        autoCompleteIngredient2.getEntries().addAll(ingredientList);
        autoCompleteIngredient3.getEntries().addAll(ingredientList);
        autoCompleteIngredient4.getEntries().addAll(ingredientList);
        autoCompleteIngredient5.getEntries().addAll(ingredientList);
    }


    /** Handles the submission of a new menu item or an updated menu item. The user
    * interface is also changed accordingly.
    * 
    * This method is responsible for processing the submission of a new menu item
    * or an updated menu item by checking for input validation and then adding/updating
    * the database.
    *
    * @param actionEvent The event triggered by clicking the "Submit" button.
    */
    @FXML
    private void submitMenu(ActionEvent actionEvent) {
        Alert alert;

        if (autoCompleteDrinkName.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR, "Please enter a menu item name.");
            alert.show();
            return;
        }

        int itemID = 0;
        try (Connection conn = getSQLConnection()) {
            String query = "SELECT * FROM menuItems WHERE menuItemName = ?";

            PreparedStatement findName = conn.prepareStatement(query);
            findName.setString(1, autoCompleteDrinkName.getText());
            ResultSet rs = findName.executeQuery();

            // query for item name here
            while (rs.next()) {
                if (rs.getString("menuitemName").equals(autoCompleteDrinkName.getText())) {
                    helperUpdateItem();
                    this.backButton(null);
                    return;
                }
            }

            // query id to add 1 to; incrememnt menuitemID by 1 when adding new item
            itemID = 88888;
            PreparedStatement findBiggestID = conn.prepareStatement("SELECT MAX(menuitemid) AS maxID FROM menuitems");
            ResultSet resultSet = findBiggestID.executeQuery();
            while (resultSet.next()) {
                itemID = resultSet.getInt("maxID") + 1;
            }

            int ingredient1 = helperInventoryQuery(autoCompleteIngredient1, ingredient1Quantity);
            int ingredient2 = helperInventoryQuery(autoCompleteIngredient2, ingredient2Quantity);
            int ingredient3 = helperInventoryQuery(autoCompleteIngredient3, ingredient3Quantity);
            int ingredient4 = helperInventoryQuery(autoCompleteIngredient4, ingredient4Quantity);
            int ingredient5 = helperInventoryQuery(autoCompleteIngredient5, ingredient5Quantity);

            if (ingredient1 == -1 || ingredient2 == -1 || ingredient3 == -1 || ingredient4 == -1 || ingredient5 == -1) {
                return;
            }

            Random rand = new Random();
            String[] hexColorArray = {"#FF5733", "#00AABB", "#FFCC00", "#9933FF", "#33CC33"};

            // insert new inventory item
            PreparedStatement insertItem = conn.prepareStatement("INSERT INTO menuitems (menuitemID, menuItemName, menuItemPrice, menuItemCalories, menuItemCategory, hasCaffeine, color) VALUES (?, ?, ?, ?, ?, ?, ?)");
            insertItem.setInt(1, itemID);
            insertItem.setString(2, autoCompleteDrinkName.getText());
            insertItem.setFloat(3, Float.parseFloat(menuItemPrice.getText()));
            insertItem.setString(4, menuItemCalories.getText());
            insertItem.setString(5, menuItemCategory.getText().replace(" Series", ""));
            insertItem.setBoolean(6, Boolean.parseBoolean(hasCaffeine.getText()));
            insertItem.setString(7, hexColorArray[rand.nextInt(5)]); // for color: need to randomly select a #rgb
            insertItem.executeUpdate();

            alert = new Alert(Alert.AlertType.INFORMATION, "Item added to " + menuItemCategory.getText() + " Menu Items.");
            alert.show();

            // updates the drinks
            helperAddToJunction(itemID, ingredient1, ingredient1Quantity);
            helperAddToJunction(itemID, ingredient2, ingredient2Quantity);
            helperAddToJunction(itemID, ingredient3, ingredient3Quantity);
            helperAddToJunction(itemID, ingredient4, ingredient4Quantity);
            helperAddToJunction(itemID, ingredient5, ingredient5Quantity);

            this.backButton(null);
        } catch (SQLException e) {
            // System.out.println("Error accessing database.");
            e.printStackTrace();
        }

        autoCompleteDrinkName.setText("");
        menuItemPrice.setText("");
        menuItemCalories.setText("");
        seriesNameText.setText("");
        hasCaffeine.setText("");
        autoCompleteIngredient1.clear();
        autoCompleteIngredient2.clear();
        autoCompleteIngredient3.clear();
        autoCompleteIngredient4.clear();
        autoCompleteIngredient5.clear();
    }


    /** Helper method for updating an existing menu item's details in the inventory.
    * 
    * This method is used to update an existing menu item with modified information.
    * It executes an SQL update query to modify the item's price, calories, category, caffeine status, and color.
    */
    private void helperUpdateItem () {
        Alert alert;
        try (Connection conn = getSQLConnection()) {
            PreparedStatement insertItem = conn.prepareStatement("UPDATE menuitems SET menuItemPrice = ?, menuItemCalories = ?, menuItemCategory = ?, hasCaffeine = ?, color = ? WHERE menuItemName = ?");

            insertItem.setFloat(1, Float.parseFloat(menuItemPrice.getText()));

            Random rand = new Random();
            String[] hexColorArray = {"#FF5733", "#00AABB", "#FFCC00", "#9933FF", "#33CC33"};

            insertItem.setString(2, menuItemCalories.getText());
            insertItem.setString(3, menuItemCategory.getText().replace(" Series", ""));
            insertItem.setBoolean(4, Boolean.parseBoolean(hasCaffeine.getText()));
            insertItem.setString(5, hexColorArray[rand.nextInt(5)]); // for color: need to randomly select a #rgb
            insertItem.setString(6, autoCompleteDrinkName.getText());
            insertItem.executeUpdate();

            alert = new Alert(Alert.AlertType.INFORMATION, "Item updated to " + menuItemCategory.getText() + " Menu Items.");
            alert.show();
        }
        catch (SQLException e) {
//            System.out.println("Error accessing database.");
            e.printStackTrace();
        }

        autoCompleteDrinkName.setText("");
        menuItemPrice.setText("");
        menuItemCalories.setText("");
        seriesNameText.setText("");
        hasCaffeine.setText("");
        autoCompleteIngredient1.clear();
        autoCompleteIngredient2.clear();
        autoCompleteIngredient3.clear();
        autoCompleteIngredient4.clear();
        autoCompleteIngredient5.clear();
    }


    /** Helper method for querying the inventory to obtain an ingredient's ID.
    * 
    * This method queries the database to find the ID of a given ingredient based on its name.
    * If the ingredient name is not found in the inventory, an error is displayed.
    *
    * @param ingredientName The name of the ingredient to query.
    * @param quantity The quantity associated with the ingredient.
    * @return The ID of the ingredient in the inventory, or -1 if the ingredient is not found.
    */
    private int helperInventoryQuery(AutoCompleteTextBox ingredientName, TextField quantity) {
        int inventoryID = -2;
        if (!ingredientName.getText().isEmpty() && !quantity.getText().isEmpty()) {
            try (Connection conn = getSQLConnection()) {
                String inventoryIDQuery = "Select inventoryID FROM inventory WHERE inventoryName = ?";
                PreparedStatement stmt = conn.prepareStatement(inventoryIDQuery);
                stmt.setString(1, ingredientName.getText());

                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    inventoryID = resultSet.getInt("inventoryID");
                }
                else {
                    throw new SQLException("No ingredient " + ingredientName.getText() + " in inventory");
                }


            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.show();
                return -1;

            }
        }
        return inventoryID;
    }


    /** Helper method for adding an item to the junction table connecting menu items and ingredients.
    * 
    * This method inserts a new record into the junction table to associate a menu item with ingredients and their quantities.
    *
    * @param menuItemid The ID of the menu item to associate with ingredients.
    * @param inventoryID The ID of the ingredient in the inventory.
    * @param quantity The quantity of the ingredient associated with the menu item.
    */
    private void helperAddToJunction(int menuItemid, int inventoryID, TextField quantity) {
        if(inventoryID == -2) {
            return;
        }
        try {
            Connection conn = getSQLConnection();
            String addToJunctionQuery = "INSERT INTO menuitems_inventory (menuitemID, inventoryID, measurement) VALUES (?, ?, ?)";
            PreparedStatement stmt2 = conn.prepareStatement(addToJunctionQuery);
            stmt2.setInt(1, menuItemid);
            stmt2.setInt(2, inventoryID);
            stmt2.setFloat(3, Float.parseFloat(quantity.getText()));
            stmt2.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
