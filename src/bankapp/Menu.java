package bankapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class Menu {
	private InputCaretaker caretaker;
	private PersonalCapital currentAccount;
	private User currentUser;
	private Boolean firstIteration = true;
	private List<Integer> accountIDs;
	private List<String> contextOptions;
	
	private BankDatabase database;

	public Menu(Scanner scanner) {
		this(new ScannerCaretaker(scanner));
	}

	// Constructor
	public Menu(InputCaretaker caretaker) {
		this.caretaker = caretaker;
		this.database = new BankDatabase("bank");
		this.accountIDs = new LinkedList<Integer>();
		initializeContextOptions();
	}

	private void initializeContextOptions() {
        contextOptions = new LinkedList<>();
        contextOptions.add("Banking");
        contextOptions.add("Finances");
        contextOptions.add("Investments");
        contextOptions.add("Loans");
        contextOptions.add("Mortgages");
        contextOptions.add("Retirement");
        contextOptions.add("Savings");
        contextOptions.add("Budgeting");
        contextOptions.add("Credit Scores");
        contextOptions.add("Financial Advice");
    }

	// not tested
	public static void main(String[] args) {
		runMainMenu();
	}

	private static void runMainMenu() {
		Menu mainMenu = new Menu(new ScannerCaretaker(new Scanner(System.in)));
		while (true) {
			if (mainMenu.firstIteration) {
				mainMenu.initizalizeBank();
			}
			mainMenu.getExecuteOptions();
		}
	}

	private void getExecuteOptions() {
		displayingOptions();
		int option = getOption();
		while (option < 1 || option > 11) {
			System.out.println("Invalid option!");
			displayingOptions();
			option = getOption();
		}
		if (option == 11) {
			System.out.println("Exiting...");
			database.saveBank();
			System.exit(0);
		}
		executeSelectedOption(option);
		firstIteration = false;
	}

	private void printUsers() {
		System.out.println("List of users: ");
		for (String username : database.getUserList()) {
			System.out.println(username);
		}
		System.out.println();
	}

	private void initizalizeBank() {
		if (database.loadBank()) {
			System.out.println("Database files loaded! \n");
			printUsers();
			String userSelect = "";
			while (database.getUser(userSelect) == null) {
				if (userSelect.equals("new")) {
					createNewAccounts();
					return;
				}
				System.out.println("Enter a valid username, or type \"new\" to make a new account: ");
				userSelect = getString();
			}
			String password = "";
			while (!database.getUser(userSelect).getPassword().equals(password)) {
				System.out.println("Enter your password: ");
				password = getString();
			}
			currentUser = database.getUser(userSelect);
			currentAccount = currentUser.getBankAccounts().getFirst();
		} else {
			createNewAccounts();
		}
	}

	private void createNewAccounts() {
		displayFirstIterationName();
		String name = getString();
		displayFirstIterationPassword();
		String password = getString();
		displayFirstIterationEnd();
		User user = new User(name, password);
		database.addUser(user);
		setUser(user);
		currentAccount = new BankAccount(true, user, 10000, database.createUniqueID());
		database.addAccount(currentAccount);
	}

	public void setUser(User user) {
		this.currentUser = user;
	}

	// Code that just displays stuff - no tests needed
	public void displayingOptions() {
		List<String> options = Arrays.asList("Choose from the following options: ", "1. Deposit", "2. Withdraw",
				"3. Transfer", "4. Check Balance", "5. Show Account Information", "6. Switch Account",
				"7. Create Account", "8. View Assets & Liabilities", "9. Delete Account", "10. Talk to a Friend", "11. Exit");
		for (String option : options) {
			System.out.println(option);
		}
	}

	public void executeSelectedOption(int option) {
		System.out.println("You have selected option: " + option);
		switch (option) {
		case 1:
			menuDeposit();
			break;
		case 2:
			menuWithdraw();
			break;
		case 3:
			menuTransfer();
			break;
		case 4:
			menuCheckBalance();
			break;
		case 5:
			menuAccountInformation();
			break;
		case 6:
			menuSwitchAccounts();
			break;
		case 7:
			menuCreateAccount();
			break;
		case 8:
			assetsAndLiabilities();
			break;
		case 9:
			menuDeleteAccount();
			break;
		case 10:
			talkToFriend();
			break;
		}
	}

	public void menuDeposit() {
		System.out.println("How much money do you want to deposit?");
		double amount = getValidUserDeposit();
		processingUserDeposit(amount);
	}

	public void menuWithdraw() {
		System.out.println("How much money do you want to withdraw?");
		double amount = getValidUserWithdraw();
		processingUserWithdraw(amount);
	}

	public void menuTransfer() {
		if (database.numberAccounts() < 2 && currentUser.getBankAccounts().size() >= 1) {
			System.out.println(database.numberAccounts() + " " + currentUser.getBankAccounts().size());
			System.out.println("You need at least one account to transfer money, or you need one other account from a different user to transfer to.");
			return;
		} else if (database.numberAccounts() < 2 && currentUser.getBankAccounts().size() < 2) {
			System.out.println("You need at least one account to transfer money.");
			return;
		}
		
		System.out.println("Choose from the following account IDs to transfer from: ");
		for (PersonalCapital account : currentUser.getBankAccounts()) {
			System.out.println("Account ID: " + account.getID());
		}

		int senderID = getOption();
		PersonalCapital senderAccount = null;
		for (PersonalCapital account : currentUser.getBankAccounts()) {
			if (account.getID() == senderID) {
				senderAccount = account;
				break;
			}
		}

		while (senderAccount == null) {
			System.out.println("Invalid account ID!");
			System.out.println("Choose from the following account IDs to transfer from: ");
			for (PersonalCapital account : currentUser.getBankAccounts()) {
				System.out.println("Account ID: " + account.getID());
			}
			senderID = getOption();
			for (PersonalCapital account : currentUser.getBankAccounts()) {
				if (account.getID() == senderID) {
					senderAccount = account;
					break;
				}
			}
		}

		System.out.println("Choose from the following account IDs to transfer to: ");
		for (Entry<Integer, String> account : database.getAllBankAccounts()) {
			if (account.getKey() != senderAccount.getID()) {
				System.out.println("Account ID: " + account.getKey());
			}
		}

		int recipientID = getOption();
		PersonalCapital recipientAccount = null;
		recipientAccount = database.getAccount(recipientID);
		if (recipientAccount.getID() == senderAccount.getID()) {
			recipientAccount = null;
		}

		while (recipientAccount == null) {
			System.out.println("Invalid account ID!");
			System.out.println("Choose from the following account IDs to transfer to: ");
			for (Entry<Integer, String> account : database.getAllBankAccounts()) {
				if (account.getKey() != senderAccount.getID()) {
					System.out.println("Account ID: " + account.getKey());
				}
			}
			recipientID = getOption();
			for (Entry<Integer, String> account : database.getAllBankAccounts()) {
				if (account.getKey() == recipientID && account.getKey() != senderAccount.getID()) {
					recipientAccount = database.getAccount(recipientID);
					break;
				}
			}
		}

		System.out.println("Enter the amount to transfer: ");
		double amount = getValidUserWithdraw();

		senderAccount.transfer(amount, recipientAccount);

		System.out.println("Transfer successful!");
		System.out.println("Sender Account Balance: " + senderAccount.getBalance());
		System.out.println("Recipient Account Balance: " + recipientAccount.getBalance());
	}

	public void menuCheckBalance() {
		System.out.println("Your balance is: " + currentAccount.getBalance());
	}

	public void menuAccountInformation() {
		System.out.println("Bank Account Information: ");
		for (BankAccount account : currentUser.getBankAccounts()) {
			System.out.println("Account ID: " + account.getID());
			System.out.println("Account Type: " + (account.isChecking() ? "Checking" : "Savings"));
			System.out.println("Account Balance: " + account.getBalance());
		}
	}

	public void menuSwitchAccounts() {
		if (currentUser.getAssetList().size() < 2) {
			System.out.println("You need at least two accounts to switch accounts");
			return;
		}
		System.out.println("Choose the account ID to switch to: ");
		accountIDs.clear();
		for (PersonalCapital account : currentUser.getBankAccounts()) {
			System.out.println("Account ID: " + account.getID());
			accountIDs.add(account.getID());
		}
		int switchID = getOption();
		while (!accountIDs.contains(switchID)) {
			System.out.println("Invalid account ID!");
			switchID = getOption();
		}
		currentAccount = database.getAccount(switchID);
	}

	public void menuCreateAccount() {
		System.out.println("Do you want to create a (1) checking account or a (2) savings account? ");
		int accountType = getOption();
		while (accountType != 1 && accountType != 2) {
			System.out.println("Invalid account type!");
			System.out.println("Do you want to create a (1) checking account or a (2) savings account? ");
			accountType = getOption();
		}
		System.out.println("Creating account...");
		if (accountType == 1) {
			BankAccount checking = new BankAccount(true, currentUser, 0.0, database.createUniqueID());
			database.addAccount(checking);
		} else {
			BankAccount savings = new BankAccount(false, currentUser, 0.0, database.createUniqueID());
			database.addAccount(savings);
		}
	}

	public void menuDeleteAccount() {
		if (currentUser.getAssetList().size() < 1) {
			System.out.println("You need at least one account to delete an account");
			return;
		}
		System.out.println("Choose from the following account IDs to delete an account (you cannot delete debts): ");
		accountIDs.clear();
		System.out.println("Assets:");
		for (PersonalCapital account : currentUser.getAssetList()) {
			System.out.println("Account ID: " + account.getID());
			accountIDs.add(account.getID());
		}
		int deleteID = getOption();
		while (!accountIDs.contains(deleteID)) {
			System.out.println("Invalid account ID!");
			System.out.println("Choose from the following account IDs to delete an account: ");
			for (PersonalCapital account : currentUser.getAssetList()) {
				System.out.println("Account ID: " + account.getID());
			}
			deleteID = getOption();
		}
		PersonalCapital toRemove = database.getAccount(deleteID);
		database.removeAccount(toRemove);
		currentUser.removeAsset(toRemove);

	}

	public void talkToFriend() {
		System.out.println("Select a context to discuss with your friend:");
        for (int i = 0; i < contextOptions.size(); i++) {
            System.out.println((i + 1) + ". " + contextOptions.get(i));
        }
        int contextChoice = getOption();
        while (contextChoice < 1 || contextChoice > contextOptions.size()) {
            System.out.println("Invalid context choice!");
            contextChoice = getOption();
        }
        String selectedContext = contextOptions.get(contextChoice - 1);
        System.out.println("You have selected the context: " + selectedContext);
        System.out.println("Enter your message to send to your friend:");
        String userInput = getString();
        String response;
		try {
			response = LLMInterface.sendQueryToLLM(userInput, selectedContext);
			System.out.println("Friend's response: " + response);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
	
	public int getValidAccountNumber() {
		accountIDs.clear();
		for (PersonalCapital account : currentUser.getBankAccounts()) {
			System.out.println("Account ID: " + account.getID());
			accountIDs.add(account.getID());
		}
		int switchID = getOption();
		while (!accountIDs.contains(switchID)) {
			System.out.println("Invalid account ID!");
			switchID = getOption();
		}
		return switchID;
	}

	public void assetsAndLiabilities() {
		boolean validAnswer = false;
		while (validAnswer == false) {
			System.out.println("Choose from the following options: ");
			System.out.println("1. Access Assets");
			System.out.println("2. Acess Liabilities");
			System.out.println("3. Exit");
			int option = caretaker.getInt();
			if (option == 1) {
				validAnswer = true;
				menuAssets();
			} else if (option == 2) {
				validAnswer = true;
				menuLiabilities();
			} else if (option == 3) {
				validAnswer = true;
			} else {
				System.out.println("Invalid option. Please type 1, 2, or 3");
			}
		}
	}

	public void menuAssets() {
		System.out.println("You are now viewing assets.");
		// will fix below line, discussing implementing the lists into bank account
		// class
		System.out.println("Your total assets' liquid value is " + currentUser.getAssetBalance());
		boolean exit = false;
		while (exit != true) {
			System.out.println("What would you like to do next?");
			System.out.println("1. Get a list of current assets and values");
			System.out.println("2. View Retirement Fund");
			System.out.println("3. Exit");
			int option = caretaker.getInt();
			if (option == 1) {
				for (PersonalCapital pc : currentUser.getAssetList()) {
					int id = pc.getID();// fix ID method
					Double val = pc.getLiquidValue();
					System.out.println(id + " value: " + val);
				}
			} else if (option == 2) {
				System.out.println("What would you like to do with your retirement fund?");
				System.out.println("1. Deposit money");
				System.out.println("2. See balance");
				System.out.println("3. Open retirement fund");
				int opt = caretaker.getInt();
				if (opt == 1) {
					if (currentUser.findFund() == null) {
						System.out.println("You cannot do this action since no fund currently exists.");
						continue;
					}
					System.out.println("How much would you like to deposit?");
					double deposit = caretaker.getDouble();
					System.out.println("Which account would you like to pay from? Enter ID number: ");
										int number = getValidAccountNumber();
					currentUser.findFund().addYearlyPayment(database.getAccount(number), deposit);
				} else if (opt == 2) {
					if (currentUser.findFund() == null) {
						System.out.println("You cannot do this action since no fund currently exists.");
						continue;
					}
					System.out.println("Total value of retirement fund is : " + currentUser.findFund().getLiquidValue());
				} else if (opt == 3) {
					System.out.println("Which account would you like to pay from today?");
					int number = getValidAccountNumber();
					System.out.println("How much would you like to intialize?");
					double start = caretaker.getDouble();
					System.out.println("What is your average annual income?");
					double income = caretaker.getDouble();
					System.out.println("What is the annual rate you would like to contribute?");
					double rate = caretaker.getDouble();
					RetirementFund retirementfund = new RetirementFund(rate, income, currentUser, database.createUniqueID());
					retirementfund.deposit(database.getAccount(number).withdraw(start));
					currentUser.addAsset(retirementfund);
				}

			} else if (option == 3) {
				exit = true;
			}

			else {
				System.out.println("Please select 1, 2, or 3");
			}
		}
	}

	public void menuLiabilities() {
		System.out.println("You are now viewing liabilities.");
		// will fix below line, discussing implementing the lists into bank account
		// class
		System.out.println("Your total liabilities' liquid value is " + currentUser.getLiabilityBalance());
		boolean exit = false;
		while (exit != true) {
			System.out.println("What would you like to do next?");
			System.out.println("1. Get a list of current liabilities and values");
			System.out.println("2. Remove a liability");
			System.out.println("3. Add a liability");
			System.out.println("4. Access loans");
			System.out.println("5. Access mortgage");
			System.out.println("6. Exit");
			int option = caretaker.getInt();
			if (option == 1) {
				for (PersonalCapital pc: currentUser.getLiabilityList()) {
					int id2 = pc.getID();
					Double val = pc.getLiquidValue();
					System.out.println(id2 + " value: " + val);
				}
			} else if (option == 2) {
				System.out.println("Which liabilitiy would you like to remove? Enter ID number");
				int idnum = caretaker.getInt();
				boolean success = false;
				for (PersonalCapital x : currentUser.getLiabilityList()) {
					if (x.getID() == idnum) {
						success = currentUser.removeLiability(x);;
					}
				}
				if (success == true) {
					System.out.println("Item removed");
				} else {
					System.out.println("Item not removed");
				}
			} else if (option == 3) {
				System.out.println("Which type of liability would you like to add?");
				System.out.println("1. Mortgage");
				System.out.println("2. Loan");
				int op = caretaker.getInt();
				if (op == 1) {
					System.out.println("What is the name of your Mortgage?");
					String name = caretaker.getString();
					System.out.println("What is the total amount you need to pay?");
					double due = caretaker.getDouble();
					System.out.println("What is the interest rate of your Mortgage?");
					double rate = caretaker.getDouble();
					System.out.println("How many years is your mortgage?");
					int years = caretaker.getInt();
					Mortgage newM = new Mortgage(name, due, rate, years, currentUser);
					currentUser.addLiability(newM);
				} else if (op == 2) {
					// need to add with loan attributes
				}
			} else if (option == 4) {
				// need to discuss with how loans are supposed to be implemented
			} else if (option == 5) {
				System.out.println("You are accessing mortgages.");
				int count = 0;
				for (int i = 0; i < currentUser.getLiabilityList().size(); i++) {
					if (currentUser.getLiabilityList().get(i) instanceof Mortgage) {
						count++;
						System.out.println(count + "." + currentUser.getLiabilityList().get(i).toString());
					}
				}
				boolean exit2 = false;
				System.out.println("Total Mortgages: " + count);
				while (exit2 == false) {
					System.out.println("What would you like to do?");
					System.out.println("1. Pay Mortgage");
					System.out.println("2. See Mortgage Payment Due this Month");
					System.out.println("3. Exit");
					int select = caretaker.getInt();
					if (select == 1) {
						System.out.println("What mortgage would you like to pay? Select number from list abbove");
						int wow = caretaker.getInt();
						Mortgage paying = (Mortgage) currentUser.getLiabilityList().get(wow);
						double total = paying.getInterestPayment() + paying.getMortgagePayment();
						System.out.println(
								"Your mortgage due this month is: " + total + ". How much are you paying today?");
						double money = caretaker.getDouble();
						if (money < total) {
							System.out.println(
									"You are paying less than tht total due for this month. If the rest of the payment is turned in after the due date, a late fee will be added to your account. Proceed?");
							System.out.println("Type 'yes' to continue");
							String choice = caretaker.getString();
							if (choice.equals("yes")) {
							} else {
								exit = true;
							}
						}
						if (exit == true) {
						} else {
							boolean answer = false;
							boolean time = false;
							while (answer == false) {
								System.out.println(
										"Did client turn in payment before or after deadline? Type True for before, False for after");
								String ans = caretaker.getString();
								if (ans.equals("True")) {
									answer = true;
									time = true;
								} else if (ans.equals("False")) {
									answer = true;
									time = false;
								} else {
									System.out.println("Inavlid Answer.");
								}
							}
							System.out.println("Enter the account number  that the user is paying with");
							Integer id = this.getValidAccountNumber();
							PersonalCapital pay = database.getAccount(id);
							double before = pay.getBalance();
							paying.payMortgage(money, pay, time, currentUser);
							if (before - money == pay.getBalance()) {
								System.out.println(
										"Successfully paid. Total overall amount remaining: " + paying.getAmount());
							} else {
								System.out.println(
										"Unsucessful payment. Please check account balance, mortgage, and payment amount.");
							}
						}
					} else if (option == 2) {
						System.out.println("What mortgage would you like to see? Select number from list abbove");
						int wow = caretaker.getInt();
						Mortgage view = (Mortgage) currentUser.getLiabilityList().get(wow);
						double total = view.getInterestPayment() + view.getMortgagePayment();
						System.out.println("Payment of " + total + " due to this month");
					} else if (option == 3) {
						exit = true;
					} else {
						System.out.println("Please select option 1-3.");
					}
				}
			} else if (option == 6) {
				exit = true;
			} else {
				System.out.println("Invalid option. Please select an option 1-6.");
			}

		}
	}

// payment cal
	public static double calculateLoanPayment(double principal, double annualInterestRate, double years) {
		// Convert annual interest rate to monthly rate
		double monthlyInterestRate = annualInterestRate / 100 / 12;

		// Calculate the number of monthly payments
		int numPayments = (int) (years * 12);

		// Calculate the monthly payment using the formula for monthly loan payment
		double monthlyPayment = (principal * monthlyInterestRate)
				/ (1 - Math.pow(1 + monthlyInterestRate, -numPayments));

		return monthlyPayment;
	}

	// Loan set up method
	public double setupLoan() {
		Scanner scanner = new Scanner(System.in);

		// User input for loan details
		System.out.print("Enter the loan amount: $");
		double loanAmount = scanner.nextDouble();

		System.out.print("Enter the annual interest rate (as a percentage): ");
		double annualInterestRate = scanner.nextDouble();

		System.out.print("Enter the loan term in years: ");
		double loanTermYears = scanner.nextDouble();

		// Calculate the monthly loan payment
		double monthlyPayment = calculateLoanPayment(loanAmount, annualInterestRate, loanTermYears);

		// Display the monthly payment to the user
		System.out.printf("Your monthly loan payment is: $%.2f%n", monthlyPayment);
		
		scanner.close();

		return monthlyPayment;
	}

	public double menuLoans() {
		return setupLoan();
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

	// Code that gets user input
	// No tests needed...for now (probably discuss in future class)
	public double getValidUserDeposit() {
		double amount = caretaker.getDouble();
		while (amount <= 0) {
			System.out.println("Invalid value!");
			System.out.println("How much money do you want to deposit?");
			amount = caretaker.getDouble();
		}
		return amount;
	}

	public double getValidUserWithdraw() {
		double amount = caretaker.getDouble();
		while (amount <= 0 || amount > currentAccount.getBalance()) {
			System.out.println("Invalid value!");
			System.out.println("How much money do you want to withdraw?");
			amount = caretaker.getDouble();
		}
		return amount;
	}

	public void processingUserWithdraw(double amount) {
		currentAccount.withdraw(amount);
		System.out.println("Your balance is now: " + currentAccount.getBalance());
	}

	// Does work - needs tests
	public void processingUserDeposit(double amount) {
		currentAccount.deposit(amount);
		System.out.println("Your balance is now: " + currentAccount.getBalance());
	}

	public void setCurrentAccount(BankAccount account) {
		this.currentAccount = account;
	}
	
	public PersonalCapital getCurrentAccount() {
		return this.currentAccount;
	}
	
	public BankDatabase getDatabase() {
		return database;
	}
}