package green.gongchapos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;

public class DB {
    @FXML
    private TextField texthh;
    @FXML
    public void ONCLOSE(ActionEvent actionEvent) {
        try {
            String pass = texthh.getText();

            System.out.println(pass);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
