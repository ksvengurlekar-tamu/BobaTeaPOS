package green.gongchapos;

import green.gongchapos.cashierView.CashierViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class GongCha extends Application {
    private static Connection conn = null;

    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("Amerigo BT.ttf"), 14);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));

        Image Logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/GongChaLogo.png")));
        stage.getIcons().add(Logo);

        Platform.runLater(() -> {
                    if (java.awt.Taskbar.isTaskbarSupported()) {
                        var taskbar = java.awt.Taskbar.getTaskbar();

                        if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                            var dockIcon = defaultToolkit.getImage(getClass().getResource("images/GongChaLogo.png"));
                            taskbar.setIconImage(dockIcon);
                        }

                    }
        });

        Scene scene = new Scene(fxmlLoader.load());
        LoginController controller = fxmlLoader.getController(); // Get the controller instance;
        stage.setTitle("GongChaPOS");
        stage.setScene(scene);
        controller.setLogInStage(stage);
        stage.show();
    }

    public static Connection getSQLConnection() throws SQLException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                closeDatabaseConnection();
            }
        });

        String teamName = "00g";
        String dbName = "csce315331_" + teamName + "_db";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;


        try {
            conn = DriverManager.getConnection(dbConnectionString, dbSetup.user, dbSetup.pswd);
            System.out.println("Succesfully created connection.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating connection.");
            throw e; // Rethrow the exception so the caller can handle it
        }
        // Close the connection if it's open
        /*if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }*/



        return conn;
    }

    private static void closeDatabaseConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Successfully closed connection.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error closing connection.");
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
