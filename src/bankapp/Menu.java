package bankapp;

import java.util.Scanner;
import java.util.LinkedList;

import bankapp.User;

import bankapp.BankAccount;

interface InputCaretaker {
	double getDouble();
	int getInt();
	String getString();
}

class ScannerCaretaker implements InputCaretaker {
	private Scanner scanner;

	public ScannerCaretaker(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public double getDouble() {
		return scanner.nextDouble();
	}

	@Override
	public String getString() {
		return scanner.nextLine();
	}

	@Override
	public int getInt() {
		return scanner.nextInt();
	}
}

public class Menu {

	private InputCaretaker caretaker;
	private LinkedList<BankAccount> accounts = new LinkedList<>();
    private LinkedList<Integer> accountIDs = new LinkedList<>();
	private BankAccount account;
	private User user;
	private Boolean firstIteration = true;

	//Constructor
	public Menu(InputCaretaker caretaker) {
		this.caretaker = caretaker;
		this.accounts = new LinkedList<>();
        this.accountIDs = new LinkedList<>();
	}
	//not tested
	public static void main(String[] args) {
		Menu mainMenu = new Menu(new ScannerCaretaker(new Scanner(System.in)));
		while (true) {
			if (mainMenu.firstIteration) {
				mainMenu.displayFirstIterationName();
				String name = mainMenu.getString();
				mainMenu.displayFirstIterationPassword();
				String password = mainMenu.getString();
				mainMenu.displayFirstIterationEnd();
				User user = new User(name, password);
				mainMenu.setUser(user);
				mainMenu.addAccount(true, user);
			}
			mainMenu.displayingOptions();
			int option = mainMenu.getOption();
			while (option < 1 || option > 9) {
				System.out.println("Invalid option!");
				mainMenu.displayingOptions();
				option = mainMenu.getOption();
			}
			if (option == 9) {
				System.out.println("Exiting...");
				System.exit(0);
			}
			mainMenu.executeSelectedOption(option);
			mainMenu.firstIteration = false;
		}
	}
	public void setUser(User user) {
		this.user = user;
	}
	//Code that just displays stuff - no tests needed
	public void displayingOptions() {
		System.out.println("Choose from the following options: ");
		System.out.println("1. Deposit");
		System.out.println("2. Withdraw");
		System.out.println("3. Transfer");
		System.out.println("4. Check Balance");
		System.out.println("5. Show Account Information");
		System.out.println("6. Switch Account");
		System.out.println("7. Create Account");
		System.out.println("8. Delete Account");
		System.out.println("9. Exit");
	}
	public void executeSelectedOption(int option){
		System.out.println("You have selected option: " + option);
		if (option == 1) {
			System.out.println("How much money do you want to deposit?");
			double amount = getValidUserDeposit();
			processingUserDeposit(amount);
		} 
		else if (option == 2) {
			System.out.println("How much money do you want to withdraw?");
			double amount = getValidUserWithdraw();
			processingUserWithdraw(amount);
		} 
		else if (option == 3) {
			if (accounts.size() < 2) {
				System.out.println("You need at least two accounts to transfer money");
				return;
			}
			System.out.println("Choose from the following account IDs to transfer to: ");
			accountIDs.clear();
			for (BankAccount account : accounts) {
				System.out.println("Account ID: " + account.getID());
				accountIDs.add(account.getID());
			}
			int recipientID = getOption();
			while (!accountIDs.contains(recipientID)) {
				System.out.println("Invalid account ID!");
				System.out.println("Choose from the following account IDs to transfer to: ");
				for (BankAccount account : accounts) {
					System.out.println("Account ID: " + account.getID());
				}
				recipientID = getOption();
			}
		} 
		else if (option == 4) {
			System.out.println("Your balance is: " + account.getBalance());
		} 
		else if (option == 5) {
			System.out.println("Account Information: ");
			System.out.println("Account ID: " + account.getID());
			System.out.println("Account Type: " + (account.isChecking() ? "Checking" : "Savings"));
			System.out.println("Account Balance: " + account.getBalance());
		} 
		else if (option == 6) {
			if (accounts.size() < 2) {
				System.out.println("You need at least two accounts to switch accounts");
				return;
			}
			System.out.println("Choose the account ID to switch to: ");
			accountIDs.clear();
			for (BankAccount account : accounts) {
				System.out.println("Account ID: " + account.getID());
				accountIDs.add(account.getID());
			}
			int switchID = getOption();
			while (!accountIDs.contains(switchID)) {
				System.out.println("Invalid account ID!");
				switchID = getOption();
			}
			account = accounts.get(accountIDs.indexOf(switchID));
		} 
		else if (option == 7) {
			System.out.println("Do you want to create a (1) checking account or a (2) savings account? ");
			int accountType = getOption();
			while (accountType != 1 && accountType != 2) {
				System.out.println("Invalid account type!");
				System.out.println("Do you want to create a (1) checking account or a (2) savings account? ");
				accountType = getOption();
			}
			System.out.println("Creating account...");
			if (accountType == 1) {
				accounts.add(new BankAccount(true, user, 0.0));
			} 
			else {
				accounts.add(new BankAccount(false, user, 0.0));
			}
		} 
		else if (option == 8) {
			if (accounts.size() < 1) {
				System.out.println("You need at least one account to delete an account");
				return;
			}
			System.out.println("Choose from the following account IDs to delete an account: ");
			accountIDs.clear();
			for (BankAccount account : accounts) {
				System.out.println("Account ID: " + account.getID());
				accountIDs.add(account.getID());
			}
			int deleteID = getOption();
			while (!accountIDs.contains(deleteID)) {
				System.out.println("Invalid account ID!");
				System.out.println("Choose from the following account IDs to delete an account: ");
				for (BankAccount account : accounts) {
					System.out.println("Account ID: " + account.getID());
				}
				deleteID = getOption();
			}
			accounts.remove(accountIDs.indexOf(deleteID));
		} 
	}

