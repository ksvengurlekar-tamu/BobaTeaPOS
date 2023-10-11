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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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


    private void addToOrder() {
        String __name__ = null;
        boolean __isLarge__ = false;

        cart.add(new Drink(__name__, __isLarge__));
    }

    // private void placeOrder() {  
    //      int orderID = -1;
    //     int orderNo = -1;
        
    //     LocalDateTime currentDateTime = LocalDateTime.now();

    //     // Define the date and time format
    //     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //     DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    //     // Format the date and time
    //     String _date = currentDateTime.format(dateFormatter);
    //     String _time = currentDateTime.format(timeFormatter);


    //     try {              
    //         Connection conn = getSQLConnection();
    // 
    //          String orderIDQuery = "SELECT MAX(orderID), MAX(orderNo) FROM sales"; 
    //         try(PreparedStatement orderIDStatement = conn.prepareStatement(orderIDQuery)) {
    //             ResultSet resultSet = orderIDStatement.executeQuery();
    //             orderID = resultSet.getInt("orderID");
    //              orderNo = resultSet.getInt("orderNo");
    //         }
    //          
        //     for (Drink d : cart) {
        //         // String menuIDQuery = "SELECT menuItemID, menuItemPrice FROM menuItems WHERE menuItemName = " + d.name;
        //          try(PreparedStatement orderStatement = conn.prepareStatement(menuIDQuery)) {
    //                  ResultSet resultSet = orderStatement.executeQuery();
    //                  menuItemID = resultSet.getInt("menuItemID");
            //          price = resultSet("menuItemPrice");
    //              }

        //         // run sql command
        //         String placeOrderSQL = "INSERT INTO sales (" + orderID + ", " + orderNo + ", " + _date + ", " + _time + 
        //             ", " + price + ", " + d.isLarge + ", " + menuItemID + ")";
                   // try(PreparedStatement orderStatement = conn.prepareStatement(placeOrderSQL)) {}
                //    orderID++; // orderID is a primary key and must be unique

        //      }

        // }
    //     catch (SQLException e) {
    //         System.out.println("Error accessing order number.");
    //         e.printStackTrace();
    //     }

    //     cart.clear();
    // }
}
