package green.gongchapos;

import green.gongchapos.cashierView.CashierViewController;
import green.gongchapos.managerView.ManagerViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;

import static green.gongchapos.GongCha.getSQLConnection;

/**
 * Controller class for the login view of the GongChaPOS system. Handles user authentication and
 * determines whether the user is a cashier or a manager, directing them to the appropriate view.
 * Manages user login and view switching.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class LoginController {

    @FXML
    private Stage logInStage;

    @FXML
    private Stage viewStage;

    @FXML
    private TextField userName;

    @FXML
    private TextField password;
    

    /**
     * Sets the primary login stage.
     *
     * @param primaryStage The primary login stage to set.
     */
    public void setLogInStage(Stage primaryStage) {
        this.logInStage = primaryStage;
    }


    /**
     * Handles the login button click event. Verifies user credentials, determines if the user is a
     * manager or a cashier, and navigates to the respective view.
     *
     * @param actionEvent The ActionEvent associated with the button click.
     * @throws SQLException If there is an issue with the database connection or SQL queries.
     */
    @FXML
    private void logIn_onClick(ActionEvent actionEvent) throws SQLException {
        Connection conn = getSQLConnection();

        try {
            // Step 1: Check if the username exists
            String checkUsernameSQL = "SELECT employeeUserPassword, isManager FROM employees WHERE employeeUserName = ? LIMIT 1";
            try(PreparedStatement checkUsername = conn.prepareStatement(checkUsernameSQL)) {

                checkUsername.setString(1, userName.getText());
                ResultSet resultSet = checkUsername.executeQuery();

                Alert alert;
                if (resultSet.next()) { // if not empty (meaning username exists)
                    String storedPassword = resultSet.getString("employeeUserPassword");
                    boolean isManager = resultSet.getBoolean("isManager");

                    // Step 2: Check if the input password matches the stored password
                    if (storedPassword.equals(password.getText())) {
                        alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Successful login"); // No header
                        alert.setContentText("You are logged in successfully!");
                        alert.showAndWait();

                        // Create the CashierViewController and pass the Stage
                        FXMLLoader fxmlLoader;

                        if (isManager) {
                            fxmlLoader = new FXMLLoader(getClass().getResource("managerView/managerView.fxml"));
                        } else {
                            fxmlLoader = new FXMLLoader(getClass().getResource("cashierView/cashierView.fxml"));
                        }

                        viewStage = new Stage();

                        Image Logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/GongChaLogo.png")));
                        viewStage.getIcons().add(Logo);

                        Scene scene = new Scene(fxmlLoader.load());
                        if (isManager) {
                            ManagerViewController controller = fxmlLoader.getController();
                            controller.setCashierViewController(viewStage);
                        } else {
                            CashierViewController controller = fxmlLoader.getController();
                            controller.setCashierViewController(viewStage);
                        }

                        viewStage.setTitle("GongChaPOS");
                        viewStage.setScene(scene);

                        GridPane gridPane = (GridPane) scene.lookup("#drinkPane");
                        
                        gridPane.setDisable(true);
                        gridPane.setVisible(false);

                        VBox rightVBox = (VBox) scene.lookup("#rightVBox");
                        rightVBox.setDisable(true);
                        rightVBox.setVisible(false);

                        Pane drinkPopUp = (Pane) scene.lookup("#drinkPopUp");
                        drinkPopUp.setDisable(true);
                        drinkPopUp.setVisible(false);
                        drinkPopUp.setOpacity(0);

                        viewStage.show();
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
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
