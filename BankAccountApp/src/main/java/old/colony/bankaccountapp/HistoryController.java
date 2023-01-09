package old.colony.bankaccountapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HistoryController {

    private void switchScene(ActionEvent event, String newScene) throws IOException {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(newScene)));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        SessionInformation.setCurrentRoot(root);
    }
    public void returnHistory(ActionEvent event) throws IOException {
        switchScene(event, "MenuMain.fxml");
    }
    public void logout(ActionEvent event) throws IOException {
        switchScene(event, "Login.fxml");
    }

}
