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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static green.gongchapos.GongCha.getSQLConnection;
import static green.gongchapos.GongCha.main;


public class CashierViewController {

    private GridPane mainMenuPane;
    private GridPane drinkPane;
    private TilePane subDrinkPane;
    private VBox rightVBox;
    private Stage cashierViewStage;
    private GridPane drinkPopUp;

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
        mainMenuPane.setDisable(true);
        mainMenuPane.setVisible(false);

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
                    drinkButton.setId("drinkButton");

                    drinkButton.setOnAction(drinkButtonEvent -> {
                        mainMenuPane.setDisable(true);
                        mainMenuPane.setVisible(false);
                        mainMenuPane.setOpacity(0.3);

                        drinkPopUp.setDisable(false);
                        drinkPopUp.setVisible(true);
                        drinkPopUp.setOpacity(1);
                    });

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


    private void addButton() {
        String __name__ = null;
        boolean __isLarge__ = false;

        cart.add(new Drink(__name__, __isLarge__));
    }

    private void placeOrder() {
        int orderID;
        int orderNo;
        int menuItemID;
        float price;

        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the date and time format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Format the date and time
        String _date = currentDateTime.format(dateFormatter);
        String _time = currentDateTime.format(timeFormatter);


        try {
            Connection conn = getSQLConnection();

            String orderIDQuery = "SELECT MAX(orderID), MAX(orderNo) FROM sales";
            try(PreparedStatement orderIDStatement = conn.prepareStatement(orderIDQuery)) {
                ResultSet resultSet = orderIDStatement.executeQuery();
                orderID = resultSet.getInt("orderID");
                 orderNo = resultSet.getInt("orderNo");
            }

            for (Drink d : cart) {
                String menuIDQuery = "SELECT menuItemID, menuItemPrice FROM menuItems WHERE menuItemName = " + d.name;
                try(PreparedStatement orderStatement = conn.prepareStatement(menuIDQuery)) {
                    ResultSet resultSet = orderStatement.executeQuery();
                    menuItemID = resultSet.getInt("menuItemID");
                    price = resultSet.getFloat("menuItemPrice");
                }

                // run insert sql command
                String placeOrderSQL = "INSERT INTO sales (" + orderID + ", " + orderNo + ", " + _date + ", " + _time +
                    ", " + price + ", " + d.isLarge + ", " + menuItemID + ")";
                try (PreparedStatement orderStatement = conn.prepareStatement(placeOrderSQL)) {}

                orderID++; // orderID is a primary key and must be unique
            }

        }
        catch (SQLException e) {
            System.out.println("Error accessing order number.");
            e.printStackTrace();
        }

        cart.clear();
    }
}
