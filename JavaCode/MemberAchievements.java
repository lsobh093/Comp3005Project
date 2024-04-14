import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class MemberAchievements {

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

    public static void isFitnessGoalReached(int id) {

        if(MemberExercises.checkRoutine(id) && MemberNutrition.checkNutrition(id)) {

            double totalCaloriesBurned = getTotalCaloriesBurned(id);

            int totalCaloriesConsumed = getTotalCaloriesConsumed(id);

            int netCaloriesGoal = getNetCaloriesGoal(id);

            double calorieDifference = totalCaloriesConsumed - totalCaloriesBurned;
            int goalType = getGoalType(id);
            // Determine if the member is on track to meet their goal based on goal type
            boolean isOnTrack = false;

            if (goalType == 1) {
                // Weight loss goal: Goal is to consume fewer calories than burned
                isOnTrack = calorieDifference <= netCaloriesGoal;
            } else if (goalType == 2) {
                // Muscle gain goal: Goal is to consume more calories than burned
                isOnTrack = calorieDifference >= netCaloriesGoal;
            } else {
                // Maintenance goal: Goal is to maintain a balance between consumed and burned calories
                isOnTrack = Math.abs(calorieDifference) <= netCaloriesGoal;
            }

            String goalStatus = isOnTrack ? "Reached! Great Work" : "Not Reached, Keep Going!";

            String query = "INSERT INTO Achievements (memberID, caloriesGoal, dateUpdated) " +
                    "VALUES (?, ?, ?)";
            Date dateUpdated = Date.valueOf(LocalDate.now());

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setString(2, goalStatus);
                statement.setDate(3, dateUpdated);

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static double getTotalCaloriesBurned(int id){
        Connection connection = getConnection();

        String query =  "SELECT SUM(uw.caloriesBurned) AS totalCaloriesBurned "+
        "FROM UserWorkouts uw "+
        "JOIN (  SELECT memberID, MAX(updated_Date) AS latest_updated_Date "+
                "FROM UserGoal "+
                "WHERE memberID = ? "+
                "GROUP BY memberID "+
        ") ug ON uw.memberID = ug.memberID "+
        "WHERE uw.startTime >= ug.latest_updated_Date "+
        "AND uw.startTime < (SELECT end_date FROM UserGoal WHERE memberID = ? AND updated_Date = ug.latest_updated_Date);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters
            statement.setInt(1, id);
            statement.setInt(2, id);

            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("totalCaloriesBurned");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no data found
    }

    public static int getTotalCaloriesConsumed(int id){

        Connection connection = getConnection();

        String query =  "SELECT SUM(um.calories) AS totalCaloriesConsumed "+
        "FROM UserMeals um "+ "JOIN ("+
                "SELECT memberID, MAX(updated_Date) AS latest_updated_Date "+
                "FROM UserGoal "+
                "WHERE memberID = ? "+
                "GROUP BY memberID "+
            ") ug ON um.memberID = ug.memberID "+
        "WHERE um.mealDate >= ug.latest_updated_Date "+
        "AND um.mealDate < (SELECT end_date FROM UserGoal WHERE memberID = ? AND updated_Date = ug.latest_updated_Date);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters
            statement.setInt(1, id);
            statement.setInt(2, id);
            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    return resultSet.getInt("totalCaloriesConsumed");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no data found
    }

    public static int getNetCaloriesGoal(int id){

        Connection connection = getConnection();
        String query = "SELECT fg.netCaloriesGoal AS calories "+
        "FROM UserGoal ug "+
        "JOIN FitnessGoal fg ON ug.goalID = fg.goalID "+
        "WHERE ug.memberID = ? "+
        "ORDER BY ug.updated_Date DESC "+
        "LIMIT 1;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters
            statement.setInt(1, id);

            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("calories");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no data found
    }

    private static int getGoalType(int id) {
        Connection connection = getConnection();
        String query = "SELECT goalID FROM UserGoal WHERE memberID = ? ORDER BY updated_Date DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("goalID");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
