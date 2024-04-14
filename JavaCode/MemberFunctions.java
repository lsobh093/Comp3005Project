import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MemberFunctions {

    private static Scanner scanner = new Scanner(System.in);

    public static Connection getConnection() {
        String url = "jdbc:postgresql://localhost:5432/ProjectComp";
        String user = "postgres";
        String password = "admin";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (Exception e) {
            System.out.println("Error");
        }

        return null;
    }

    public static void nextScreen(String userType, int id) {

        if (userType.equals("Member")) {
            int choice = 0;
            boolean running = true;

            while (running) {
                System.out.println("Please select from the following options: ");
                System.out.println("1. View Gym Memberships to Purchase.");
                System.out.println("2. Manage Profile (update personal information, fitness goals, and health metrics).");
                System.out.println("3. Dashboard Display.");
                System.out.println("4. Schedule a group fitness class.");
                System.out.println("5. Schedule a private session with a trainer.");
                System.out.println("6. Exit from the system.");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        purchaseMembership(id);
                        running = false;
                        break;
                    case 2:
                        manageProfile(id);
                        running = false;
                        break;
                    case 3:
                        MemberDashboard.dashBoard(id);
                        running = false;
                        break;
                    case 4:
                        MemberRegistration.joinClass(id);
                    case 5:
                        MemberPrivateRegistration.joinPrivateSession(id);
                    case 6:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 6.");
                }

            }
            scanner.close();
        }
    }

    public static void memberRegister() {

        System.out.println("Member Registration");

        Connection newConnect1 = getConnection();
        int nextUserSystemId = getNextMemberID();

        System.out.println("Enter your first name: ");
        String firstName = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your last name: ");
        String lastName = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your date of birth: (YYYY/MM/DD): ");
        String dateOfBirth = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your gender: ");
        String gender = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your address: ");
        String address = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your email: ");
        String email = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your emergency contact name: ");
        String contactName = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter your emergency contact phone number: ");
        String contactPhoneNumber = scanner.nextLine();
        scanner.nextLine();

        String insertMemberQuery = "INSERT INTO MemberRegistration (memberID, usersTypeID, first_name, last_name, date_of_birth," +
                "gender, address, email, phone_number, emergency_contact_name, emergency_contact_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = newConnect1.prepareStatement(insertMemberQuery)) {

            // Parse date of birth string into java.sql.Date object
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dob = dateFormat.parse(dateOfBirth);
            java.sql.Date sqlDob = new java.sql.Date(dob.getTime());

            // Set parameters for the insert statement
            statement.setInt(1, nextUserSystemId);
            statement.setInt(2, 3);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setDate(5, sqlDob);
            statement.setString(6, gender);
            statement.setString(7, address);
            statement.setString(8, email);
            statement.setString(9, phoneNumber);
            statement.setString(10, contactName);
            statement.setString(11, contactPhoneNumber);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Member registered successfully! Please create a username and password!");
            } else {
                System.out.println("Failed to register member.");
            }
        } catch (SQLException | ParseException e) {
            System.out.println("Error: Connection issue" + e.getMessage());
            e.printStackTrace();
        }

        System.out.print("Enter a username: ");
        String username = scanner.nextLine();
        scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        scanner.nextLine();

        // Check if the username already exists
        if (isUsernameExists(username)) {
            System.out.println("Username already exists. Please choose another username.");
            username = "";
            password = "";
            memberRegister();
        }

        Connection newConnect = getConnection();
        // Insert member details into the database
        String insertUserQuery = "INSERT INTO MemberUserAccounts (memberID, username, pswd) VALUES (?, ?, ?)";
        try (PreparedStatement userStatement = newConnect.prepareStatement(insertUserQuery)) {
            // Set parameters for the userAccounts table
            userStatement.setInt(1, nextUserSystemId);
            userStatement.setString(2, username);
            userStatement.setString(3, password);
            // Execute the insert query for userAccounts
            int affectedRows = userStatement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Creating user failed, no rows affected.");
                System.exit(0);
            } else {
                System.out.println("User successfully registered. Proceed to login");
                UserInteraction.loginAs("Member");
            }
        } catch (Exception e) {
            System.out.println("Error: Connection issue" + e.getMessage());
            e.printStackTrace();
        }

    }

    public static boolean isUsernameExists(String username) {
        Connection newConnect = getConnection();

        String query = "SELECT 1 FROM MemberUserAccounts WHERE username = ? LIMIT 1";
        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            } catch (Exception e) {
                System.out.println("Error: There is an issue" + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error: There is an issue" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static int getNextMemberID() {
        Connection newConnect = getConnection();
        String query = "SELECT MAX(memberID) FROM MemberUserAccounts";
        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1; // Get the maximum system ID
            } else {
                return 3000;
            }
        } catch (Exception e) {
            System.out.println("Error: Connection issue" + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public static void recordLogin(int memberID) {
        // Prepare the insert statement
        Connection newConnect = getConnection();
        String insertQuery = "INSERT INTO MemberLoginHistory (memberID, loginDate) VALUES (?, ?)";

        try (PreparedStatement statement = newConnect.prepareStatement(insertQuery)) {
            // Set parameters for the insert statement
            statement.setInt(1, memberID);
            statement.setObject(2, LocalDateTime.now()); // Use the current date and time
            // Execute the insert statement
            int rowsInserted = statement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error: Connection issue" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void purchaseMembership(int id) {

        Connection newConnect = getConnection();

        String query = "SELECT * FROM Subscriptions";
        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            // Print out the available subscriptions
            System.out.println("Available Subscriptions:");
            while (resultSet.next()) {
                int subscriptionId = resultSet.getInt("subscription_id");
                String subscriptionType = resultSet.getString("subscription_type");
                double price = resultSet.getDouble("price");

                System.out.println("Subscription ID: " + subscriptionId);
                System.out.println("Subscription Type: " + subscriptionType);
                System.out.println("Price: $" + price);
                System.out.println("-----------------------------------");
            }

        } catch (Exception e) {
            System.out.println("Error: Connection issue" + e.getMessage());
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        int subscriptionChoice;
        do {
            System.out.print("Enter the Subscription ID you want to purchase (1 or 2): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter 1 or 2.");
                scanner.next(); // Consume the invalid input
            }
            subscriptionChoice = scanner.nextInt();
        } while (subscriptionChoice != 1 && subscriptionChoice != 2);

        purchaseSubscription(id, subscriptionChoice);
    }

    public static void purchaseSubscription(int id, int subscriptionType) {
        Connection newConnect = getConnection();

        String query = "SELECT COUNT(*) FROM SubscribesTo WHERE memberID = ? AND active_subscription = TRUE AND end_date > CURRENT_DATE";
        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    if (count > 0) {
                        System.out.println("You already have an active membership");
                        nextScreen("Member", id);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double price = 0;
        Connection newConnect2 = getConnection();
        String query2 = "SELECT price FROM Subscriptions WHERE subscription_id = ?";
        try (PreparedStatement statement = newConnect2.prepareStatement(query2)) {
            statement.setInt(1, subscriptionType);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    price = resultSet.getDouble("price");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int invoiceID = generateInvoice(price);
        LocalDate paymentDate = LocalDate.now();
        LocalDate endDate = paymentDate.plusDays(30);

        String insertQuery = "INSERT INTO SubscribesTo (memberID, subscription_id, invoice_id, paid, payment_date, end_date, active_subscription) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = newConnect.prepareStatement(insertQuery)) {
            statement.setInt(1, id);
            statement.setInt(2, subscriptionType);
            statement.setInt(3, invoiceID);
            statement.setBoolean(4, true);
            statement.setDate(5, java.sql.Date.valueOf(paymentDate));
            statement.setDate(6, java.sql.Date.valueOf(endDate));
            statement.setBoolean(7, false); // set to true by administrator once payment is processed.

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Subscription purchased successfully!");
                nextScreen("Member", id);
            } else {
                System.out.println("Failed to purchase subscription.");
                nextScreen("Member", id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        generateProcessedPayment(invoiceID);
    }

    public static int generateInvoice(double price) {
        Connection newConnect = null;
        try {
            newConnect = getConnection();
            String insertQuery = "INSERT INTO InvoicesMemberShip (amount, date_created) VALUES (?, ?)";
            try (PreparedStatement statement = newConnect.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                statement.setDouble(1, price);
                statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the generated invoice ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return -1 if invoice generation failed
        return -1;
    }

    public static void generateProcessedPayment(int invoiceID) {
        Connection newConnect = getConnection();
        try {

            // Insert a processed payment record into the ProcessMemberShipPayment table with processedBy as null
            String insertQuery = "INSERT INTO ProcessMemberShipPayment (invoice_id, processedBy, date_processed) VALUES (?, ?, ?)";
            try (PreparedStatement statement = newConnect.prepareStatement(insertQuery)) {
                statement.setInt(1, invoiceID);
                statement.setNull(2, java.sql.Types.INTEGER); // set to null
                statement.setNull(3, java.sql.Types.DATE); // set to null

                statement.executeUpdate();
                System.out.println("Processed payment record generated successfully.");
//                AdminFunction.updateSubscribesTo(invoiceID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void manageProfile(int id) {

        int choice = 0;
        boolean running = true;

        while (running) {
            System.out.println("Welcome to Profile Management: ");
            System.out.println("1. Update Personal Information.");
            System.out.println("2. Update or view personal goals.");
            System.out.println("3. Update or view health metrics.");
            System.out.println("4. Go back to previous page.");
            System.out.println("5. Exit from program");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    UpdatePersonalInformation(id);
                    running = false;
                    break;
                case 2:
                    UpdateGoals(id);
                    running = false;
                    break;
                case 3:
                    MemberHealthFunctions.UpdateHealthMetrics(id);
                    running = false;
                    break;
                case 4:
                    nextScreen("Member", id);
                    running = false;
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }

        }
        scanner.close();
    }

    public static void UpdatePersonalInformation(int id) {
        Connection connection = getConnection();
        try {
            // Query the database to retrieve current member information
            String selectQuery = "SELECT * FROM MemberRegistration WHERE memberID = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Display current member information
                        System.out.println("Current Member Information:");
                        System.out.println("Address: " + resultSet.getString("address"));
                        System.out.println("Phone Number: " + resultSet.getString("phone_number"));
                        System.out.println("Email: " + resultSet.getString("email"));
                        System.out.println("Emergency Contact Name: " + resultSet.getString("emergency_contact_name"));
                        System.out.println("Emergency Contact Phone: " + resultSet.getString("emergency_contact_number"));

                        // Prompt the user to choose what information to update
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("\nChoose what information to update:");
                        System.out.println("1. Address");
                        System.out.println("2. Phone Number");
                        System.out.println("3. Email");
                        System.out.println("4. Emergency Contact Name");
                        System.out.println("5. Emergency Contact Phone");
                        System.out.println("6. Password Change");
                        System.out.println("7. Go back to previous page");
                        System.out.print("Enter your choice: ");
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        // Update the selected information in the database
                        updateProfileField(connection, id, choice);

                    } else {
                        System.out.println("Member ID not found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateProfileField(Connection connection, int id, int choice) {

        String columnName = "";
        switch (choice) {
            case 1:
                columnName = "address";
                break;
            case 2:
                columnName = "phone_number";
                break;
            case 3:
                columnName = "email";
                break;
            case 4:
                columnName = "emergency_contact_name";
                break;
            case 5:
                columnName = "emergency_contact_number";
                break;
            case 6:
                updatePassword(id);
                break;
            case 7:
                nextScreen("Member", id);
                break;
            default:
                System.out.println("Invalid choice. Please Try Again");
                UpdatePersonalInformation(id);
        }
        System.out.print("Enter the new information: ");
        String newInfo = scanner.nextLine();
        scanner.nextLine();

        // Update the selected field in the database
        String updateQuery = "UPDATE MemberRegistration SET " + columnName + " = ? WHERE memberID = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newInfo);
            statement.setInt(2, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Update successful.");
                manageProfile(id);
            } else {
                System.out.println("Update failed.");
                manageProfile(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePassword(int id) {
        Connection connection = getConnection();

        System.out.println("Please enter a new password:");
        String newPass = scanner.nextLine();
        scanner.nextLine();
        String updateQuery = "UPDATE MemberUserAccounts SET pswd = ? WHERE memberID = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newPass);
            statement.setInt(2, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password updated successfully.");
                manageProfile(id);
            } else {
                System.out.println("No records updated. Member ID not found.");
                manageProfile(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void UpdateGoals(int id) {
        Connection newConnect = getConnection();
        try {
            // Query the database to retrieve current fitness goal for the member
            String selectQuery = "SELECT fg.goalName, ug.updated_Date FROM FitnessGoal fg JOIN UserGoal ug ON fg.goalID = ug.goalID WHERE ug.memberID = ? ORDER BY ug.updated_Date DESC LIMIT 1";
            try (PreparedStatement statement = newConnect.prepareStatement(selectQuery)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Display current fitness goal information
                        System.out.println("Current Fitness Goal Information:");
                        System.out.println("Goal Name: " + resultSet.getString("goalName"));
                        System.out.println("Updated Date: " + resultSet.getDate("updated_Date"));

                        // Prompt the user to choose if they want to update the goal
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("\nDo you want to update your fitness goal? (yes/no): ");
                        String updateChoice = scanner.nextLine();

                        if (updateChoice.equalsIgnoreCase("yes")) {
                            // Prompt the user to enter the new fitness goal
                            System.out.print("Enter your new fitness goal: ");
                            System.out.println("Select your fitness goal:");
                            System.out.println("1. Weight Loss");
                            System.out.println("2. Muscle Mass");
                            System.out.println("3. Maintain");
                            System.out.print("Enter your choice: ");
                            int choice = scanner.nextInt();

                            String goalName2 = getGoalName(choice);
                            // Prompt the user to confirm their choice
                            System.out.println("You have selected: " + goalName2);

                            // Calculate the end date (updated date + 7 days)
                            LocalDate updatedDate = LocalDate.now();
                            LocalDate endDate = updatedDate.plusDays(7);

                            // Update the fitness goal in the database
                            updateFitnessGoal(id, choice, updatedDate, endDate);
                        } else {
                            System.out.println("No update performed. Going back to User Profile. ");
                            manageProfile(id);
                        }
                    } else {
                        System.out.println("No fitness goal found for member ID: " + id);
                        // Display the three fitness goals for the member to choose from
                        System.out.println("Select your fitness goal:");
                        System.out.println("1. Weight Loss");
                        System.out.println("2. Muscle Mass");
                        System.out.println("3. Maintain");
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("Enter your choice: ");
                        int choice = scanner.nextInt();

                        // Get the goal name based on the user's choice
                        String goalName = getGoalName(choice);
                        // Prompt the user to confirm their choice
                        System.out.println("You have selected: " + goalName);
                        Connection newConnect2 = getConnection();
                        LocalDate updateDate2 = LocalDate.now();
                        LocalDate endDate2 = updateDate2.plusDays(7);

                        // Insert the new goal into the FitnessGoal table
                        String insertQuery = "INSERT INTO UserGoal (memberID, goalID, updated_Date, end_date) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement statement2 = newConnect2.prepareStatement(insertQuery)) {
                            statement2.setInt(1, id); // Set memberID
                            statement2.setInt(2, choice); // Set goalID
                            statement2.setDate(3, java.sql.Date.valueOf(updateDate2)); // Set updated_Date
                            statement2.setDate(4, java.sql.Date.valueOf(endDate2)); // Set end_date

                            int rowsInserted = statement2.executeUpdate();
                            if (rowsInserted > 0) {
                                System.out.println("Goal set successful.");
                                manageProfile(id);
                            } else {
                                System.out.println("Failed to set goal.");
                                manageProfile(id);
                            }
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

            public static String getGoalName ( int choice){
                switch (choice) {
                    case 1:
                        return "Weight Loss";
                    case 2:
                        return "Muscle Mass";
                    case 3:
                        return "Maintain";
                    default:
                        return "Invalid Choice";
                }
            }

            public static void updateFitnessGoal ( int id, int newGoal, LocalDate newDate, LocalDate endDate){

                Connection newConnect = getConnection();

                // Update the UserGoal table with the new fitness goal and end date
                String updateQuery = "INSERT INTO UserGoal (memberID, goalID, updated_Date, end_date) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = newConnect.prepareStatement(updateQuery)) {
                    statement.setInt(1, id);
                    statement.setInt(2, newGoal);
                    statement.setDate(3, java.sql.Date.valueOf(newDate)); // Updated date set to current date
                    statement.setDate(4, java.sql.Date.valueOf(endDate));

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Fitness goal updated successfully.");
                        manageProfile(id);
                    } else {
                        System.out.println("Update failed.");
                        manageProfile(id);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

}