import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BankAccount { //represents bank account
    private int balance; //stores current balance
    private final Lock lock = new ReentrantLock(); //lock for thread synchronization

    public BankAccount(int balance) { //constructor set balance
        this.balance = balance;
    }

    public void Deposit (int amount) throws InterruptedException { //method to deposit money
        lock.lock(); //lock before modifying balance to ensure mutual exclusion
        try {
            System.out.println("Depositing $" + amount);
            int newBalance = balance + amount; //adds to account balance
            Thread.sleep(1000); //delay
            balance = newBalance; //sets the attribute to updated balance
            System.out.println("Deposit complete. New balance: $" + balance);
        } catch (InterruptedException e) {
            throw new InterruptedException("Deposit Interrupted"); //exception handling with message
        } finally {
            lock.unlock(); //ensures lock is released
        }
    }

    public void Withdraw (int amount) throws InterruptedException{
        lock.lock(); //lock before modifying for mutual exclusion
        try {
            if (balance >= amount) { //checks if amount causes overdraft
                System.out.println("Withdrawing $" + amount);
                int newBalance = balance - amount;
                Thread.sleep(1000);
                balance = newBalance; //sets attribute to updated balance
                System.out.println("Withdrawal complete. New balance: $" + balance);
            }
        } catch (InterruptedException e) {
            throw new InterruptedException("Withdraw Interrupted"); //exception handling
        } finally {
            lock.unlock(); //lock released
        }
    }

    public int getBalance() {
        return balance;
    } //gets current balance
}

class Transaction implements Runnable { //execute transaction using threads
    private final BankAccount account; //account where transaction happens
    private final String transactionType; //withdraw or deposit
    private final int amount; //amount for the transaction

    public Transaction(BankAccount account, String transactionType, int amount) { //constructor that takes in the conditions
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    @Override
    public void run() { //executes transactions
        if ("d".equalsIgnoreCase(transactionType)) { //check that transaction type matches deposit
            try {
                account.Deposit(amount); //call deposit method
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if ("w".equalsIgnoreCase(transactionType)) { //check that transaction type matches withdraw
            try {
                account.Withdraw(amount); //call withdraw method
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
public class OS_Project1 {
    public static void main(String[] args) throws InterruptedException{ //driver class

        BankAccount account = new BankAccount(10); //create bank account with initial balance of 10

        Thread[] transactions = new Thread[] { //make multiple threads
                new Thread(new Transaction(account, "d", 5)), //d for deposit
                new Thread(new Transaction(account, "w", 3)), //w for withdraw
                new Thread(new Transaction(account, "d", 10)),
                new Thread(new Transaction(account, "w", 1)),
                new Thread(new Transaction(account, "d", 3))
        };

        for (Thread myT : transactions) { //start threads
            myT.start();
        }

        for (Thread myT : transactions) { //wait for threads to finish before printing balance
            try {
                myT.join(); //syncs the threads with main thread
            } catch (InterruptedException e) {
                throw new InterruptedException ("Wait Interrupted");
            }
        }
        System.out.println("Final balance: $" + account.getBalance()); //print final balance
    }
}
