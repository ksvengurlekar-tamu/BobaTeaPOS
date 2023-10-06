module green.gongchapos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.naming;


    opens green.gongchapos to javafx.fxml;
    exports green.gongchapos;
}