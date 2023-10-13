package green.gongchapos.managerView;

import green.gongchapos.cashierView.CashierViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static green.gongchapos.GongCha.getSQLConnection;


/** Class for the CashierViewController, which controls the cashierView.fxml file and holds all
 * attributes and methods for the cashier view of the GongChaPOS system.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class ManagerViewController extends CashierViewController {
    
    public void inventoryClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/green/gongchapos/managerView/inventoryView/inventoryView.fxml"));

        Scene inventoryScene = new Scene(loader.load());
        InventoryViewController controller = loader.getController();
        controller.setInvViewController(cashierViewStage);
        controller.displayTable(false);

        cashierViewStage.setScene(inventoryScene);
        cashierViewStage.show();
    }
}
