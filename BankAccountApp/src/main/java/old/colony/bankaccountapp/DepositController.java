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

public class DepositController {
    @FXML
    private TextField depositAmount;
    @FXML
    private Label depositOutput;
    private void switchScene(ActionEvent event, String newScene) throws IOException {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(newScene)));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        SessionInformation.setCurrentRoot(root);
    }
    public void attemptDeposit(ActionEvent event) throws IOException {
        BankAccount currentAccount = SessionInformation.getCurrentAccount();
        if (!depositAmount.getText().isBlank()) {
            // int casting is involved, so try/catch to ensure proper input
            try {
                // user can input up to 2 places decimal values, so let them do so but multiply by 100 to get int
                int amount = (int)(Double.parseDouble(depositAmount.getText()) * 100);
                if (amount > 0) { // must be a valid deposit
                    currentAccount.deposit(amount);
                    SessionInformation.updateRecord();

                    // Output the result of the deposit, currentAccount's tostring is the balance
                    depositOutput.setText("Successfully deposited $"
                            + depositAmount.getText()
                            + ", new balance is "
                            + currentAccount);
                } else {
                    depositOutput.setText("ERROR: Can not deposit negative amounts, consider withdrawing instead");
                }
            } catch (NumberFormatException e) {
                depositOutput.setText("ERROR: Provided amount is not a number, please try again.");
            }
            // Clear the field regardless of errors
            depositAmount.clear();
        } else {
            depositOutput.setText("ERROR: Text field is blank, please input a number.");
        }
    }
    public void returnDeposit(ActionEvent event) throws IOException {
        switchScene(event, "MenuMain.fxml");
    }
    public void logout(ActionEvent event) throws IOException {
        switchScene(event, "Login.fxml");
    }
}
