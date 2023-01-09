package old.colony.bankaccountapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField userName;
    @FXML
    private TextField passWord;
    @FXML
    private Label loginOutput;
    public void attemptLogin(ActionEvent event) throws IOException {
        if (!userName.getText().isBlank() && !passWord.getText().isBlank()) {
            // since it is not blank, attempt to log in
            PasswordResult result = AccountsFileHandler.passwordCheck(userName.getText(), passWord.getText());
            System.out.println(result);
            if (result == PasswordResult.VALID) {
                // TODO: User logs in with either savings or checking
                BankAccount account = AccountsFileHandler.accountFromUsername(userName.getText());
                SessionInformation.setCurrentAccount(account);

                // Load the main menu
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("MenuMain.fxml")));
                Parent root = loader.load();
                MenuMainController controller = loader.getController();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                controller.setNameLabel(userName.getText());
            } else {
                loginOutput.setText("Login credentials have been rejected: " + result.toString());
            }
        } else {
            loginOutput.setText("All text fields must be filled in order to attempt a login");
        }
    }
    public void switchToRegister(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Register.fxml")));
        AnchorPane root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        SessionInformation.setCurrentRoot(root);
    }
}
