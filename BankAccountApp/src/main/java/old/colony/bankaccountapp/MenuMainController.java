package old.colony.bankaccountapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MenuMainController {
    @FXML
    private Label nameLabel;
    private static String currentName;
    private void switchScene(ActionEvent event, String newScene) throws IOException {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(newScene)));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        SessionInformation.setCurrentRoot(root);
    }
    public void logout(ActionEvent event) throws IOException {
        switchScene(event, "Login.fxml");
    }
    public void switchDeposit(ActionEvent event) throws IOException {
        switchScene(event, "Deposit.fxml");
    }
    public void switchWithdraw(ActionEvent event) throws IOException {
        switchScene(event, "Withdraw.fxml");
    }
    public void switchHistory(ActionEvent event) throws IOException {
        switchScene(event, "History.fxml");
    }
    public void switchAccount(ActionEvent event) throws IOException {
        switchScene(event, "Account.fxml");
    }
    public void setNameLabel(String name) {
        currentName = name;
        nameLabel.setText("Hello, " + name);
    }
    public void initialize() {
        nameLabel.setText("Hello, " + currentName);
    }
}
