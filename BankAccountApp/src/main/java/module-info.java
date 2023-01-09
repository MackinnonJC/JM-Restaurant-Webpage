module old.colony.bankaccountapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens old.colony.bankaccountapp to javafx.fxml;
    exports old.colony.bankaccountapp;
}