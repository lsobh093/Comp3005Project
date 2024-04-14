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
                    viewRoutines(id);
                    running = false;
                    break;
                case 2:
                    viewNutritionData(id);
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

    public static void viewRoutines(int id) {

        Connection newConnect = getConnection();

        String query = "SELECT uw.memberID, uw.startTime, uw.endTime, uw.duration, uw.caloriesBurned, aw.workoutName " +
                "FROM UserWorkouts uw " +
                "JOIN AllWorkouts aw ON uw.workoutID = aw.workoutID " +
                "WHERE uw.memberID = ? AND uw.startTime >= ? ORDER BY uw.startTime ASC";

        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(7)));
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Workout routines for member with ID " + id + ":");
            System.out.println("------------------------------------------------------");
            System.out.printf("%-15s %-20s %-20s %-10s %-15s %-15s\n",
                    "Member ID", "Start Time", "End Time", "Duration", "Calories Burned", "Workout Name");
            System.out.println("------------------------------------------------------");

            while (resultSet.next()) {
                int memberId = resultSet.getInt("memberID");
                String startTime = resultSet.getTimestamp("startTime").toString();
                String endTime = resultSet.getTimestamp("endTime").toString();
                int duration = resultSet.getInt("duration");
                double caloriesBurned = resultSet.getDouble("caloriesBurned");
                String workoutName = resultSet.getString("workoutName");

                System.out.printf("%-15s %-20s %-20s %-10s %-15s %-15s\n",
                        memberId, startTime, endTime, duration, caloriesBurned, workoutName);
            }
            dashBoard(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void viewNutritionData(int id){

            Connection newConnect = getConnection();
            String query = "SELECT mealID, mealDate, mealDescription, calories FROM UserMeals WHERE memberID = ? AND mealDate >= ? ORDER BY mealDate ASC";

            try (PreparedStatement statement = newConnect.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(7)));
                ResultSet resultSet = statement.executeQuery();

                System.out.println("Meals for Member ID: " + id);
                System.out.println("-----------------------------------------");
                System.out.printf("%-8s %-15s %-30s %-10s\n", "Meal ID", "Date", "Description", "Calories");
                System.out.println("-----------------------------------------");

                while (resultSet.next()) {
                    int mealID = resultSet.getInt("mealID");
                    String mealDate = resultSet.getDate("mealDate").toString();
                    String mealDescription = resultSet.getString("mealDescription");
                    int calories = resultSet.getInt("calories");

                    System.out.printf("%-8d %-15s %-30s %-10d\n", mealID, mealDate, mealDescription, calories);
                }
                dashBoard(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    public static void viewLatestHealthStatistics(int id) {

        Connection connection = getConnection();

        String selectQuery = "SELECT * FROM MemberHealthMetrics WHERE memberID = ? ORDER BY date_recorded DESC LIMIT 1;";
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Display existing health metrics
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

        //MemberAchievements.isFitnessGoalReached(memberId);

        Connection connection = getConnection();
        // Prepare SQL query to retrieve latest achievement
        String query = "SELECT * FROM Achievements WHERE memberID = ? ORDER BY dateUpdated DESC LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Check if any result is returned
                if (resultSet.next()) {
                    // Retrieve data from the result set
                    int achievementId = resultSet.getInt("achieve_ID");
                    String caloriesGoal = resultSet.getString("caloriesGoal");
                    Date dateUpdated = resultSet.getDate("dateUpdated");

                    // Display the latest achievement
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

