import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
//Main GUI class for the ATM system
public class ATMSystemGUI extends JFrame {
    private Connection connection;
    private ATM atm;
    private String userID;
 // Constructor to initialize the GUI
    public ATMSystemGUI() {
        initializeDB();// Initialize database connection
       atm = new ATM(connection);// Create ATM object
        initializeUI();// Initialize user interface
    }
 // Method to initialize database connection
    private void initializeDB() {
        try {
            // Establish database connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://F://sushant/atm.accdb");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
            System.exit(1);
        }
    }
 // Method to initialize the user interface
    private void initializeUI() {
        setTitle("ATM System");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;
        JLabel userIDLabel = new JLabel("User ID:");
        JTextField userIDField = new JTextField(20);
        JLabel pinLabel = new JLabel("PIN:");
        JPasswordField pinField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
     // ActionListener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userID = userIDField.getText();
                String pin = new String(pinField.getPassword());
                if (authenticateUser(userID, pin)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    openATMOperationsPage();// Open ATM operations page
                }
                 else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials. Please try again.");
                }
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(userIDLabel, constraints);

        constraints.gridx = 1;
        panel.add(userIDField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(pinLabel, constraints);

        constraints.gridx = 1;
        panel.add(pinField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(loginButton, constraints);
     // Add components to the panel using GridBagLayout
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }
 // Method to authenticate user using userID and PIN
    private boolean authenticateUser(String userID, String pin) {
        try {
            // Prepare SQL statement
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM usertable WHERE id = ? AND password = ?");
            statement.setString(1, userID);
            statement.setString(2, pin);

            // Execute query
            ResultSet resultSet = statement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                return true; // Authentication successful
            } else {
                return false; // Authentication failed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        }
    }
    private void openATMOperationsPage() {
        // Instantiate the ATM operations page and make it visible
        ATMOperationsGUI atmOperationsGUI = new ATMOperationsGUI(userID, atm);
        atmOperationsGUI.setVisible(true);
    }
    public static void main(String[] args) {
        // Create and display the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ATMSystemGUI().setVisible(true);
            }
        });
    }
}
//GUI class for ATM operations page
class ATMOperationsGUI extends JFrame {
	JButton j1,j2,j3;
    public ATMOperationsGUI(String userID,ATM atm) {
        setTitle("ATM Operations");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //JPanel panel = new JPanel(new GridBagLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5,5,5,5);
        gbc.ipady = 10; // Add vertical weight to push components to the center
         JButton checkBalanceButton = new JButton("Check Balance");
         JButton withdrawButton = new JButton("Withdraw");
         JButton depositButton = new JButton("Deposit");
         Dimension buttonSize = new Dimension(200, 50);
         checkBalanceButton.setPreferredSize(buttonSize);
         withdrawButton.setPreferredSize(buttonSize);
         depositButton.setPreferredSize(buttonSize);

         checkBalanceButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 // Call the checkBalance method of the ATM class
                 double balance = atm.checkBalance(userID);
                 JOptionPane.showMessageDialog(null, "Your balance is: $" + balance);
             }
         });

         withdrawButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 // Prompt the user for the amount to withdraw
                 String amountString = JOptionPane.showInputDialog("Enter amount to withdraw:");
                 if (amountString != null && !amountString.isEmpty()) {
                     double amount = Double.parseDouble(amountString);
                     // Call the withdraw method of the ATM class
                     boolean success = atm.withdraw(userID, amount);
                     if (success) {
                         JOptionPane.showMessageDialog(null, "Withdrawal successful!");
                     } else {
                         JOptionPane.showMessageDialog(null, "Insufficient funds!");
                     }
                 }
             }
         });

         depositButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 // Prompt the user for the amount to deposit
                 String amountString = JOptionPane.showInputDialog("Enter amount to deposit:");
                 if (amountString != null && !amountString.isEmpty()) {
                     double amount = Double.parseDouble(amountString);
                     // Call the deposit method of the ATM class
                     atm.deposit(userID, amount);
                     JOptionPane.showMessageDialog(null, "Deposit successful!");
                 }
             }
         });

         panel.add(checkBalanceButton, gbc);
         gbc.gridy++;
         panel.add(withdrawButton, gbc);
         gbc.gridy++;
         panel.add(depositButton, gbc);

         getContentPane().add(panel);
     }
 
}
//Class representing the ATM system
class ATM {
	private Connection connection;
	public ATM(Connection connection)
	{
		this.connection=connection;
	}
	// Method to check account balance for a given userID
    public double checkBalance(String userID) {
        // Implement your logic to check balance using userID
    	try {
            // Prepare SQL statement to retrieve the balance for the given userID
            PreparedStatement statement = connection.prepareStatement("SELECT accountbalance FROM usertable WHERE id = ?");
            statement.setString(1, userID);

            // Execute query
            ResultSet resultSet = statement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                // Retrieve the balance from the result set
                double balance = resultSet.getDouble("accountbalance");
                System.out.println("Balance retrieved successfully: " + balance);
                return balance; // Return the balance
            } else {
                // User not found
                JOptionPane.showMessageDialog(null, "User not found.");
                return -1; // Return -1 to indicate an error
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving balance: " + e.getMessage());
            return -1; // Return -1 to indicate an error
        }
    }


    public boolean withdraw(String userID, double amount) {
        // Implement your logic to withdraw money using userID and amount
    	try {
            // Check if the user has sufficient balance
            double currentBalance = checkBalance(userID);
            if (currentBalance < amount) {
                JOptionPane.showMessageDialog(null, "Insufficient funds!");
                return false; // Withdrawal failed due to insufficient funds
            }

            // Update the account balance after withdrawal
            double newBalance = currentBalance - amount;
            PreparedStatement withdrawStatement = connection.prepareStatement("UPDATE usertable SET accountbalance = ? WHERE id = ?");
            withdrawStatement.setDouble(1, newBalance);
            withdrawStatement.setString(2, userID);
            withdrawStatement.executeUpdate();
            return true; // Withdrawal successful
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error withdrawing amount: " + e.getMessage());
            return false; // Withdrawal failed due to database error
        }
    }
        //return false; // Placeholder return value

    public void deposit(String userID, double amount) {
        // Implement your logic to deposit money using userID and amount
    	 try {
             // Get the current balance
             double currentBalance = checkBalance(userID);

             // Update the account balance after deposit
             double newBalance = currentBalance + amount;
             PreparedStatement depositStatement = connection.prepareStatement("UPDATE usertable SET accountbalance = ? WHERE id = ?");
             depositStatement.setDouble(1, newBalance);
             depositStatement.setString(2, userID);
             depositStatement.executeUpdate();
         } catch (SQLException e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(null, "Error depositing amount: " + e.getMessage());
         }
     }
 
    }
//Class representing a user
class User {
    private String userID;
    private int userPIN;
    private double accountBalance;

    public User(String userID, int userPIN, double accountBalance) {
        this.userID = userID;
        this.userPIN = userPIN;
        this.accountBalance = accountBalance;
    }

    // Getters and setters
    public String getUserID() {
        return userID;
    }

    public int getUserPIN() {
        return userPIN;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
}	