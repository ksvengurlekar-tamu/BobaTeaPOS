package green.gongchapos.cashierView;

import green.gongchapos.dbSetup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.*;

public class CashierViewController {

    @FXML
    String seriesName;


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
}
