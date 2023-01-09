package old.colony.bankaccountapp;

import java.io.*;
import java.util.Scanner;
import java.util.UUID;

public class AccountsFileHandler {
    public static final String TARGET_FILE = "Accounts.txt";
    private static final String NEW_LINE = "\n";
    private static final String SECTION_BREAK = "|";
    private static final String SECTION_BREAK_REGEX = "\\|";
    public static boolean accountExists(String username) throws IOException {
        // Create a file reader
        File file = new File(TARGET_FILE);
        Scanner scan = new Scanner(file);
        if (!scan.hasNextLine()) return false; // empty file

        // Loop through the accounts using a scanner
        String content;
        while (scan.hasNextLine()) {
            content = scan.nextLine();
            // check for a section break
            if (content.contains(SECTION_BREAK)) {
                // now that it has a section break, extract username
                String userName = content.split(SECTION_BREAK_REGEX, 2)[1];

                // compare username to the provided username to see if they match
                if (userName.equals(username)) {
                    System.out.println("returning true");
                    return true;
                }
            }
        }
        // no username was found, return false
        return false;
    }
    // This is an integer
    public static PasswordResult passwordCheck(String username, String password) throws IOException {
        // Create a file reader
        File file = new File(TARGET_FILE);
        Scanner scan = new Scanner(file);
        if (!scan.hasNextLine()) return PasswordResult.FILE_EMPTY; // empty file

        // Loop through the accounts using a scanner
        String content;
        while (scan.hasNextLine()) {
            content = scan.nextLine();
            // check for a section break
            if (content.contains(SECTION_BREAK)) {
                // now that it has a section break, extract username and password
                String[] info = content.split(SECTION_BREAK_REGEX);

                // compare username to the provided username to see if they match
                if (info[1].equals(username) && info[2].equals(password)) {
                    return PasswordResult.VALID;
                } else if (info[1].equals(username)) {
                    return PasswordResult.PASSWORD_MISMATCH;
                }
            }
        }
        // no username was found, return false
        return PasswordResult.USER_NOT_FOUND;
    }
    // Generates a random UUID for indexing purposes
    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }
    // Add an account to the file
    public static StringBuilder accountInformationToString(String username, String password, String accountType, String userId, int balance,
                                                    int deposits, int withdrawls) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId);
        builder.append(SECTION_BREAK);
        builder.append(username);
        builder.append(SECTION_BREAK);
        builder.append(password);
        builder.append(SECTION_BREAK);
        builder.append(accountType);
        builder.append(SECTION_BREAK);
        builder.append(balance);
        builder.append(SECTION_BREAK);
        builder.append(deposits);
        builder.append(SECTION_BREAK);
        builder.append(withdrawls);
        return builder;
    }
    public static void writeAccountToFile(String username, String password, String accountType, String userId, int balance) throws IOException {
        FileWriter writer = new FileWriter("Accounts.txt", true);

        // Format the account in one line - username|password\n. Unfortunately no JSON apis so can't use those
        StringBuilder builder = accountInformationToString(username, password, userId, accountType, balance, 0, 0);
        builder.append(NEW_LINE);
        String info = builder.toString();

        // Add the information then close the writer
        writer.append(info);
        writer.close();
    }

    public static BankAccount accountFromUsername(String username) throws IOException {
        File file = new File(TARGET_FILE);
        Scanner scan = new Scanner(file);
        if (!scan.hasNextLine()) return null; // empty file

        // Loop through the accounts using a scanner
        String content;
        while (scan.hasNextLine()) {
            content = scan.nextLine();
            // check for a section break
            if (content.contains(SECTION_BREAK)) {
                // now that it has a section break, extract username
                String[] info = content.split(SECTION_BREAK_REGEX);

                // compare username to the provided username to see if they match
                if (info[1].equals(username)) {
                    String balance = info[4];
                    int trueBalance = Integer.parseInt(balance);
                    BankAccount account;
                    if (info[3].equals("Checking")) {
                        account = new CheckingAccount(info[1], info[2], info[0], trueBalance, 0.02f);
                    } else {
                        account = new SavingsAccount(info[1], info[2], info[0], trueBalance, 0.02f);
                    }
                    account.setDeposits(Integer.parseInt(info[5]));
                    account.setWithdrawls(Integer.parseInt(info[6]));
                    return account;
                }
            }
        }
        // no username was found, return false
        return null;
    }

    public static void updateAccount(BankAccount acc, boolean updateCredentials) throws IOException {
        // find the user id
        String id = acc.getUserID();

        // now that we have the user ID, try to find the account information
        File file = new File(TARGET_FILE);
        Scanner scan = new Scanner(file);

        // For now, we will be storing the contents to a StringBuilder. This is inefficient, yes,
        // but it will be replaced by SQL or CSV at some point.
        StringBuilder fileContent = new StringBuilder();
        if (!scan.hasNextLine()) return; // empty file

        // Loop through the accounts using a scanner
        String content;
        while (scan.hasNextLine()) {
            content = scan.nextLine();
            if (content.contains(SECTION_BREAK)) {
                String[] info = content.split(SECTION_BREAK_REGEX);

                // is matching ID? then replace information, otherwise add it unmodified
                if (info[0].equals(id)) {
                    String infoStr = accountInformationToString(acc.getUserName(), acc.getPassWord(), acc.getAccountType(),
                            id, acc.getBalance(), acc.getDeposits(), acc.getWithdrawls()).toString();

                    fileContent.append(infoStr);
                } else {
                    fileContent.append(content);
                }
                fileContent.append(NEW_LINE);
            }
        }
        // With the StringBuilder contents, write to the file
        FileWriter writer = new FileWriter(TARGET_FILE, false);
        writer.write(fileContent.toString());
        writer.close();
    }
}
