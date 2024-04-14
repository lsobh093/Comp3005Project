import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class TrainerFunctions {

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

        int choice = 0;
        boolean running = true;

        while (running) {
            System.out.println("Please select from the following options: ");
            System.out.println("1. Create and view your schedule.");
            System.out.println("2. View your booked private lessons");
            System.out.println("3. View your classes and the room #");
            System.out.println("4. View specific member profiles by first name");
            System.out.println("5. Exit from the system.");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    trainerAvailability(id);
                    running = false;
                    break;
                case 2:
                    viewBookedPrivateSessions(id);
                    running = false;
                    break;
                case 3:
                    viewClasses(id);
                    running = false;
                    break;
                case 4:
                    viewMemberProfile(id);
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

    public static void trainerAvailability(int trainerID) {

        Connection newConnect = getConnection();

        String sql = "SELECT * FROM TrainerCalendar WHERE trainerID = ?";
        try (PreparedStatement pstmt = newConnect.prepareStatement(sql)) {
            pstmt.setInt(1, trainerID);
            ResultSet rs = pstmt.executeQuery();

            // Print the availability data
            System.out.println("Trainer Calendar for Trainer ID " + trainerID + ":");
            System.out.println("---------------------------------------------------------");
            System.out.printf("%-15s %-15s %-12s %-12s %-12s %-15s\n",
                    "TrainerID", "SessionType", "Date", "Start Time", "End Time", "Status");
            System.out.println("---------------------------------------------------------");
            while (rs.next()) {
                int trainerIDResult = rs.getInt("trainerID");
                String sessionType = rs.getString("sessionType");
                Date date = rs.getDate("date");
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                String status = rs.getString("status");

                System.out.printf("%-15d %-15s %-12s %-12s %-12s %-15s\n",
                        trainerIDResult, sessionType, date, startTime, endTime, status);
            }
            System.out.println("---------------------------------------------------------");
            rs.close();

            boolean running = true;
            while (running) {

                System.out.println("Please select from the following options. ");
                System.out.println("1. Add availability to your schedule. ");
                System.out.println("2. Go back to your dashboard menu. ");
                System.out.println("3. Exit from the system. ");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        addAvailability(trainerID);
                        running = false;
                        break;
                    case 2:
                        trainerAvailability(trainerID);
                        running = false;
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 4.");
                }

            }

        } catch (SQLException e) {
            System.out.println("Connection Error");
            e.printStackTrace();
        }
    }

    public static void addAvailability(int trainerID) {

        Connection newConnect = getConnection();
            System.out.println("Enter sessionType (1 for private, 2 for class):");
            int sessionType = scanner.nextInt();

            System.out.println("Enter date (yyyy-mm-dd):");
            String dateString = scanner.next();
            LocalDate date = LocalDate.parse(dateString);

            System.out.println("Enter start time (hh:mm:ss):");
            String startTimeString = scanner.next();
            LocalTime startTime = LocalTime.parse(startTimeString);

            System.out.println("Enter end time (hh:mm:ss):");
            String endTimeString = scanner.next();
            LocalTime endTime = LocalTime.parse(endTimeString);

            String sql = "INSERT INTO TrainerAvailability (trainerID, sessionType, date, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
            try(PreparedStatement pstmt = newConnect.prepareStatement(sql)){
                pstmt.setInt(1, trainerID);
                pstmt.setInt(2, sessionType);
                pstmt.setDate(3, Date.valueOf(date));
                pstmt.setTime(4, Time.valueOf(startTime));
                pstmt.setTime(5, Time.valueOf(endTime));

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("New schedule added successfully!");
                } else {
                    System.out.println("Failed to add new schedule.");
                }

        } catch (SQLException e) {
                if (e.getMessage().contains("Trainer cannot book sessions at the same time")) {
                    System.out.println("Error: Trainer has conflicting sessions at the same time.");
                } else if (e.getMessage().contains("Trainer cannot hold private and class sessions at the same time")) {
                    System.out.println("Error: Trainer cannot hold private and class sessions at the same time.");
                } else {
                    e.printStackTrace();
                }
        }
        trainerAvailability(trainerID);
    }

    public static void viewClasses(int trainerID){
        Connection newConnect = getConnection();

            String query = "SELECT c.availabilityID, c. no_members_enrolled, c.status, r.room_name, s.description, ta.date " +
                    "FROM Classes c " +
                    "JOIN TrainerAvailability ta ON c.availabilityID = ta.availabilityID " +
                    "JOIN Room r ON c.room_id = r.roomID " +
                    "JOIN SessionType s ON s.sessionType_id = ta.sessionType " +
                    "WHERE s.description = 'Class' AND ta.trainerID = ?";
            try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
                pstmt.setInt(1, trainerID);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int availabilityID = rs.getInt("availabilityID");
                    String roomName = rs.getString("room_name");
                    String sessionType = rs.getString("description");
                    String date = rs.getString("date");
                    int no_members_enrolled = rs.getInt("no_members_enrolled");
                    String status = rs.getString("status");

                    System.out.println("Availability ID: " + availabilityID);
                    System.out.println("Room Name: " + roomName);
                    System.out.println("Session Type: " + sessionType);
                    System.out.println("Date: " + date);
                    System.out.println("No members enrolled: " + no_members_enrolled);
                    System.out.println("Status: " + status);
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void viewBookedPrivateSessions(int trainerID){

        Connection newConnect = getConnection();

        String sqlQuery = "SELECT * FROM PrivateSessionsViewerForTrainer WHERE trainerID = ?";

        try (PreparedStatement pstmt = newConnect.prepareStatement(sqlQuery)){
            pstmt.setInt(1, trainerID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int availabilityID = rs.getInt("availabilityID");
                int trainerIDFromDB = rs.getInt("trainerID");
                String trainerFirstName = rs.getString("trainer_fName");
                String trainerLastName = rs.getString("trainer_lName");
                Date date = rs.getDate("date");
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                int memberID = rs.getInt("memberID");
                String memberFirstName = rs.getString("member_first_name");
                String memberLastName = rs.getString("member_last_name");
                Date enrollmentDate = rs.getDate("enrollment_date");

                System.out.println("Availability ID: " + availabilityID);
                System.out.println("Trainer ID: " + trainerIDFromDB);
                System.out.println("Trainer Name: " + trainerFirstName + " " + trainerLastName);
                System.out.println("Date: " + date);
                System.out.println("Start Time: " + startTime);
                System.out.println("End Time: " + endTime);
                System.out.println("Member ID: " + memberID);
                System.out.println("Member Name: " + memberFirstName + " " + memberLastName);
                System.out.println("Enrollment Date: " + enrollmentDate);
                System.out.println("-------------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        nextScreen("Trainer", trainerID);
    }


    public static void viewMemberProfile(int id) {
        Connection newConnect = getConnection();

        System.out.println("Please enter the name of the member to view their profile ");
        String firstName = scanner.nextLine();

        String query = "SELECT * FROM memberdetails WHERE first_name = ?";

        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setString(1, firstName);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No member found with the name: " + firstName);
                System.out.println("Returning to menu. ");
                nextScreen("Trainer", id);
            }

            while (resultSet.next()) {
                // Assuming column indexes or names for retrieving data
                int memberId = resultSet.getInt("memberID");
                String lastName = resultSet.getString("last_name");
                Date dob = resultSet.getDate("date_of_birth");
                String gender = resultSet.getString("gender");
                String phoneNumber = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                double weight = resultSet.getDouble("weight");
                String goalName = resultSet.getString("goalName");
                String emergencyContactName = resultSet.getString("emergency_contact_name");
                String emergencyContactNumber = resultSet.getString("emergency_contact_number");
                Date metricsDate = resultSet.getDate("health_metrics_recorded_date");
                double metricsWeight = resultSet.getDouble("weight");
                double height = resultSet.getDouble("height");
                double bodyFatPercentage = resultSet.getDouble("body_fat_percentage");
                int restingHeartRate = resultSet.getInt("resting_heart_rate");
                int bloodPressure = resultSet.getInt("blood_pressure");

                System.out.println("Member ID: " + memberId);
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName);
                System.out.println("Date of Birth: " + dob);
                System.out.println("Gender: " + gender);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Email: " + email);
                System.out.println("Weight: " + weight);
                System.out.println("Goal: " + goalName);
                System.out.println("Emergency Contact Name: " + emergencyContactName);
                System.out.println("Emergency Contact Number: " + emergencyContactNumber);
                System.out.println("Health Metrics Recorded Date: " + metricsDate);
                System.out.println("Health Metrics Weight: " + metricsWeight);
                System.out.println("Height: " + height);
                System.out.println("Body Fat Percentage: " + bodyFatPercentage);
                System.out.println("Resting Heart Rate: " + restingHeartRate);
                System.out.println("Blood Pressure: " + bloodPressure);
                System.out.println();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
