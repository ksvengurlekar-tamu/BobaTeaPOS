package green.gongchapos.managerView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
import java.sql.*;

import static green.gongchapos.GongCha.getSQLConnection;

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
    public TextField startTime, endTime, menuItem;
    public HBox menuItemNameBox;
    AutoCompleteTextBox autoCompleteDrinkName = new AutoCompleteTextBox();;
    public DatePicker salesStartDate;
    public DatePicker salesEndDate;
    public TableView salesTableView;
    public Pane salesReportPane;

    private ObservableList<ObservableList> salesData;
    // private ObservableList<Map<String, Object>> salesData = FXCollections.observableArrayList();


    @FXML
    private Label Time;

    private final String pattern = "yyyy-MM-dd";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);


    // for the inventory table
    private ObservableList<ObservableList> data;
    private final TableView tableView = new TableView();

    public void setInvViewController(Stage primaryStage) { this.invViewStage = primaryStage; }


    /** Initializes the application's user interface by setting up a digital clock
     * that displays the current time, updating every second. This method should be
     * called when the application starts.
     *
     */
    public void initialize() {
        Platform.runLater(() -> {
            menuItemNameBox.getChildren().add(autoCompleteDrinkName);
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


    /**
     * Displays the inventory table by fetching data from the database and populating the TableView.
     *
     */
    public void displayTable(boolean screenExists) {
        try (Connection conn = getSQLConnection()) {
            String stmt = "SELECT * FROM inventory";

            // find some way to put the pulled select * into a JavaFX element tableView
            ResultSet rs = conn.createStatement().executeQuery(stmt);
            data = FXCollections.observableArrayList();

            for (int i = 0 ; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
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
                //Iterate Row
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

            //FINALLY ADDED TO TableView

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


    /**
     * Helper method to update an existing item in the inventory; runs an update SQL query
     *
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


    public void inventoryAdd(ActionEvent actionEvent) {
        inventoryAddPane.setVisible(true);
        inventoryAddPane.setDisable(false);
        inventoryAddPane.setOpacity(1);

        inventoryPane.setOpacity(0.5);
        inventoryPane.setDisable(true);

    }

    public void mainMenuButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/green/gongchapos/managerView/managerView.fxml"));

        Scene inventoryScene = new Scene(loader.load());
        ManagerViewController controller = loader.getController();
        controller.setCashierViewController(invViewStage);
        controller.backButton(null);

        invViewStage.setScene(inventoryScene);
        invViewStage.show();
    }



    // SALES REPORT DISPLAY


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

    public void salesReportBack(ActionEvent event) {
        salesReportPane.setVisible(false);
        salesReportPane.setDisable(true);
        salesReportPane.setOpacity(0);

        inventoryPane.setOpacity(1);
        inventoryPane.setDisable(false);
    }



}

