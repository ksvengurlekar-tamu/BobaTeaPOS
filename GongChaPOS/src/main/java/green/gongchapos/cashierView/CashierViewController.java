package green.gongchapos.cashierView;

import green.gongchapos.dbSetup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

import static green.gongchapos.GongCha.getSQLConnection;


public class CashierViewController {

    public GridPane mainMenuPane;
    public GridPane drinkPane;
    public TilePane subDrinkPane;
    public VBox rightVBox;
    private Stage cashierViewStage;

    private boolean isPaneVisible = false; // Initial visibility state


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

    public void setCashierViewController(Stage primaryStage) { this.cashierViewStage = primaryStage; }

    @FXML
    private void seriesPress(ActionEvent event) throws SQLException {
        subDrinkPane.getChildren().clear();
        Scene scene = cashierViewStage.getScene();
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

                subDrinkPane.setAlignment(Pos.TOP_LEFT);
                subDrinkPane.setHgap(10.0);
                subDrinkPane.setVgap(10.0);
                subDrinkPane.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
                int count = 0;

                while(resultSet.next()) {
                    Text text = new Text(resultSet.getString("menuItemName"));
                    text.setTextAlignment(TextAlignment.CENTER);
                    Button drinkButton = new Button();
                    drinkButton.setMinSize(161, 120);
                    drinkButton.setMaxSize(161, 120);
                    text.wrappingWidthProperty().bind(drinkButton.widthProperty());
                    drinkButton.setGraphic(text);

                    String drinkColor = resultSet.getString("color");
                    //drinkButton.setOnAction();

                    drinkButton.setStyle(
                        "-fx-background-color: " + drinkColor + "; " +
                        "-fx-cursor: hand;"
                    );

                    drinkButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            // Mouse is hovering over the button
                            drinkButton.setStyle("-fx-background-color: " + drinkColor + "; -fx-border-color: #0099ff;" + "-fx-border-width: 2px;");
                        } else {
                            // Mouse is not hovering over the button
                            drinkButton.setStyle("-fx-background-color: " + drinkColor + "; -fx-border-width: 0px;");
                        }
                    });

                    subDrinkPane.getChildren().add(drinkButton);
                    count++;

                }
                while(count != 20) {
                    Button drinkButton = new Button();
                    drinkButton.setMinSize(161, 120);
                    drinkButton.setMaxSize(161, 120);
                    drinkButton.setStyle("-fx-background-color: #ffffff;");
                    subDrinkPane.getChildren().add(drinkButton);
                    count++;
                }

            } catch (SQLException e) {
                System.out.println("Error getting drinks");
                e.printStackTrace();
            }

        }
        catch (Exception e){
            System.out.println("Error getting drinks");
            e.printStackTrace();
        }
    }

    public void onCheckout(ActionEvent actionEvent) {
        if (isPaneVisible) {
            rightVBox.setDisable(true);
            rightVBox.setVisible(false);
            isPaneVisible = false;
        } else {
            rightVBox.setDisable(false);
            rightVBox.setVisible(true);
            isPaneVisible = true;
        }

    }

    public void backButton(ActionEvent actionEvent) {
        mainMenuPane.setDisable(false);
        mainMenuPane.setVisible(true);
        drinkPane.setDisable(true);
        drinkPane.setVisible(false);

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
