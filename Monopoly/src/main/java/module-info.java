module com.example.monopoly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.monopoly to javafx.fxml;
    exports com.example.monopoly;
}