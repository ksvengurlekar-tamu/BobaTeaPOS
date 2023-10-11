package green.gongchapos.managerView;

import green.gongchapos.dbSetup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

import static green.gongchapos.GongCha.getSQLConnection;


public class ManagerViewController {

    public GridPane mainMenuPane;
    public GridPane drinkPane;
    public TilePane subDrinkPane;
    private Stage managerViewStage;

    public static class Drink {
        String name;
        boolean isLarge;

        Drink (String name, boolean isLarge) {
            this.name = name;
            this.isLarge = isLarge;
        }
    }

    // checkout cart of Drink data types; each add to order button press will add to this
    private ArrayList<Drink> cart;

    @FXML
    private String seriesName;

    public void setManagerViewController(Stage primaryStage) { this.managerViewStage = primaryStage; }
    @FXML
    private void selectSeries(ActionEvent actionEvent) {
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
            String selectSeriesSQL = "SELECT menuItemName FROM menuItems WHERE menuItemCategory = " + seriesName;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    @FXML
    private void seriesPress(ActionEvent event) throws SQLException {
        Scene scene = managerViewStage.getScene();
        GridPane mainMenuPane = (GridPane) scene.lookup("#mainMenuPane");
        mainMenuPane.setDisable(true);
        mainMenuPane.setVisible(false);

        GridPane drinkPane = (GridPane) scene.lookup("#drinkPane");
        drinkPane.setDisable(false);
        drinkPane.setVisible(true);


        Connection conn = getSQLConnection();
        Button sourceButton = (Button) event.getSource();
        String drinkName = sourceButton.getText();
        try {
            String getDrinks = "SELECT * FROM menuItems WHERE menuItemCategory = ?";
            try(PreparedStatement drinkStatment = conn.prepareStatement(getDrinks)) {
                drinkStatment.setString(1, drinkName);
                ResultSet resultSet = drinkStatment.executeQuery();

                subDrinkPane.setAlignment(Pos.CENTER);
                subDrinkPane.setHgap(10.0);
                subDrinkPane.setVgap(10.0);

                while(resultSet.next()) {
                    Text text = new Text(resultSet.getString("menuItemName"));
                    Button drinkButton = new Button();
                    drinkButton.setMinSize(161, 120);
                    drinkButton.setMaxSize(161, 120);
                    text.wrappingWidthProperty().bind(drinkButton.widthProperty());
                    drinkButton.setGraphic(text);

                    String drinkColor = resultSet.getString("color");
                    //drinkButton.setOnAction();


                    drinkButton.setMinWidth(100);
                    drinkButton.setStyle("-fx-background-color: " + drinkColor + ";");
                    subDrinkPane.getChildren().add(drinkButton);

                }
            }

        }
        catch (SQLException e){
            System.out.println("Error getting drinks");
            e.printStackTrace();
        }

    }


    // private void addToOrder() {
    // cart.add(new Drink(__name__, __isLarge__));
    //

    private void placeOrder() {
        // access cart for each of the drinks

        // find the current employee's id
        // get passed order number
        String orderNoQuery = "SELECT MAX(orderNo) FROM sales";
        int orderNo;
        // get current data and time
        for (Drink d : cart) {
           // Button sourceButton = (Button) event.getSource();
            //String drinkName = sourceButton.getText();
            // String drinkPriceQuery = "SELECT menuItemID, menuItemPrice FROM menuItems WHERE menuItemName = " ;
            // get id of drink based on d.name
            // get that drink's ^ price

            // run sql command
            String placeOrderSQL = "INSERT INTO sales (orderNo, _date, _time, price, isLarge, menuItemsID)";
        }

        cart.clear();
    }
}
