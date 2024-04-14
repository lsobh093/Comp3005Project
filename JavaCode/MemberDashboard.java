import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class MemberDashboard {
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

    public static void dashBoard(int id) {

        boolean running = true;

        while (running) {

            System.out.println("Welcome to your dashboard:");
            System.out.println("Select one of the following options:");
            System.out.println("1. View your workout routines (past 7 days)");
            System.out.println("2. View your nutrition journal (past 7 days)");
            System.out.println("3. View your health statics");
            System.out.println("4. View your achievements for the most recent goal");
            System.out.println("5. Go back to the main menu");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {

                case 1:
                    MemberExercises.exerciseRoutine(id);
                    running = false;
                    break;
                case 2:
                    MemberNutrition.nutritionFunction(id);
                    running = false;
                    break;
                case 3:
                    viewLatestHealthStatistics(id);
                    running = false;
                    break;
                case 4:
                    viewLatestAchievement(id);
                    running = false;
                    break;
                case 5:
                    MemberFunctions.nextScreen("Member", id);
                    running = false;
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 4.");
            }
        }
    }

    public static void viewLatestHealthStatistics(int id) {

        Connection connection = getConnection();

        String selectQuery = "SELECT * FROM MemberHealthMetrics WHERE memberID = ? ORDER BY date_recorded DESC LIMIT 1;";
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    System.out.println("Existing and Latest Health Metrics:");

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
                    dashBoard(id);
                } else {
                    System.out.println("No health statistics found for member ID: " + id);
                    dashBoard(id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewLatestAchievement(int memberId) {

        MemberAchievements.isFitnessGoalReached(memberId);

        Connection connection = getConnection();
        String query = "SELECT * FROM Achievements WHERE memberID = ? ORDER BY dateUpdated DESC LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int achievementId = resultSet.getInt("achieve_ID");
                    String caloriesGoal = resultSet.getString("caloriesGoal");
                    Date dateUpdated = resultSet.getDate("dateUpdated");

                    System.out.println("Latest Achievement:");
                    System.out.println("Achievement ID: " + achievementId);
                    System.out.println("Member ID: " + memberId);
                    System.out.println("Calories Goal: " + caloriesGoal);
                    System.out.println("Date Updated: " + dateUpdated);
                    dashBoard(memberId);
                } else {
                    System.out.println("No achievement found for member ID: " + memberId);
                    dashBoard(memberId);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

