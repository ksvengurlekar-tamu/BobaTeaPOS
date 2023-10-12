package green.gongchapos.managerView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.collections.FXCollections;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;


import java.sql.*;

import static green.gongchapos.GongCha.getSQLConnection;

public class InventoryViewController {
    public AnchorPane inventoryPane;
    public Pane inventoryAddPane;
    public TextField itemID;
    public TextField itemName;
    public TextField quantity;
    public TextField dateReceived;
    public TextField expDate;
    public TextField inStock;
    public TextField supplier;

    private ObservableList<ObservableList> data;
    private TableView tableView = new TableView();

    /**
     * Displays the inventory table by fetching data from the database and populating the TableView.
     *
     */
    public void displayTable() {
        try (Connection conn = getSQLConnection()) {
            String stmt = "SELECT * FROM inventory";
            PreparedStatement p = conn.prepareStatement(stmt);
            ResultSet table = p.executeQuery();
            //idExists = rs.next();

        // find some way to put the pulled select * into a JavaFX element tableView
            ResultSet rs = conn.createStatement().executeQuery(stmt);
            data = FXCollections.observableArrayList();

            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        SimpleStringProperty s = new SimpleStringProperty();
                        String colName = param.getValue().get(j).toString();
                        colName = colName.replace("inventory.", "");
                        colName = colName.replace("date.", "");
                        colName = colName.substring(0, 1).toUpperCase() + colName.substring(1);
                        s.set(colName);
                        return s;
                    }
                });

                tableView.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }

            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added "+row );
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tableView.setItems(data);

            inventoryPane.getChildren().add(tableView);
            AnchorPane.setTopAnchor(tableView, 0.0);
            AnchorPane.setBottomAnchor(tableView, 0.0);
            AnchorPane.setLeftAnchor(tableView, 0.0);
            AnchorPane.setRightAnchor(tableView, 0.0);
        } catch (SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }


    @FXML
    private void submitInventory(ActionEvent actionEvent) {
        Button button = new Button("Add");
        button.setOnAction((ActionEvent e) -> {
            System.out.println("Add");
        });

        // will probably take inputs as strings: need to cast to correct datatypes before query
        // if arguments are passed into a single string of arguments: split into array of strings

        if (itemName.getText().isEmpty()) {
            System.out.println("no name given");
            return;
        }

        try (Connection conn = getSQLConnection()) {
            String query = "SELECT * FROM inventory WHERE inventoryName = ?";

            PreparedStatement findId = conn.prepareStatement(query);
            findId.setString(1, itemName.getText());
            ResultSet rs = findId.executeQuery();

            boolean nameExists = false;

            // query for item name here
            if (rs.next() && rs.getString("iventoryName").equals(itemName)) {
                helperUpdateItem();
                return;
            }

            PreparedStatement insertItem = conn.prepareStatement("INSERT INTO inventory (inventoryName, inventoryQuantity, inventoryReceivedDate, inventoryExpirationDate, inventoryInStock, inventorySupplier) VALUES (?, ?, ?, ?, ?, ?)");

            insertItem.setString(1, itemName.getText());
            insertItem.setFloat(2, Float.parseFloat(quantity.getText()));

            java.sql.Date r_date = java.sql.Date.valueOf(dateReceived.getText());
            insertItem.setDate(3, r_date);
            java.sql.Date e_date = java.sql.Date.valueOf(expDate.getText());
            insertItem.setDate(4, e_date);

            insertItem.setBoolean(5, Boolean.parseBoolean(inStock.getText()));
            insertItem.setString(6, supplier.getText());
            insertItem.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }


    /**
     * Helper method to update an existing item in the inventory; runs an update SQL query
     *
     */
    private void helperUpdateItem () {
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
        }
        catch (SQLException e) {
            System.out.println("Error accessing database.");
            e.printStackTrace();
        }
    }


    public void inventoryAdd(ActionEvent actionEvent) {
        inventoryAddPane.setVisible(true);
        inventoryAddPane.setDisable(false);
        inventoryAddPane.setOpacity(1);

        inventoryPane.setOpacity(0.5);
        inventoryPane.setDisable(true);

    }
}

