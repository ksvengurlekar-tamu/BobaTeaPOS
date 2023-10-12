package green.gongchapos.managerView;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;
import javafx.util.Callback;


import java.sql.*;

import static green.gongchapos.GongCha.getSQLConnection;

public class InventoryViewController {
    /*
    @FXML
    private TableView<InventoryItem> inventoryTable;
    @FXML
    private TableColumn<InventoryItem, String> nameColumn;
    @FXML
    private TableColumn<InventoryItem, Integer> quantityColumn;
    @FXML
    private TableColumn<InventoryItem, String> receivedDateColumn;
    @FXML
    private TableColumn<InventoryItem, String> expirationDateColumn;
    @FXML
    private TableColumn<InventoryItem, Boolean> inStockColumn;
    @FXML
    private TableColumn<InventoryItem, String> supplierColumn;

    private String name;
    private int quantity;
    private String receivedDate;
    private String expirationDate;
    private boolean inStock;
    private String supplier;
*/
//    public InventoryItem(String name, int quantity, String receivedDate, String expirationDate, boolean inStock, String supplier) {
//        this.name = name;
//        this.quantity = quantity;
//        this.receivedDate = receivedDate;
//        this.expirationDate = expirationDate;
//        this.inStock = inStock;
//        this.supplier = supplier;
//    }

    private TableView tableView = new TableView();

    /**
     * Displays the inventory table by fetching data from the database and populating the TableView.
     *
     */
    private void displayTable() {
        try (Connection conn = getSQLConnection()) {
            String stmt = "SELECT * FROM inventory";
            PreparedStatement p = conn.prepareStatement(stmt);
            ResultSet table = p.executeQuery();
            //idExists = rs.next();

        // find some way to put the pulled select * into a JavaFX element tableView
            ResultSet rs = conn.createStatement().executeQuery(stmt);

            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tableView.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }




//            while (table.next()) {
//                InventoryItem item = new InventoryItem(
//                    table.getString("inventoryName"),
//                    table.getInt("inventoryQuantity"),
//                    table.getString("inventoryReceivedDate"),
//                    table.getString("inventoryExpirationDate"),
//                    table.getBoolean("inventoryInStock"),
//                    table.getString("inventorySupplier")
//                );
//
//                inventoryItems.add(item);
//            }
//
//        // Bind columns to the corresponding properties of the InventoryItem class
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
//        receivedDateColumn.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
//        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
//        inStockColumn.setCellValueFactory(new PropertyValueFactory<>("inStock"));
//        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
//
//        // Set the items in the TableView
//        inventoryTable.setItems(inventoryItems);
        } catch (SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }

     /**
     * Adds an item to the inventory. If the item with the given ID already exists, it will be updated.
     *
     * @param id The ID of the item.
     * @param name The name of the item.
     * @param quantity The quantity of the item.
     * @param dateReceived The date the item was received.
     * @param expirationDate The expiration date of the item.
     * @param inStock Whether the item is in stock.
     * @param supplier The supplier of the item.
     */
    private void addButton(String id, String name, String quantity, String dateReceived, String expirationDate, String inStock, String supplier) {
        Button button = new Button("Add");
        button.setOnAction((ActionEvent e) -> {
            System.out.println("Add");
        });

        // will probably take inputs as strings: need to cast to correct datatypes before query
        // if arguments are passed into a single string of arguments: split into array of strings

        if (id.isEmpty()) {
            System.out.println("wrong id");
            return;
        }

        try (Connection conn = getSQLConnection()) {
            String query = "SELECT * FROM inventory WHERE id = ?";
            int targetID = 123; // TODO: make this input from front end

            PreparedStatement findId = conn.prepareStatement(query);
            findId.setInt(1, targetID);
            ResultSet rs = findId.executeQuery();

            boolean idExists = rs.next();

            // query for id here
            if (idExists) {
                helperUpdateItem(id, name, quantity, dateReceived, expirationDate, inStock, supplier);
                return;
            }

            PreparedStatement insertItem = conn.prepareStatement("INSERT INTO inventory (inventoryName, inventoryQuantity, inventoryReceivedDate, inventoryExpirationDate, inventoryInStock, inventorySupplier) VALUES (?, ?, ?, ?, ?, ?)");

            insertItem.setString(1, name);
            insertItem.setFloat(2, Float.parseFloat(quantity));

            java.sql.Date r_date = java.sql.Date.valueOf(dateReceived);
            insertItem.setDate(3, r_date);
            java.sql.Date e_date = java.sql.Date.valueOf(expirationDate);
            insertItem.setDate(4, e_date);

            insertItem.setBoolean(5, Boolean.parseBoolean(inStock));
            insertItem.setString(6, supplier);
            insertItem.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }

    /**
     * Helper method to update an existing item in the inventory.
     *
     * @param id The ID of the item.
     * @param name The name of the item.
     * @param quantity The quantity of the item.
     * @param dateReceived The date the item was received.
     * @param expirationDate The expiration date of the item.
     * @param inStock Whether the item is in stock.
     * @param supplier The supplier of the item.
     */
    private void helperUpdateItem (String id, String name, String quantity, String dateReceived, String expirationDate, String inStock, String supplier) {
        try (Connection conn = getSQLConnection()) {
            PreparedStatement insertItem = conn.prepareStatement("UPDATE inventory SET (inventoryName, inventoryQuantity, inventoryReceivedDate, inventoryExpirationDate, inventoryInStock, inventorySupplier) = VALUES(?, ?, ?, ?, ?, ?) WHERE inventoryID = ?");

            insertItem.setString(1, name);
            insertItem.setFloat(2, Float.parseFloat(quantity));

            java.sql.Date r_date = java.sql.Date.valueOf(dateReceived);
            insertItem.setDate(3, r_date);
            java.sql.Date e_date = java.sql.Date.valueOf(expirationDate);
            insertItem.setDate(4, e_date);

            insertItem.setBoolean(5, Boolean.parseBoolean(inStock));
            insertItem.setString(6, supplier);
            insertItem.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }
}
