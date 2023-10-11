package green.gongchapos;

import green.gongchapos.cashierView.CashierViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

public class LoginController {

    private Stage logInStage;
    private Stage cashierViewStage;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    public void setLogInStage(Stage primaryStage) {
        this.logInStage = primaryStage;
    }
    @FXML
    private void logIn_onClick(ActionEvent actionEvent) {
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
            // Step 1: Check if the username exists
            String checkUsernameSQL = "SELECT employeeUserPassword FROM employees WHERE employeeUserName = ? LIMIT 1";
            try(PreparedStatement checkUsername = conn.prepareStatement(checkUsernameSQL)) {

                checkUsername.setString(1, userName.getText());
                ResultSet resultSet = checkUsername.executeQuery();

                Alert alert;
                if (resultSet.next()) { // if not empty (meaning username exists)
                    String storedPassword = resultSet.getString("employeeUserPassword");

                    // Step 2: Check if the input password matches the stored password
                    if (storedPassword.equals(password.getText())) {
                        alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Successful login"); // No header
                        alert.setContentText("You are logged in successfully!");
                        alert.showAndWait();

                        // Create the CashierViewController and pass the Stage
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cashierView/cashierView.fxml"));
                        cashierViewStage = new Stage();

                        Scene scene = new Scene(fxmlLoader.load());
                        CashierViewController controller = fxmlLoader.getController();

                        cashierViewStage.setTitle("GongChaPOS");
                        cashierViewStage.setScene(scene);
                        controller.setCashierViewController(cashierViewStage);
                        GridPane gridPane = (GridPane) scene.lookup("#drinkPane");
                        gridPane.setDisable(true);
                        gridPane.setVisible(false);

                        cashierViewStage.show();
                        logInStage.close();

                    } else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Password Error");
                        alert.setContentText("Incorrect Password.");
                        alert.showAndWait();
                    }
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Username Error");
                    alert.setContentText("Username does not exist.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                System.out.println("Error checking username.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
