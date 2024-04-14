import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MemberExercises {

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

    public static void exerciseRoutine(int id) {

        boolean running = true;
        while(running) {
            System.out.println("Welcome to your exercise tracker");
            System.out.println("1. Enter Manual Data into your tracker");
            System.out.println("2. Display your routine exercises for the past 7 days");
            System.out.println("3. Go back to the previous page");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    manualGenerateData(id);
                    running = false;
                    break;
                case 2:
                    viewRoutines(id);
                    running = false;
                    break;
                case 3:
                    MemberFunctions.nextScreen("Member", id);
                    running = false;
                    break;
                case 4:
                    System.out.println("System exiting..");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 4.");
            }
        }

    }

    public static void manualGenerateData(int id) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Connection connection = getConnection();

        System.out.println("Please manually enter your workout data log for the day.");
        System.out.println("Enter the exercise type");
        System.out.println("Do you want to load the list of exercises and their code? (yes/no)");
        String response = scanner.nextLine();
        if (response.equals("yes")) {
            System.out.println("The list of exercises:");
            loadExercises();
        }
        System.out.println("Please enter an integer selection (1-10)");
        int wrkID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the day and start time in the format YYYY-MM-DD H:mm");
        String inputStart = scanner.nextLine();
        try {
            LocalDateTime dateTime = LocalDateTime.parse(inputStart, inputFormatter);

        } catch (Exception e) {
            System.out.println("Invalid date and time format. Please enter in the format yyyy-MM-dd H:mm.");
            manualGenerateData(id);
        }
        System.out.println("Enter the day and end time in the format YYYY-MM-DD H:mm");
        String inputEnd = scanner.nextLine();
        try {
            LocalDateTime dateTime1 = LocalDateTime.parse(inputEnd, inputFormatter);

        } catch (Exception e) {
            System.out.println("Invalid date and time format. Please enter in the format yyyy-MM-dd H:mm.");
            manualGenerateData(id);
        }
        System.out.println("Enter the weight (kg) if applicable zero otherwise");
        int inputWeight = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the number of sets if applicable zero otherwise");
        int inputSets = scanner.nextInt();
        scanner.nextLine();

        LocalDateTime dateTime = LocalDateTime.parse(inputStart, inputFormatter);
        LocalDateTime dateTime1 = LocalDateTime.parse(inputEnd, inputFormatter);

        String insertQuery = "INSERT INTO UserWorkouts (memberID, workoutID, startTime, endTime, duration, caloriesBurned, weight, numberOfSets) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setInt(1, id);
            statement.setInt(2, wrkID);
            statement.setTimestamp(3, Timestamp.valueOf(dateTime));
            statement.setTimestamp(4, Timestamp.valueOf(dateTime1));
            statement.setInt(5, java.sql.Types.INTEGER);
            statement.setDouble(6, java.sql.Types.DOUBLE);
            statement.setInt(7, inputWeight);
            statement.setInt(8, inputSets);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                boolean running = true;
                while(running) {
                    System.out.println("User workouts inserted successfully.");
                    System.out.println("1. To go back to the previous page.");
                    System.out.println("2. To enter more dataset.");
                    System.out.println("3. To view your workout routines");
                    System.out.println("4. To exit");
                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            exerciseRoutine(id);
                            running = false;
                            break;
                        case 2:
                            manualGenerateData(id);
                            running = false;
                            break;
                        case 3:
                            viewRoutines(id);
                        case 4:
                            System.out.println("System exiting..");
                            System.exit(0);
                        default:
                            System.out.println("Invalid choice. Please enter a number from 1 to 4.");
                    }
                }

            } else {
                System.out.println("Failed to insert user workouts.");
                exerciseRoutine(id);
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadExercises() {
        Connection newConnect = getConnection();

        String query = "SELECT * FROM AllWorkouts";
        try (PreparedStatement statement = newConnect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()) {
            System.out.printf("%-10s %-30s %-20s%n", "WorkoutID", "WorkoutName", "CaloriesBurnedPerMinute");
            System.out.println("---------------------------------------------------------");
            while (resultSet.next()) {
                int workoutID = resultSet.getInt("workoutID");
                String workoutName = resultSet.getString("workoutName");
                double caloriesBurnedPerMinute = resultSet.getDouble("caloriesBurnedPerMinute");

                System.out.printf("%-10d %-30s %-20.2f%n", workoutID, workoutName, caloriesBurnedPerMinute);
            }
        }catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void viewRoutines(int id){

        if (checkRoutine(id)) {

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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Going back to menu");
            exerciseRoutine(id);
        }else{
            System.out.println("This user has not entered any routines yet.");
            exerciseRoutine(id);
        }
    }

    public static boolean checkRoutine(int id) {
        Connection newConnect = getConnection();
        String query = "SELECT COUNT(*) AS routineCount FROM UserWorkouts WHERE memberID = ?";

        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int routineCount = resultSet.getInt("routineCount");
                return routineCount > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}




