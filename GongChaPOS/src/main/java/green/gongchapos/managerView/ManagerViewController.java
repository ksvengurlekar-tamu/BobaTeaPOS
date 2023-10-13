package green.gongchapos.managerView;

import green.gongchapos.cashierView.CashierViewController;
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
import java.util.Random;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static green.gongchapos.GongCha.getSQLConnection;


/** Class for the CashierViewController, which controls the cashierView.fxml file and holds all
 * attributes and methods for the cashier view of the GongChaPOS system.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class ManagerViewController extends CashierViewController {
    public StackPane drinkStackPane;
    // menu item view inputs

    AutoCompleteTextBox autoCompleteDrinkName = new AutoCompleteTextBox();
    public TextField menuItemPrice;
    public TextField menuItemCalories;
    public TextField menuItemCategory;
    public TextField hasCaffeine;
    public String color = "";
    public HBox drinkNameHbox;
    public Pane addDrinkPane;

    @Override
    public void initialize() {
        this.backButton(null);
        drinkNameHbox.getChildren().add(autoCompleteDrinkName);
    }

    public void inventoryClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/green/gongchapos/managerView/inventoryView/inventoryView.fxml"));

        Scene inventoryScene = new Scene(loader.load());
        InventoryViewController controller = loader.getController();
        controller.setInvViewController(cashierViewStage);
        controller.displayTable(false);

        cashierViewStage.setScene(inventoryScene);
        cashierViewStage.show();
    }

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


    private void addDrink(ActionEvent event) throws SQLException {
        addDrinkPane.setDisable(false);
        addDrinkPane.setVisible(true);

        drinkPane.setDisable(true);
        drinkPane.setOpacity(0.5);

        ArrayList<String> drinkList = new ArrayList<String>();

        try {
            Connection conn = getSQLConnection();

            String query = "SELECT menuItemName FROM menuItems WHERE menuitemcategory = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, seriesNameText.getText().replace(" Series", ""));
//            System.out.println(seriesNameText.getText().replace(" Series", ""));
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                drinkList.add(resultSet.getString("menuItemName"));
            }

        } catch(SQLException e) {
//            System.out.println("Error accessing database.");
        }

        autoCompleteDrinkName.getEntries().addAll(drinkList);

        menuItemCategory.setText(seriesNameText.getText());

    }


    @FXML
    private void submitMenu(ActionEvent actionEvent) {
        Alert alert;

        if (autoCompleteDrinkName.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR, "Please enter a menu item name.");
            alert.show();
            return;
        }

        try (Connection conn = getSQLConnection()) {
            String query = "SELECT * FROM menuItems WHERE menuItemName = ?";

            PreparedStatement findName = conn.prepareStatement(query);
            findName.setString(1, autoCompleteDrinkName.getText());
            ResultSet rs = findName.executeQuery();

            // query for item name here
            while (rs.next()) {
                if (rs.getString("menuitemName").equals(autoCompleteDrinkName.getText())){
                    helperUpdateItem();
                    this.backButton(null);
                    return;
                }
            }


            // query id to add 1 to; incrememnt menuitemID by 1 when adding new item
            int itemID = 88888;
            PreparedStatement findBiggestID = conn.prepareStatement("SELECT MAX(menuitemid) AS maxID FROM menuitems");
            ResultSet resultSet = findBiggestID.executeQuery();
                while (resultSet.next()) {
                    itemID = resultSet.getInt("maxID");
                }
            Random rand = new Random();

            String[] hexColorArray = {"#FF5733", "#00AABB", "#FFCC00", "#9933FF", "#33CC33"};

            // insert new inventory item
            PreparedStatement insertItem = conn.prepareStatement("INSERT INTO menuitems (menuitemID, menuItemName, menuItemPrice, menuItemCalories, menuItemCategory, hasCaffeine, color) VALUES (?, ?, ?, ?, ?, ?, ?)");
            insertItem.setInt(1, itemID + 1);
            insertItem.setString(2, autoCompleteDrinkName.getText());
            insertItem.setFloat(3, Float.parseFloat(menuItemPrice.getText()));

            insertItem.setString(4, menuItemCalories.getText());
            insertItem.setString(5, menuItemCategory.getText().replace(" Series", ""));

            insertItem.setBoolean(6, Boolean.parseBoolean(hasCaffeine.getText()));
            insertItem.setString(7, hexColorArray[rand.nextInt(5)]); // for color: need to randomly select a #rgb
            insertItem.executeUpdate();
            alert = new Alert(Alert.AlertType.INFORMATION, "Item added to" + menuItemCategory.getText() + " Menu Items.");
            alert.show();

            // updates the drinks

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
    }


     /**
      * Helper method to update an existing item in the inventory; runs an update SQL query
      *
      */
    private void helperUpdateItem () {
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
    }
}
