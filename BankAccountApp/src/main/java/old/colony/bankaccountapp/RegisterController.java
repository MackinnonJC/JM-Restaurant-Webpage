package old.colony.bankaccountapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;
public class RegisterController {
    // Creating this here to use later without numerous rebuilds
    private final String NEW_LINE = "\n";
    private final String SECTION_BREAK = "|";
    @FXML
    private TextField userName;
    @FXML
    private TextField passWord;
    @FXML
    private Label registerOutput;
    @FXML
    private RadioButton radioButton1;
    @FXML
    private RadioButton radioButton2;
    private BankAccount addAccount() throws IOException {
        String name = userName.getText();
        String pass = passWord.getText();
        // Checking for == false for now so I can easily change to == null if needed
        if (AccountsFileHandler.accountExists(name) == false) {
            String userId = AccountsFileHandler.generateUserId();
            BankAccount newAccount;

            // check whcih radiobutton was pressed
            ToggleGroup group = radioButton1.getToggleGroup();
            if (group.getSelectedToggle().equals(radioButton1)) {
                newAccount = new SavingsAccount(name, pass, userId, 10000, 0.02f);
            } else {
                newAccount = new CheckingAccount(name, pass, userId, 10000, 0.02f);
            }
            // now handle the file
            AccountsFileHandler.writeAccountToFile(name, pass, userId, newAccount.getAccountType(), 10000);

            return newAccount;
        } else {
            registerOutput.setText("An account with that username already exists, please select a different username.");
            return null;
        }
    }
    public void attemptRegister(ActionEvent event) throws IOException {
        if (!userName.getText().isBlank()) {
            // Attempt creating a bank account, if it errors then alert the user
            try {
                // If it doesn't error, ensure it at least created an account
                BankAccount account = addAccount();
                if (account != null) {
                    // since it was a success, update the session information
                    SessionInformation.setCurrentAccount(account);

                    // load the main menu
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("MenuMain.fxml")));
                    Parent root = loader.load();
                    MenuMainController controller = loader.getController();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                    controller.setNameLabel(userName.getText());
                }
            } catch (IOException e) {
                System.out.println("There was an error making the account");
            } // No finally will be used as the user should not be redirected if an account creation failed
        }
    }
    public void switchToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        AnchorPane root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        SessionInformation.setCurrentRoot(root);
    }
}
