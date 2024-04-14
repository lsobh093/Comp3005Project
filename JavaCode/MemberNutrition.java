import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class MemberNutrition {

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

    public static void nutritionFunction(int id) {

        boolean running = true;

        while(running) {
            System.out.println("Welcome to your nutrition tracker");
            System.out.println("1. Enter Manual Data into your tracker");
            System.out.println("2. Display your nutrition entries for the past 7 days");
            System.out.println("3. Go back to the previous page");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    manualNutritionData(id);
                    running = false;
                    break;
                case 2:
                    viewNutritionData(id);
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

    public static void manualNutritionData(int id){
        Connection newConnect = getConnection();

        System.out.println("Enter meal information:");
        System.out.print("Meal Date (YYYY-MM-DD): ");
        String mealDate = scanner.nextLine();
        Date mealD = Date.valueOf(mealDate);
        System.out.print("Meal Description: ");
        String mealDescription = scanner.nextLine();
        System.out.print("Calories: ");
        int calories = scanner.nextInt();
        scanner.nextLine();

        // Prepare SQL statement for inserting meal data
        String query = "INSERT INTO UserMeals (memberID, mealDate, mealDescription, calories) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setDate(2, mealD);
            statement.setString(3, mealDescription);
            statement.setInt(4, calories);

            // Execute the SQL statement to insert the meal data
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                boolean running = true;
                while(running) {
                    System.out.println("Meal data inserted successfully.");
                    System.out.println("1. To go back to the previous page.");
                    System.out.println("2. To enter more dataset.");
                    System.out.println("3. To view your nutrition data");
                    System.out.println("4. To exit");
                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            nutritionFunction(id);
                            running = false;
                            break;
                        case 2:
                            manualNutritionData(id);
                            running = false;
                            break;
                        case 3:
                            viewNutritionData(id);
                        case 4:
                            System.out.println("System exiting..");
                            System.exit(0);
                        default:
                            System.out.println("Invalid choice. Please enter a number from 1 to 4.");
                    }
                }

            } else {
                System.out.println("Failed to insert meal data. Going back");
                nutritionFunction(id);

            }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    public static void viewNutritionData(int id){
        if(checkNutrition(id)){
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Going back to menu");
            nutritionFunction(id);
        }else{
            System.out.println("This user has not entered any routines yet. Going back");
            nutritionFunction(id);
        }
    }

    public static boolean checkNutrition(int id) {
        Connection newConnect = getConnection();
        String query = "SELECT COUNT(*) AS nutritionCount FROM UserMeals WHERE memberID = ?";

        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int nutritionCount = resultSet.getInt("nutritionCount");
                return nutritionCount > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

