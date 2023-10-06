package green.gongchapos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;
import javax.naming.spi.DirStateFactory.Result;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        //Building the connection
        Connection conn = null;
        //TODO STEP 1
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_00g_db",
                    "csce331_900_eanazir",
                    "Mollyadydo123$");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Set the minimum height
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE); // Set the minimum width
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Opened database successfully");
        // Show the dialog
        alert.showAndWait();

        String name = "";
        try{
            //create a statement object
            Statement stmt = conn.createStatement();
            //create a SQL statement
            //TODO Step 2
            String sqlStatement = "SELECT * FROM menuitems;";
            //send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                name += result.getString("menuItemName")+"\n";
            }
        } catch (Exception e){
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        TextArea textArea = new TextArea(name);

        // Create a Button
        Button closeButton = new Button("Close");

        // Add an action event handler to the button (you'll need to define the event handling)
        // Create a VBox (vertical box) to hold the TextArea and Button
        VBox vbox = new VBox(textArea, closeButton);

        // Create a Scene and set it in the Stage
        Scene scene = new Scene(vbox, 400, 400);
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        // Show the Stage
        primaryStage.show();
        closeButton.setOnAction(e -> {
            // Handle the button click event here
            System.out.println("Close button clicked");
            primaryStage.close();
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("DB.fxml")));
                Scene secondScene = new Scene(root, 800, 800);
                Stage secondStage = new Stage();
                secondStage.setScene(secondScene);
                // Show the Stage
                secondStage.show();
            }
            catch(Exception es){
                alert.setContentText(es.getMessage());
                alert.showAndWait();
            }
        });

        //welcomeText.setText("Welcome to JavaFX Application!");
    }
}