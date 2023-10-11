package green.gongchapos;

import green.gongchapos.cashierView.CashierViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class GongCha extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("Amerigo BT.ttf"), 14);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        LoginController controller = fxmlLoader.getController(); // Get the controller instance;
        stage.setTitle("GongChaPOS");
        stage.setScene(scene);
        controller.setLogInStage(stage);
        stage.show();
    }

    public static Connection getSQLConnection() throws SQLException {
        Connection conn = null;
        String teamName = "00g";
        String dbName = "csce315331_" + teamName + "_db";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;

        try {
            conn = DriverManager.getConnection(dbConnectionString, dbSetup.user, dbSetup.pswd);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow the exception so the caller can handle it
        }

        return conn;
    }

    public static void main(String[] args) { launch(); }
}
