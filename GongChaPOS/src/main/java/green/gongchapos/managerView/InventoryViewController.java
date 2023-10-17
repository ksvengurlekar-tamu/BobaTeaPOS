package green.gongchapos.managerView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.collections.FXCollections;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.sql.*;

import static green.gongchapos.GongCha.getSQLConnection;


/** Class for the InventoryViewController, which controls the inventoryView.fxml file and holds all
 * attributes and methods for the inventory view of the GongChaPOS system.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class InventoryViewController {

    @FXML
    // inventory table columns
    public Stage invViewStage;
    public AnchorPane inventoryPane;
    public Pane inventoryAddPane;
    public TextField itemName;
    public TextField quantity;
    public TextField dateReceived;
    public TextField expDate;
    public TextField inStock;
    public TextField supplier;

    // For sales report
    public HBox menuItemNameBox;
    public TableView excessTable;
    public Pane excessReportPane;
    public Pane restockPane;
    AutoCompleteTextBox autoCompleteDrinkName = new AutoCompleteTextBox();;
    public DatePicker salesStartDate;
    public DatePicker salesEndDate;
    public TableView salesTableView;
    public Pane salesReportPane;
    private ObservableList<ObservableList> salesData;

    // For restock report
    public TableView restockTable;

    // For pair report
    public DatePicker pairStartDate;
    public DatePicker pairEndDate;
    public TableView pairTable;
    public Pane pairPane;

    @FXML
    private Label Time;

    private final String pattern = "yyyy-MM-dd";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

    // for the inventory table
    private ObservableList<ObservableList> data;
    private final TableView tableView = new TableView();

    public DatePicker targetDate;


    /** Sets the primary stage for the inventory view controller.
     *
     * @param primaryStage The primary stage object of the inventory view of the application.
     */
    public void setInvViewController(Stage primaryStage) { this.invViewStage = primaryStage; }


    /** Initializes the application's user interface by setting up a digital clock
     * that displays the current time, updating every second.
     *
     * This method should be called when the application starts.
     */
    public void initialize() {
        Platform.runLater(() -> {
            menuItemNameBox.getChildren().add(autoCompleteDrinkName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

            Font.loadFont(getClass().getResourceAsStream("Amerigo BT.ttf"), 14);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                java.util.Date currentTime = new java.util.Date();
                String formattedTime = dateFormat.format(currentTime);

                Time.setStyle("-fx-font-size: 20;");
                Time.setText(formattedTime);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });
    }


    /** Displays the inventory table by fetching data from the database and populating the TableView.
     *
     * This method populates the TableView with data from the inventory which is retrieved the database
     * and shown to the Managers.
     *
     * @param screenExists A boolean to indicate whether the TableView is already present on the screen.
     */
    public void displayTable(boolean screenExists) {
        try (Connection conn = getSQLConnection()) {
            String stmt = "SELECT * FROM inventory";

            // find some way to put the pulled select * into a JavaFX element tableView
            ResultSet rs = conn.createStatement().executeQuery(stmt);
            data = FXCollections.observableArrayList();

            for (int i = 0 ; i < rs.getMetaData().getColumnCount(); i++) {
                // We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        SimpleStringProperty s = new SimpleStringProperty();
                        String colName = param.getValue().get(j).toString();
                        colName = colName.replaceAll("(?i)inventory|date", "");
                        colName = colName.substring(0, 1).toUpperCase() + colName.substring(1);
                        s.set(colName);
                        return s;
                    }
                });

                tableView.getColumns().addAll(col);
            }

            while(rs.next()) {
                // Iterate Row
                ObservableList<Object> row = FXCollections.observableArrayList();
                for (int i=1 ; i<=rs.getMetaData().getColumnCount(); i++) {
                    int columnType = rs.getMetaData().getColumnType(i);
                    Object value;

                    switch (columnType) {
                        case Types.INTEGER:
                            value = rs.getInt(i);
                            break;
                        case Types.BOOLEAN:
                            value = rs.getBoolean(i);
                            break;
                        case Types.DATE:
                            value = rs.getDate(i);
                            break;
                        default:
                            // Default to treating it as a string
                            value = rs.getString(i);
                            break;
                    }
                    row.add(value);
                }
                data.add(row);

            }

            // FINALLY ADDED TO TableView
            tableView.setItems(data);

            if (!screenExists) {
                inventoryPane.getChildren().add(tableView);
                AnchorPane.setTopAnchor(tableView, 0.0);
                AnchorPane.setBottomAnchor(tableView, 0.0);
                AnchorPane.setLeftAnchor(tableView, 0.0);
                AnchorPane.setRightAnchor(tableView, 0.0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }


    /** Adds a new inventory item to the Inventory in our database. We also
     * take care of clearing the popup window when we add an item so that the next time
     * we do this, we don't see the information from the last item that was added.
     *
     * This method adds a new item to the inventory in case we have new drinks that require
     * new ingredients rather than the ones that are already in the database.
     *
     * @param actionEvent The ActionEvent when "Submit" button is pressed within the Inventory View to
     * update or add a new inventory item.
     */
    @FXML
    private void submitInventory(ActionEvent actionEvent) {
        Alert alert;
        // will probably take inputs as strings: need to cast to correct datatypes before query
        // if arguments are passed into a single string of arguments: split into array of strings

        if (itemName.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR, "Please enter an item name.");
            alert.show();
            return;
        }

        try (Connection conn = getSQLConnection()) {
            String query = "SELECT * FROM inventory WHERE inventoryName = ?";

            PreparedStatement findName = conn.prepareStatement(query);
            findName.setString(1, itemName.getText());
            ResultSet rs = findName.executeQuery();

            // query for item name here
            while (rs.next()) {
                if (rs.getString("inventoryName").equals(itemName.getText())){
                    helperUpdateItem();
                    inventoryAddPane.setVisible(false);
                    inventoryAddPane.setDisable(true);
                    inventoryAddPane.setOpacity(0);

                    inventoryPane.setOpacity(1);
                    inventoryPane.setDisable(false);
                    return;
                }
            }

            // query id to add 1 to; incrememnt inventoryID by 1 when adding new item
            int itemID = 88888;
            PreparedStatement findBiggestID = conn.prepareStatement("SELECT MAX(inventoryID) AS maxID FROM inventory");
            ResultSet resultSet = findBiggestID.executeQuery();
            while (resultSet.next()) {
                itemID = resultSet.getInt("maxID");
            }

            // insert new inventory item
            PreparedStatement insertItem = conn.prepareStatement("INSERT INTO inventory (inventoryID, inventoryName, inventoryQuantity, inventoryReceivedDate, inventoryExpirationDate, inventoryInStock, inventorySupplier) VALUES (?, ?, ?, ?, ?, ?, ?)");
            insertItem.setInt(1, itemID + 1);
            insertItem.setString(2, itemName.getText());
            insertItem.setFloat(3, Float.parseFloat(quantity.getText()));

            java.sql.Date r_date = java.sql.Date.valueOf(dateReceived.getText());
            insertItem.setDate(4, r_date);
            java.sql.Date e_date = java.sql.Date.valueOf(expDate.getText());
            insertItem.setDate(5, e_date);

            insertItem.setBoolean(6, Boolean.parseBoolean(inStock.getText()));
            insertItem.setString(7, supplier.getText());
            insertItem.executeUpdate();
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("New Item");
            alert.setHeaderText("New item information has been added");
            alert.setContentText("Item has been added to inventory successfully.");
            alert.showAndWait();

            displayTable(true);

            inventoryAddPane.setVisible(false);
            inventoryAddPane.setDisable(true);
            inventoryAddPane.setOpacity(0);

            inventoryPane.setOpacity(1);
            inventoryPane.setDisable(false);
        } catch (SQLException e) {
            //System.out.println("Error accessing database.");
            e.printStackTrace();
        }

        supplier.setText("");
        itemName.setText("");
        dateReceived.setText("");
        expDate.setText("");
        quantity.setText("");
    }


    /** Helper method to update an existing item in the inventory; runs an update SQL query
     *
     * This method updates an item that is already within the inventory database and clears
     * the popup to allow for an update in the future without seeing the information that was
     * previously added.
     */
    private void helperUpdateItem () {
        Alert alert;

        try (Connection conn = getSQLConnection()) {
            PreparedStatement insertItem = conn.prepareStatement("UPDATE inventory SET inventoryQuantity = ?, inventoryReceivedDate = ?, inventoryExpirationDate = ?, inventoryInStock = ?, inventorySupplier = ? WHERE inventoryName = ?");

            insertItem.setFloat(1, Float.parseFloat(quantity.getText()));

            java.sql.Date r_date = java.sql.Date.valueOf(dateReceived.getText());
            insertItem.setDate(2, r_date);
            java.sql.Date e_date = java.sql.Date.valueOf(expDate.getText());
            insertItem.setDate(3, e_date);

            insertItem.setBoolean(4, Boolean.parseBoolean(inStock.getText()));
            insertItem.setString(5, supplier.getText());
            insertItem.setString(6, itemName.getText());
            insertItem.executeUpdate();

            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Updated Item");
            alert.setHeaderText("Updated item information has been added");
            alert.setContentText("Item has been updated in inventory successfully.");
            alert.showAndWait();

            displayTable(true);
        } catch (SQLException e) {
            //System.out.println("Error accessing database.");
            e.printStackTrace();
            return;
        }

        supplier.setText("");
        itemName.setText("");
        dateReceived.setText("");
        expDate.setText("");
        quantity.setText("");
    }


    /** Allows for going back to the inventory stage when adding a new item to the inventory.
     *
     * This method updates the user interface to go from the popup button to go back to the
     * inventory stage.
     *
     * @param actionEvent This ActionEvent is triggered by pressing the "Add" button in
     * the inventory.
     */
    public void inventoryAdd(ActionEvent actionEvent) {
        inventoryAddPane.setVisible(true);
        inventoryAddPane.setDisable(false);
        inventoryAddPane.setOpacity(1);

        inventoryPane.setOpacity(0.5);
        inventoryPane.setDisable(true);
    }


    /** Switches to the main menu stage within the inventory view stage.
     *
     * This method is responsible for transitioning from the inventory view to the
     * main menu view after clicking on the "Main Menu" tab.
     *
     * @param actionEvent This ActionEvent is triggered by pressing the "Main Menu" button on the left side of the interface from the inventory stage.
     * @throws IOException If an I/O exception occurs while loading the manager view stage.
     */
    public void mainMenuButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/green/gongchapos/managerView/managerView.fxml"));

        Scene inventoryScene = new Scene(loader.load());
        ManagerViewController controller = loader.getController();
        controller.setCashierViewController(invViewStage);
        controller.backButton(null);

        invViewStage.setScene(inventoryScene);
        invViewStage.show();
    }


    /** Displays the sales report interface and initializes data for it.
     *
     * @param event This ActionEvent triggers an action when the "Sales Report" button
     * is pressed within the inventory view.
     */
    public void salesReportButton(ActionEvent event) {
        salesReportPane.setVisible(true);
        salesReportPane.setDisable(false);
        salesReportPane.setOpacity(1);

        inventoryPane.setOpacity(0.5);
        inventoryPane.setDisable(true);

        salesStartDate.setValue(LocalDate.now().minusWeeks(1));
        salesEndDate.setValue(LocalDate.now());
        
        ArrayList<String> drinkList = new ArrayList<String>();

        try {
            Connection conn = getSQLConnection();

            String query = "SELECT menuItemName FROM menuItems WHERE menuitemcategory != ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "Toppings");

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                drinkList.add(resultSet.getString("menuItemName"));
            }


            String query2 = "SELECT * FROM sales WHERE saledate >= ? AND saledate <= ?";
            PreparedStatement stmt2 = conn.prepareStatement(query2);

            stmt2.setDate(1, java.sql.Date.valueOf(salesStartDate.getValue()));
            stmt2.setDate(2, java.sql.Date.valueOf(salesEndDate.getValue()));


            ResultSet resultSet2 = stmt2.executeQuery();

            salesData = FXCollections.observableArrayList();

            for (int i = 0 ; i < resultSet2.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(resultSet2.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        SimpleStringProperty s = new SimpleStringProperty();
                        String colName = param.getValue().get(j).toString();
                        colName = colName.replaceAll("(?i)inventory|date", "");
                        colName = colName.substring(0, 1).toUpperCase() + colName.substring(1);
                        s.set(colName);
                        return s;
                    }
                });

                salesTableView.getColumns().addAll(col);
            }

            while (resultSet2.next()) {
                //Iterate Row
                ObservableList<Object> row = FXCollections.observableArrayList();
                for (int i = 1 ; i<=resultSet2.getMetaData().getColumnCount(); i++) {
                    int columnType = resultSet2.getMetaData().getColumnType(i);
                    Object value;

                    switch (columnType) {
                        case Types.INTEGER:
                            value = resultSet2.getInt(i);
                            break;
                        case Types.BOOLEAN:
                            value = resultSet2.getBoolean(i);
                            break;
                        case Types.DATE:
                            value = resultSet2.getDate(i);
                            break;
                        default:
                            // Default to treating it as a string
                            value = resultSet2.getString(i);
                            break;
                    }
                    row.add(value);
                }
                salesData.add(row);
            }
            salesTableView.setItems(salesData);


            autoCompleteDrinkName.setOnAction(drinkNameEvent -> {
                salesData.clear();
                try {
                    ResultSet rs = salesReportQuery();
                    while (rs.next()){
                        ObservableList<Object> row = FXCollections.observableArrayList();
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            row.add(rs.getString(i));
                        }
                        salesData.add(row);
                    }

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            });

            salesStartDate.setOnAction(drinkNameEvent -> {
                if(!autoCompleteDrinkName.getText().isEmpty()){
                    salesData.clear();
                    try {
                        ResultSet rs = salesReportQuery();
                        while (rs.next()){
                            ObservableList<Object> row = FXCollections.observableArrayList();
                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                row.add(rs.getString(i));
                            }
                            salesData.add(row);
                        }

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            salesEndDate.setOnAction(drinkNameEvent -> {
                if(!autoCompleteDrinkName.getText().isEmpty()){
                    salesData.clear();
                    try {
                        ResultSet rs = salesReportQuery();
                        while (rs.next()){
                            ObservableList<Object> row = FXCollections.observableArrayList();
                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                row.add(rs.getString(i));
                            }
                            salesData.add(row);
                        }

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });


        } catch(SQLException e) {
//            System.out.println("Error accessing database.");
        }
        autoCompleteDrinkName.getEntries().addAll(drinkList);
        

    }


    /** Queries the database to retrieve the data from the sales table
     *
     * @return rs A ResultSet containing the sales data.
     * @throws SQLException If an SQL exception occurs during query execution.
     */
    private ResultSet salesReportQuery() throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try (Connection conn = getSQLConnection()) {
            String queryName = "SELECT menuItemID FROM menuItems WHERE menuitemname = ?";
            stmt = conn.prepareStatement(queryName);
            stmt.setString(1, autoCompleteDrinkName.getText());

            int id = 8193250;
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("menuItemID");
            }

            String query = "SELECT * FROM sales WHERE saledate >= ? AND saledate <= ? AND menuitemid = ?";
            PreparedStatement stmt2 = conn.prepareStatement(query);

            stmt2.setDate(1, java.sql.Date.valueOf(salesStartDate.getValue()));
            stmt2.setDate(2, java.sql.Date.valueOf(salesEndDate.getValue()));
            stmt2.setInt(3, id);
            rs = stmt2.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }


    /** Hides the sales report pane and displays the inventory pane in the user interface.
    *
    * This method is used to navigate back from the sales report view to the inventory view
    * by making the sales report pane invisible and disabled and displaying the inventory
    * pane.
    *
    * @param event The ActionEvent triggered by a user action.
    */ 
    public void salesReportBack(ActionEvent event) {
        salesReportPane.setVisible(false);
        salesReportPane.setDisable(true);
        salesReportPane.setOpacity(0);

        inventoryPane.setOpacity(1);
        inventoryPane.setDisable(false);
    }

    
    /** Generates an excess report and displays it in the user interface.
    *
    * This method calculates excess inventory items based on certain criteria and
    * presents the excess report in the application. It clears the existing report
    * data, sets the excess report pane as visible and enabled, and decreases the
    * opacity of the inventory pane for a better user focus on the report.
    *
    * @param event The ActionEvent triggered by a user action of pressing "Excess Report" in inventory view.
    * @throws SQLException If there is an issue with the database connection or SQL queries.
    */
    public void excessReport(ActionEvent event) throws SQLException {
        excessTable.getItems().clear();
        excessReportPane.setVisible(true);
        excessReportPane.setDisable(false);
        excessReportPane.setOpacity(1);

        inventoryPane.setOpacity(0.5);
        inventoryPane.setDisable(true);
    }


    /** Switches the view from excess report back to inventory view.
    *
    * This method calculates excess inventory items based on certain criteria and
    * presents the excess report in the application. It clears the existing report
    * data, sets the excess report pane as visible and enabled, and decreases the
    * opacity of the inventory pane for a better user focus on the report
    *
    * @param event The ActionEvent triggered by a user action of pressing the "Back" button on the excess report view.
    */
    public void ExcessReportBack(ActionEvent event) {
        excessReportPane.setVisible(false);
        excessReportPane.setDisable(true);
        excessReportPane.setOpacity(0);

        inventoryPane.setOpacity(1);
        inventoryPane.setDisable(false);
        inventoryPane.setVisible(true);
    }


    /** Generates the excess report for the information we have in our database to see what doesn't
    * get used as often as other inventory items.
    *
    * @param event The ActionEvent triggered by a user setting the start and end date of the excess report.
    * @throws SQLException If there is an issue with the database connection or SQL queries.
    */
    public void excessReportGenerate(ActionEvent event) throws SQLException {
        excessTable.getItems().clear();
        excessTable.getColumns().clear();

        try (Connection conn = getSQLConnection()) {
            LocalDate date = targetDate.getValue();

            // Create a map to track used quantity for each ingredient
            Map<Integer, Float> ingredientUsedQuantity = new HashMap<>();


            // Step 1: Iterate through all the sales after the targetDate
            String sql = "SELECT mi.menuItemID, mi.menuItemName, mi.menuItemPrice, mi.menuItemCalories, "
                       + "mi.menuItemCategory, mi.hasCaffeine, m.inventoryID, m.measurement "
                       + "FROM Sales s "
                       + "JOIN menuItems mi ON s.menuItemID = mi.menuItemID "
                       + "JOIN menuItems_Inventory m ON mi.menuItemID = m.menuItemID "
                       + "WHERE s.saleDate >= ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(date));

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int menuItemID = rs.getInt("menuItemID");
                        int inventoryID = rs.getInt("inventoryID");
                        float measurement = rs.getFloat("measurement");
                        float saleQuantity = 1.0f; // Assuming 1 item sold, adjust if needed

                        // Calculate the used quantity for this ingredient
                        float usedQuantity = saleQuantity * measurement;

                        // Update the used quantity in the map
                        ingredientUsedQuantity.merge(inventoryID, usedQuantity, Float::sum);
                    }
                }
            }

            // Step 2: Iterate through the inventory and compare used quantity to original stocked quantity
            sql = "SELECT * FROM Inventory";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    ObservableList<ObservableList> excessData = FXCollections.observableArrayList();

                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                        // We are using non property style for making dynamic table
                        final int j = i;
                        TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                        col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                                SimpleStringProperty s = new SimpleStringProperty();
                                String colName = param.getValue().get(j).toString();
                                colName = colName.replaceAll("(?i)inventory|date", "");
                                colName = colName.substring(0, 1).toUpperCase() + colName.substring(1);
                                s.set(colName);
                                return s;
                            }
                        });
                        excessTable.getColumns().addAll(col);
                    }

                    while (rs.next()) {
                        int inventoryID = rs.getInt("inventoryID");
                        int stockedQuantity = rs.getInt("inventoryQuantity");
                        float usedQuantity = ingredientUsedQuantity.getOrDefault(inventoryID, 0.0f);
                        Date receivedDate = rs.getDate("inventoryReceivedDate");
                        LocalDate rd = receivedDate.toLocalDate();


                        // if the invetory item has been restocked within that time: SKIPPPPPPPPPPPPPPP
                        if (rd.isAfter(date)) {
                            continue;
                        }

                        // Check if excess report needs to be generated
                        if (usedQuantity < (stockedQuantity *  0.1)) {
                            // Add this inventory item to the excess report
                            //System.out.println("Inventory ID: " + inventoryID + " - Excess Report Needed");

                            //Iterate Row
                            ObservableList <Object> row = FXCollections.observableArrayList();

                            for (int i = 1 ; i <= rs.getMetaData().getColumnCount(); i++) {
                                int columnType = rs.getMetaData().getColumnType(i);
                                Object value;

                                switch (columnType) {
                                    case Types.INTEGER:
                                        value = rs.getInt(i);
                                        //System.out.println(value);
                                        break;
                                    case Types.FLOAT:
                                        value = rs.getFloat(i);
                                        //System.out.println(value);
                                        break;
                                    case Types.BOOLEAN:
                                        value = rs.getBoolean(i);
                                        //System.out.println(value);
                                        break;
                                    case Types.DATE:
                                        value = rs.getDate(i);
                                        //System.out.println(value);
                                        break;
                                    default:
                                        // Default to treating it as a string
                                        value = rs.getString(i);
                                        //System.out.println(value);
                                        break;
                                }
                                row.add(value);
                            }
                            excessData.add(row);
                        }
                    }
                    excessTable.setItems(excessData);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /** Handles the event when a user wants to go back to the inventory view from the restock report view.
    *
    * @param event The ActionEvent triggered by clicking "Back" while on the restock report.
    */
    public void RestockReportBack(ActionEvent event) {
        restockPane.setVisible(false);
        restockPane.setDisable(true);
        restockPane.setOpacity(0);

        inventoryPane.setOpacity(1);
        inventoryPane.setDisable(false);
    }


    /** Generates a restock report which populates a table with data from the inventory.
    *
    * This method clears any existing data and columns in the restock table and retrieves the data from
    * the database for items that are out of stock. This then creates a table based on the data
    * that is found.
    *
    * @param event The ActionEvent triggered by clicking "Restock Report" from the inventory view.
    */
    @FXML
    public void restockReport(ActionEvent event) {
        restockPane.setVisible(true);
        restockPane.setDisable(false);
        restockPane.setOpacity(1);

        inventoryPane.setOpacity(0.5);
        inventoryPane.setDisable(true);

        restockTable.getItems().clear();
        restockTable.getColumns().clear();

        try (Connection conn = getSQLConnection()) {
            String query = "SELECT * FROM inventory WHERE inventoryinstock = false";
            PreparedStatement stmt = conn.prepareStatement(query);
            ObservableList<ObservableList> restockReport = FXCollections.observableArrayList();
            ResultSet rs = stmt.executeQuery();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                // We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        SimpleStringProperty s = new SimpleStringProperty();
                        String colName = param.getValue().get(j).toString();
                        colName = colName.replaceAll("(?i)inventory|date", "");
                        colName = colName.substring(0, 1).toUpperCase() + colName.substring(1);
                        s.set(colName);
                        return s;
                    }
                });
                restockTable.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<Object> row = FXCollections.observableArrayList();
                for (int i = 1 ; i <= rs.getMetaData().getColumnCount(); i++) {
                    int columnType = rs.getMetaData().getColumnType(i);
                    Object value;

                    switch (columnType) {
                        case Types.INTEGER:
                            value = rs.getInt(i);
                            break;
                        case Types.BOOLEAN:
                            value = rs.getBoolean(i);
                            break;
                        case Types.DATE:
                            value = rs.getDate(i);
                            break;
                        default:
                            // Default to treating it as a string
                            value = rs.getString(i);
                            break;
                    }
                    row.add(value);
                }
                restockReport.add(row);
            }
            restockTable.setItems(restockReport);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /** Handles the event on going from pair product view back to the inventory view.
    *
    * @param event The ActionEvent triggered by clicking "Back" button on the pair product view.
    */
    public void pairPaneBack(ActionEvent event) {
        pairPane.setVisible(false);
        pairPane.setDisable(true);
        pairPane.setOpacity(0);

        inventoryPane.setOpacity(1);
        inventoryPane.setDisable(false);
    }


    /** Prepares the user interface to retrieve the information on pair products.
    *
    */
    public void pairProduct() {
        pairStartDate.setValue(java.time.LocalDate.now());
        
        pairPane.setVisible(true);
        pairPane.setDisable(false);
        pairPane.setOpacity(1);

        inventoryPane.setOpacity(0.5);
        inventoryPane.setDisable(true);

        pairTable.getItems().clear();
        pairTable.getColumns().clear();
    }


    /** Retrieves sales data and creates a mapping of pairs of drinks and their counts.
    *
    * This method connects to the database to fetch sales data, and for each order,
    * it extracts the drink information to create pairs of drinks and counts.
    * The pairs are then stored in a map, where each key represents a pair of drinks,
    * and the value is the count of that pair.
    *
    * @param event The ActionEvent triggered by a user action.
    */
    public void pairEndDateSet(ActionEvent event) {
        pairTable.getItems().clear();
        pairTable.getColumns().clear();

        try (Connection conn = getSQLConnection()) {
            String sql = "SELECT "
                + "CASE WHEN menuItem1.menuItemName < menuItem2.menuItemName THEN menuItem1.menuItemName ELSE menuItem2.menuItemName END AS item1, "
                + "CASE WHEN menuItem1.menuItemName < menuItem2.menuItemName THEN menuItem2.menuItemName ELSE menuItem1.menuItemName END AS item2, "
                + "COUNT(*) AS frequency "
                + "FROM Sales AS sale1 "
                + "INNER JOIN Sales AS sale2 ON sale1.orderNo = sale2.orderNo AND sale1.menuItemID <> sale2.menuItemID "
                + "INNER JOIN menuItems AS menuItem1 ON sale1.menuItemID = menuItem1.menuItemID "
                + "INNER JOIN menuItems AS menuItem2 ON sale2.menuItemID = menuItem2.menuItemID "
                + "WHERE sale1.saleDate BETWEEN ? AND ? "
                + "GROUP BY item1, item2 "
                + "ORDER BY frequency DESC;";


            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDate(1, java.sql.Date.valueOf(pairStartDate.getValue()));
            preparedStatement.setDate(2, java.sql.Date.valueOf(pairEndDate.getValue()));
            ObservableList<ObservableList> pairData = FXCollections.observableArrayList();

            ResultSet rs = preparedStatement.executeQuery();

            for (int i = 0 ; i < rs.getMetaData().getColumnCount(); i++) {
                // We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        SimpleStringProperty s = new SimpleStringProperty();
                        String colName = param.getValue().get(j).toString();
                        colName = colName.replaceAll("(?i)inventory|date", "");
                        colName = colName.substring(0, 1).toUpperCase() + colName.substring(1);
                        s.set(colName);
                        return s;
                    }
                });

                pairTable.getColumns().addAll(col);
            }

            while(rs.next()) {
                // Iterate Row
                ObservableList<Object> row = FXCollections.observableArrayList();
                for (int i=1 ; i<=rs.getMetaData().getColumnCount(); i++) {
                    int columnType = rs.getMetaData().getColumnType(i);
                    Object value;

                    switch (columnType) {
                        case Types.INTEGER:
                            value = rs.getInt(i);
//                            System.out.println(value);
                            break;
                        case Types.BOOLEAN:
                            value = rs.getBoolean(i);
//                            System.out.println(value);
                            break;
                        case Types.DATE:
                            value = rs.getDate(i);
//                            System.out.println(value);
                            break;
                        default:
                            // Default to treating it as a string
                            value = rs.getString(i);
//                            System.out.println(value);
                            break;
                    }
                    row.add(value);
                }
                pairData.add(row);

            }

            // FINALLY ADDED TO TableView
            pairTable.setItems(pairData);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
