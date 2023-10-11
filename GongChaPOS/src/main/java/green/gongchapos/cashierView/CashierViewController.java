package green.gongchapos.cashierView;

import green.gongchapos.dbSetup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;


public class CashierViewController {

    private Stage primaryStage;

    public static class Drink {
        String name;
        boolean isLarge;

        Drink (String name, boolean isLarge) {
            this.name = name;
            this.isLarge = isLarge;
        }
    }

    // checkout cart of Drink data types; each add to order button press will add to this
    private ArrayList<Drink> cart;

    @FXML
    private String seriesName;

    public void setCashierViewController(Stage primaryStage) { this.primaryStage = primaryStage; }
    @FXML
    private void selectSeries(ActionEvent actionEvent) {
        Connection conn = null;
        String teamName = "00g";
        String dbName = "csce315331_" + teamName + "_db";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;

        try {
            conn = DriverManager.getConnection(dbConnectionString, dbSetup.user, dbSetup.pswd);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        try {
            String selectSeriesSQL = "SELECT menuItemName FROM menuItems WHERE menuItemCategory = " + seriesName;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    @FXML
    private void customizeOrderPopUp(ActionEvent event) {
        // need to display a new scene: pop up to customize order
        // stack pane ??
        // will determine isLarge

        // how to obtain the name of the drink being ordered from button click
        Button sourceButton = (Button) event.getSource();
        String drinkName = sourceButton.getText();
    }


    // private void addToOrder() {
    //     cart.add(new Drink(__name__, __isLarge__));
    // }

    private void placeOrder() {
        // acces cart for each of the drinks

        // find the current employee's id
        // get passed order number
        String orderNoQuery = "SELECT MAX(orderNo) FROM sales";
        int orderNo;
        // get current data and time
        for (Drink d : cart) {
            // String drinkPriceQuery = "SELECT menuItemID, menuItemPrice FROM menuItems WHERE menuItemName = " ;
            // get id of drink based on d.name
            // get that drink's ^ price

            // run sql command
            String placeOrderSQL = "INSERT INTO sales (orderNo, _date, _time, price, isLarge, menuItemsID)";
        }

        cart.clear();
    }
}
