package old.colony.bankaccountapp;

public class BankAccount {
    private String userName;
    private String passWord;
    protected boolean active;
    private String userID;
    private int balance;
    private int amountOfDeposits;
    private int amountOfWithdrawls;
    private double annualInterestRate;
    private double lastTransaction;

    private String accountType;
    public BankAccount(String name, String pass, String userID, int startBalance, float interestRate, String type) {
        userName = name;
        passWord = pass;
        this.userID = userID;
        balance = startBalance;
        annualInterestRate = interestRate;
        accountType = type;
    }

    //TODO: Make into one function (what is this supposed to mean?)
    public void deposit(int newBalance) {
        balance += newBalance;
        amountOfDeposits++;
        lastTransaction = newBalance;
    }

    public void withdraw(int amount) {
        balance -= amount;
        amountOfWithdrawls++;
        lastTransaction = -amount;
    }

    public void calcInterest() {
        double monthlyInterestRate = annualInterestRate / 12;
        double monthlyInterest = balance * monthlyInterestRate;
        balance += monthlyInterest;
    }

    public void monthlyProcess(int monthlyService) {
        balance -= monthlyService;
        amountOfDeposits = 0;
        amountOfWithdrawls = 0;
    }

    public int getBalance() {
        return balance;
    }
    public int getDeposits() {
        return amountOfDeposits;
    }
    public void setDeposits(int amt) { amountOfDeposits = amt; }
    public void setWithdrawls(int amt) { amountOfWithdrawls = amt; }
    public int getWithdrawls() {
        return amountOfWithdrawls;
    }
    public String getLastTransaction() {
        if (lastTransaction > 0) {
            return "Deposited " + (lastTransaction/100) + " dollars";
        } else if (lastTransaction < 0) {
            return "Withdrew " + (-lastTransaction/100) + " dollars";
        } else {
            return "No transaction was made yet";
        }
    }
    public String getUserID() {
        return userID;
    }
    public boolean isActive() {
        return active;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassWord() {
        return passWord;
    }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    public String getAccountType() {
        return accountType;
    }
    private String formatString = "$%.2f";
    public String toString() {
        return String.format(formatString, balance/100.0);
    }
}
