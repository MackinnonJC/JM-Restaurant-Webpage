package old.colony.bankaccountapp;

public class SavingsAccount extends BankAccount {

    public SavingsAccount(String userName, String passWord, String userId, int balance, float annualRate) {
        super(userName, passWord, userId, balance, annualRate, "Saving");
        if (getBalance() >= 25) {
            active = true;
        } else {
            active = false;
        }
    }

    @Override
    public void deposit(int amount) {
        super.deposit(amount);
        if (super.getBalance() >= 2500) {
            active = true;
        } else {
            System.out.println("Inactive account");
        }
    }

    @Override
    public void withdraw(int amount) {
        if (active) {
            super.withdraw(amount);
            if (super.getBalance() < 2500) {
                active = false;
            }
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
        if (getBalance() < 25) {
            active = false;
        }
    }
}