	public int getOption() {
		return caretaker.getInt();
	}

	public void displayFirstIterationName() {
		System.out.println("Welcome to the bank!");
		System.out.println("Enter your name: ");
	}

	public void displayFirstIterationPassword() {
		System.out.println("Enter your password: ");
	}

	public void displayFirstIterationEnd() {
		System.out.println("Thank you for entering your name and password!");
		System.out.println("You can now proceed to the main menu. Have Fun!");
	}

	public String getString() {
		return caretaker.getString();
	}
	
	//Code that gets user input
	//No tests needed...for now (probably discuss in future class)
	public double getValidUserDeposit() {
		double amount = caretaker.getDouble();
		while(amount <= 0) {
			System.out.println("Invalid value!");
			System.out.println("How much money do you want to deposit?");
			amount = caretaker.getDouble();
		}
		return amount;
	}

	public double getValidUserWithdraw() {
		double amount = caretaker.getDouble();
		while(amount <= 0 || amount > account.getBalance()) {
			System.out.println("Invalid value!");
			System.out.println("How much money do you want to withdraw?");
			amount = caretaker.getDouble();
		}
		return amount;
	}

	public void processingUserWithdraw(double amount) {
		account.withdraw(amount);
		System.out.println("Your balance is now: " + account.getBalance());
	}
	
	//Does work - needs tests
	public void processingUserDeposit(double amount) {
		account.deposit(amount);
		System.out.println("Your balance is now: " + account.getBalance());
	}

	public void addAccount(Boolean isChecking, User user) {
		BankAccount newAccount = new BankAccount(isChecking, user, 0.0);
		user.addBankAccount(newAccount, user.getPassword());
		this.accounts.add(newAccount);
		this.accountIDs.add(newAccount.getID());
		this.account = newAccount;
	}

	
	public BankAccount getAccount() {
		return account;
	}
}