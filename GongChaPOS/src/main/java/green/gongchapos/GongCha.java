package green.gongchapos;

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

/**
 * The main class for the GongChaPOS application. It extends the JavaFX Application class
 * and serves as the entry point for the application. It initializes the user interface and
 * manages the database connection.
 */
public class GongCha extends Application {
    private static Connection conn = null;

    /**
     * The starting point of the JavaFX application. Initializes the UI and database connection.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If there is an issue with loading the UI layout.
     */
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

    /**
     * Establishes a connection to the PostgreSQL database for the application.
     * The connection details are defined based on the team name and database setup.
     *
     * @return A database connection object.
     * @throws SQLException If there is an issue with establishing the database connection.
     */
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow the exception so the caller can handle it
        }


        return conn;
    }

    /**
     * Closes the database connection. It is called during the application's shutdown.
     */
    private static void closeDatabaseConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The main method for launching the GongChaPOS application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch();
    }
}
