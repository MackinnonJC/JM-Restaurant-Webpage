package old.colony.bankaccountapp;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

// This holds information about the current session, so that every page can access the current account.
// Might be a little insecure, but it should only be filled on successful login.
// User can technically hack their balance, but this should be impossible once we move it to SQL
public class SessionInformation {
    // Declare as a bank account so polymorphism allows us to store savings and checking
    private static BankAccount currentAccount;
    // getChildren is hidden in Parent, which is why this has to be AnchorPane
    private static AnchorPane currentRoot;
    private static Alert currentAlert = new Alert(Alert.AlertType.ERROR);
    public static BankAccount getCurrentAccount() {
        return currentAccount;
    }
    public static void setCurrentAccount(BankAccount account) {
        currentAccount = account;
    }
    public static void setCurrentRoot(AnchorPane root) {
        currentRoot = root;
    }
    public static void showAlert(String content) {
        currentAlert.setContentText(content);
        currentAlert.show();
    }
    public static void updateRecord() throws IOException {
        //showAlert("Line 999999 or something: Missing semicolon. This is just a sample alert");
        try {
            AccountsFileHandler.updateAccount(currentAccount, false);
        } catch (IOException e) {
            System.out.println("Critical error");
            System.out.println(e.getMessage());
            showAlert(e.getMessage());
        }
    }
}
