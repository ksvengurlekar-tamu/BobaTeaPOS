package green.gongchapos;

import green.gongchapos.cashierView.CashierViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
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

    public static void main(String[] args) { launch(); }
}
