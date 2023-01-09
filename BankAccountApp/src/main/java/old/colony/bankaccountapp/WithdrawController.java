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

public class WithdrawController {

    @FXML
    private TextField withdrawAmount;
    @FXML
    private Label withdrawOutput;
    private void switchScene(ActionEvent event, String newScene) throws IOException {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(newScene)));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        SessionInformation.setCurrentRoot(root);
    }
    public void attemptWithdraw(ActionEvent event) throws IOException {
        BankAccount currentAccount = SessionInformation.getCurrentAccount();
        if (!withdrawAmount.getText().isBlank() && currentAccount.isActive()) {
            // int casting is involved, so try/catch to ensure proper input
            try {
                // user can input up to 2 places decimal values, so let them do so but multiply by 100 to get int
                int amount = (int)(Double.parseDouble(withdrawAmount.getText()) * 100);
                if (amount < 0) { // first ensure it is not negative
                    withdrawOutput.setText("ERROR: Can not withdraw negative amounts, consider depositing instead");
                }
                else if (amount <= currentAccount.getBalance()) { // must be a valid deposit
                    // withdraw the amount and update the record
                    currentAccount.withdraw(amount);
                    SessionInformation.updateRecord();

                    // output the result to the user, if it's now inactive it will tell them
                    String appendActive = currentAccount.isActive() ? "" : ". Since the balance dropped below $25.00, the account is now inactive. Deposit funds to bring it back over $25.00 to reactivate your account.";
                    withdrawOutput.setText("Successfully withdrew $"
                            + withdrawAmount.getText() + ", new balance is "
                            + currentAccount + appendActive);
                } else {
                    withdrawOutput.setText("ERROR: The amount withdrew cannot exceed the balance. Attempted to withdraw $"
                    + withdrawAmount.getText() + ", but the current balance is "
                    + currentAccount);
                }
            } catch (NumberFormatException e) {
                withdrawOutput.setText("ERROR: Provided amount is not a number, please try again.");
            }
            // Clear the field regardless of errors
            withdrawAmount.clear();
        } else if (currentAccount.isActive()) {
            withdrawOutput.setText("ERROR: Text field is blank, please input a number.");
        }
    }
    // If the account is active, then replace the placeholder output with a warning message.
    public void initialize() {
        BankAccount currentAccount = SessionInformation.getCurrentAccount();
        if (!currentAccount.isActive()) {
            withdrawOutput.setText("The account is currently inactive, so withdrawing will have no effect. In order to reactivate your account, deposit funds so that your account has over $25.00 in its' balance.");
        }
    }
    public void returnWithdraw(ActionEvent event) throws IOException {
        switchScene(event, "MenuMain.fxml");
    }
    public void logout(ActionEvent event) throws IOException {
        switchScene(event, "Login.fxml");
    }
}
