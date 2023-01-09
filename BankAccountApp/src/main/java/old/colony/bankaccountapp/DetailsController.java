package old.colony.bankaccountapp;

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

public class DetailsController {

    @FXML
    private Label accountBalance;
    @FXML
    private Label accountTransactions;
    @FXML
    private Label accountActive;
    @FXML
    private Label accountType;
    private void switchScene(ActionEvent event, String newScene) throws IOException {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(newScene)));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        SessionInformation.setCurrentRoot(root);
    }
    // Update account information when the page is loaded
    public void initialize() {
        BankAccount currentAccount = SessionInformation.getCurrentAccount();

        // Update the balance
        String balanceString = currentAccount.toString();
        accountBalance.setText(balanceString);

        // Update the transactions
        accountTransactions.setText(Integer.toString(currentAccount.getDeposits() + currentAccount.getWithdrawls()));

        // Update whether the account is active
        accountActive.setText(currentAccount.isActive() ? "Yes" : "No");

        // Lastly update the account type
        accountType.setText(currentAccount.getAccountType());
    }
    public void returnAccount(ActionEvent event) throws IOException {
        switchScene(event, "MenuMain.fxml");
    }
    public void logout(ActionEvent event) throws IOException {
        switchScene(event, "Login.fxml");
    }
}
