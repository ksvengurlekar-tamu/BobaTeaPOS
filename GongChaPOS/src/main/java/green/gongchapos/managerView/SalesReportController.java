package green.gongchapos.managerView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.collections.FXCollections;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.IOException;
import java.sql.*;

import static green.gongchapos.GongCha.getSQLConnection;

public class SalesReportController {

    public TextField startTime, endTime, menuItem;
    AutoCompleteTextBox autoCompleteDrinkName = new AutoCompleteTextBox();
    public HBox drinkNameHbox;

    private ObservableList<ObservableList> data;
    private TableView tableView = new TableView();

    @Override
    public void initialize() {
        this.backButton(null);
        drinkNameHbox.getChildren().add(autoCompleteDrinkName);
    }

    public void queryReport(ActionEvent e) {
        String startTime = this.startTime.getText();
        String endTime = this.endTime.getText();
        String menuItem = this.menuItem.getText();


    }
    public void displayTable(boolean screenExists) {

    }

    @Override
    public void backButton(ActionEvent actionEvent) {
          try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ManagerView.fxml"));
                Pane root = fxmlLoader.load();
                Stage stage = (Stage) startTime.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
          } catch (IOException e) {
                e.printStackTrace();
          }
    }
}
