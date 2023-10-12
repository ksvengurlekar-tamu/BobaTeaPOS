package green.gongchapos.managerView;

import green.gongchapos.cashierView.CashierViewController;
import green.gongchapos.dbSetup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static green.gongchapos.GongCha.getSQLConnection;
import static green.gongchapos.GongCha.main;


/** Class for the CashierViewController, which controls the cashierView.fxml file and holds all
 * attributes and methods for the cashier view of the GongChaPOS system.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class ManagerViewController extends CashierViewController {
    public void inventoryClick(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("managerView/inventoryView/inventoryView.fxml"));

        Image Logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/GongChaLogo.png")));
        viewStage.getIcons().add(Logo);
    }

    
}