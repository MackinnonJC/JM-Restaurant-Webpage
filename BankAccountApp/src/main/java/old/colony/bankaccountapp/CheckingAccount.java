package old.colony.bankaccountapp;

public class CheckingAccount extends BankAccount {

    public CheckingAccount(String userName, String passWord, String userId, int balance, float annualRate) {
        super(userName, passWord, userId, balance, annualRate, "Checking");
        active = true;
    }

    @Override
    public void deposit(int amount) {
        super.deposit(amount);
    }

    @Override
    public void withdraw(int amount) {
        if (active) {
            super.withdraw(amount);
        } else {
            System.out.println("Inactive account");
        }
    }

    @Override
    public void monthlyProcess(int serviceCharge) {
        int tempAmount = getWithdrawls();
        if (tempAmount > 4) {
            super.monthlyProcess(tempAmount - 4);
        }
    }
}
