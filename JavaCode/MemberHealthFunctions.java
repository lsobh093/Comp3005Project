import java.sql.*;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

public class MemberHealthFunctions {

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
    public static void UpdateHealthMetrics(int id) {
        Connection connection = getConnection();

        String selectQuery = "SELECT * FROM MemberHealthMetrics WHERE memberID = ? ORDER BY date_recorded DESC LIMIT 1;";
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Display existing health metrics
                    System.out.println("Existing and Latest Health Metrics:");
                    displayHealthMetrics(resultSet, id);
                } else {
                    System.out.println("No existing health metrics found for member ID: " + id);
                    System.out.print("Do you want to add new health metrics? (yes/no): ");
                    String input = scanner.nextLine();
                    scanner.nextLine();
                    if (input.equalsIgnoreCase("yes")) {
                        insertRandomHealthMetrics(connection, id);
                    }else{
                        System.out.println("Going back to main menu");
                        MemberFunctions.nextScreen("Member", id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayHealthMetrics(ResultSet resultSet, int id) throws SQLException {
        Connection connection = getConnection();

        LocalDate dateRecorded = resultSet.getDate("date_recorded").toLocalDate();
        double weight = resultSet.getDouble("weight");
        double height = resultSet.getDouble("height");
        double bodyFatPercentage = resultSet.getDouble("body_fat_percentage");
        int restingHeartRate = resultSet.getInt("resting_heart_rate");
        int bloodPressure = resultSet.getInt("blood_pressure");

        System.out.println("Date Recorded: " + dateRecorded);
        System.out.println("Weight: " + weight + " kg");
        System.out.println("Height: " + height + " cm");
        System.out.println("Body Fat Percentage: " + bodyFatPercentage);
        System.out.println("Resting Heart Rate: " + restingHeartRate + " bpm");
        System.out.println("Blood Pressure: " + bloodPressure + " mmHg");

        boolean running = true;

        while(running) {
            System.out.println("Would you like to add new data or go back to the main menu?");
            System.out.println("1. To go back to main menu.");
            System.out.println("2. Enter new data");
            System.out.println("3. To exit.");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    MemberFunctions.nextScreen("Member", id);
                    running = false;
                    break;
                case 2:
                    insertRandomHealthMetrics(connection, id);
                case 3:
                    System.out.println("System exiting..");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 2.");
            }
        }
    }

    public static void insertRandomHealthMetrics(Connection connection, int memberId) {
        // Generate random health metrics data
        Random random = new Random();
        LocalDate currentDate = LocalDate.now();
        double weight = 50 + random.nextDouble() * 50; // Random weight between 50 and 100 kg
        double height = 150; // Random height between 150 and 200 cm
        double bodyFatPercentage = random.nextDouble() * 30; // Random body fat percentage between 0 and 30
        int restingHeartRate = 60 + random.nextInt(40); // Random resting heart rate between 60 and 100 bpm
        int bloodPressure = 90 + random.nextInt(60); // Random blood pressure between 90/60 and 150/100 mmHg

        // Insert the random health metrics data into the MemberHealthMetrics table
        String insertQuery = "INSERT INTO MemberHealthMetrics (memberID, date_recorded, weight, height, body_fat_percentage, resting_heart_rate, blood_pressure) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setInt(1, memberId);
            statement.setDate(2, java.sql.Date.valueOf(currentDate));
            statement.setDouble(3, weight);
            statement.setDouble(4, height);
            statement.setDouble(5, bodyFatPercentage);
            statement.setInt(6, restingHeartRate);
            statement.setInt(7, bloodPressure);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                boolean running = true;
                while(running) {
                    System.out.println("New health metrics added successfully. ");
                    System.out.println("Would you like to display your health metrics or go back to the main menu?");
                    System.out.println("1. To go back to main menu.");
                    System.out.println("2. To display your health metrics.");
                    System.out.println("3. To exit");
                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            MemberFunctions.nextScreen("Member", memberId);
                            running = false;
                            break;
                        case 2:
                            UpdateHealthMetrics(memberId);
                            running = false;
                            break;
                        case 3:
                            System.out.println("System exiting..");
                            System.exit(0);
                        default:
                            System.out.println("Invalid choice. Please enter a number from 1 to 2.");
                    }
                }

            } else {
                System.out.println("Failed to insert new health metrics. Going back.");
                MemberFunctions.nextScreen("Member", memberId);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
