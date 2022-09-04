package com.techelevator;

import com.techelevator.model.AuthenticatedUser;
import com.techelevator.model.Transfer;
import com.techelevator.model.User;
import com.techelevator.model.UserCredentials;
import com.techelevator.services.AccountService;
import com.techelevator.services.AuthenticationService;
import com.techelevator.services.ConsoleService;
import com.techelevator.services.TransferService;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private AccountService accountService = new AccountService(currentUser);
    private TransferService transferService = new TransferService(currentUser);


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        accountService = new AccountService(currentUser);
        transferService = new TransferService(currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {

        BigDecimal balance = accountService.getBalance(currentUser);
        System.out.println("\n Your current account balance is: $" + balance + "\n");
	}

	private void viewTransferHistory() {

        Transfer[]transfers =  transferService.getAllTransfers(accountService.getAccountId(currentUser.getUser().getId()));

		User[]users = transferService.getAllUsers();
        String username = null;

        System.out.println("-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------");

        for (Transfer transfer : transfers) {

            if (accountService.getAccountId(currentUser.getUser().getId()).equals(transfer.getAccountFrom())) {
                username = getUsername(transfer.getAccountTo());
                System.out.println(transfer.getTransferID() + "        To:   " + username + "                " + transfer.getAmount());
            }
            else if (accountService.getAccountId(currentUser.getUser().getId()).equals(transfer.getAccountTo())) {
                username = getUsername(transfer.getAccountFrom());
                System.out.println(transfer.getTransferID() + "        From: " + username + "                " + transfer.getAmount());
            }
        }
        System.out.println("\n---------\n");
        ConsoleService console = new ConsoleService();

        //transferDetails
        Integer transferId = console.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if(transferId == 0) {
               mainMenu();
        }

        Transfer transferDetails = null;

        try {
            transferDetails = transferService.getTransferDetails(transferId);
        }  catch (NoSuchElementException e) {
            System.out.println("Invalid ID Entered.");
            // This doesn't seem to output to console, but it does catch the program from crashing.
        }

        if (transferDetails != null) {
            System.out.println("\n--------------------------------------------\n" +
                    "Transfer Details\n" +
                    "--------------------------------------------\n");
            System.out.println("Id: " + transferDetails.getTransferID() + "\n"
                    + "From: " + accountService.getUsername(transferDetails.getAccountFrom()) + "\n"
                    + "To: " + accountService.getUsername(transferDetails.getAccountTo()) + "\n"
                    + "Type: Send \n"
                    + "Status: Approved \n"
                    + "Amount: " + transferDetails.getAmount());
        }  else {
            System.out.println("Invalid ID Entered.");
        }
    }

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		User[]users = transferService.getAllUsers();
        Transfer transfer = new Transfer();

        if (users != null) {
            System.out.println("-------------------------------------------\n" +
                    "Users\n" +
                    "ID          Name\n" +
                    "-------------------------------------------");
            for (User user : users) {
                System.out.println(user.getId() + "    " + user.getUsername());
            }
            System.out.println("-------------------------------------------");
        }
        ConsoleService console = new ConsoleService();
        // need Integer over int to use equals method in User class
        Integer userTo = console.promptForInt("Enter ID of user you are sending to (0 to cancel):");
        Integer userFrom =  accountService.getByUserId(currentUser.getUser().getId()).getUserId();

        if(userTo == 0) {
            mainMenu();
        } else if(userTo.equals(userFrom)) {
            System.out.println("\nTransaction cancelled: You cannot seed money to yourself! Please try again\n");
        } else if(accountService.getByUserId(userTo) == null) {
            System.out.println("\nTransaction cancelled: Account does not exist. Please try again\n");
        } else {
            System.out.println("Your current balance: " + accountService.getByUserId(userFrom).getBalance());

            BigDecimal amountToSend = console.promptForBigDecimal("Enter amount:");
            if (amountToSend.intValue() <= 0) {
                System.out.println("Transaction Cancelled: Amount entered must be greater than 0.");
            } else if (amountToSend.compareTo(accountService.getBalance(currentUser)) <= 0) {
                // -1, 0, 1 <-- Less than, equal to, greater than

                transfer.setTransferTypeId(2); //refer to tenmo.sql for numerical value
                transfer.setTransferStatusId(2);
                transfer.setAccountFrom(accountService.getByUserId(userFrom).getAccountId());
                transfer.setAccountTo(accountService.getByUserId(userTo).getAccountId());
                transfer.setAmount(amountToSend);

                transferService.addTransfer(transfer);

                BigDecimal remainingBalance = accountService.getBalance(currentUser).subtract(amountToSend);
                accountService.getByUserId(userFrom).setBalance(remainingBalance);
                System.out.println("SUCCESS! \nRemaining Balance: " + accountService.getBalance(currentUser));

            } else {
                System.out.println("Transaction canceled: Not enough money in account.");
            }
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

    private String getUsername(int accountId) {
        String username = null;
        User[] users = transferService.getAllUsers();
        for (User user : users) {
            if (accountService.getAccountId(user.getId()).equals(accountId)) {
                username = user.getUsername();
            }
        }
        return username;
    }

}
