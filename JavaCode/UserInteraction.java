import java.sql.*;
import java.util.Scanner;

public class UserInteraction {
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

    public static void welcomePage() {
        int choice = 0;
        boolean running = true;

        while (running) {
            System.out.println("Main Menu:");
            System.out.println("1. Login as Member");
            System.out.println("2. Login as Trainer");
            System.out.println("3. Login as Staff");
            System.out.println("4. Register as a Member");
            System.out.println("5. Exit");
            System.out.print("Please enter your choice: ");

            // Read user choice
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    loginAs("Member");
                    running = false;
                    break;
                case 2:
                    loginAs("Trainer");
                    running = false;
                    break;
                case 3:
                    loginAs("Staff");
                    running = false;
                    break;
                case 4:
                    MemberFunctions.memberRegister();
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 4.");
            }

        }
        scanner.close();
    }

    public static void loginAs(String userType) {

        //Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username case sensitive:");
        String username = scanner.nextLine();
        System.out.println("Enter password case sensitive:");
        String password = scanner.nextLine();

        // Prepare SQL statement based on user role
        String query = "";
        switch (userType) {
            case "Member":
                query = "SELECT memberID FROM MemberUserAccounts WHERE username = ? AND pswd = ?";
                break;
            case "Trainer":
                query = "SELECT trainerID FROM TrainerUserAccounts WHERE username = ? AND pswd = ?";
                break;
            case "Staff":
                query = "SELECT adminID FROM AdminUserAccounts WHERE username = ? AND pswd = ?";
                break;
            default:
                System.out.println("Invalid role");
        }
        Connection newConnect = getConnection();
        try {
            // Execute the query
            PreparedStatement preparedStatement = newConnect.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                System.out.println("Welcome " + userType + " with ID: " + id);
                if (userType.equals("Member")) {
                    MemberFunctions.recordLogin(id);
                    MemberFunctions.nextScreen(userType, id);
                } else if (userType.equals("Trainer")) {
                    TrainerFunctions.nextScreen(userType, id);
                } else if (userType.equals("Staff")) {
                    AdminFunctions.nextScreen(userType, id);
                } else {
                    System.out.println("Invalid username or password, please try again.");
                    welcomePage();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
