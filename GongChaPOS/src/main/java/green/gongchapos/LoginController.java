package green.gongchapos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.*;

public class LoginController {

    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    private Button logInButton;

    private Alert alert;

    @FXML
    private void logIn_onClick(ActionEvent actionEvent) {
        Connection conn = null;
        String teamName = "00g";
        String dbName = "csce315331_"+teamName+"_db";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
        dbSetup myCredentials = new dbSetup();

        try {
            conn = DriverManager.getConnection(dbConnectionString, dbSetup.user, dbSetup.pswd);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        try{
            //create a statement object
            Statement createStmt = conn.createStatement();

            // Step 1: Check if the username exists
            String checkUsernameSQL = "SELECT employeeUserPassword FROM employees WHERE employeeUserName = ? LIMIT 1";
            try(PreparedStatement checkUsername = conn.prepareStatement(checkUsernameSQL)) {

                checkUsername.setString(1, userName.getText());
                ResultSet resultSet = checkUsername.executeQuery();

                if (resultSet.next()) { // if not empty (meaning username exists)
                    String storedPassword = resultSet.getString("employeeUserPassword");

                    // Step 2: Check if the input password matches the stored password
                    if (storedPassword.equals(password.getText())) {
                        alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Successfull log-in"); // No header
                        alert.setContentText("You are logged in successfully!");
                        alert.showAndWait();
                    } else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Password Error"); // No header
                        alert.setContentText("Wrong Password");
                        alert.showAndWait();
                    }
                }
                else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Username Error");
                    alert.setContentText("Username Does not exist");
                    alert.showAndWait();
                }
            }
            catch (SQLException e) {
                System.out.println("Error checking username");
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
}
