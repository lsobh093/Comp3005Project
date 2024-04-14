import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class MemberRegistration {

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

    public static void joinClass(int memberId) {
        boolean running = true;

        while (running) {
            System.out.println("Welcome to class registration");
            System.out.println("1. View Class Schedule.");
            System.out.println("2. Register for a class.");
            System.out.println("3. View the classes that you are registered in");
            System.out.println("4. Go back to the previous page.");
            System.out.println("5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewClass(memberId);
                    running = false;
                    break;
                case 2:
                    registerClass(memberId);
                    running = false;
                    break;
                case 3:
                    viewClassesRegistered(memberId);
                    running = false;
                    break;
                case 4:
                    MemberFunctions.nextScreen("Member", memberId);
                    running = false;
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }

        }
    }


    public static void viewClass(int memberId) {
        Connection newConnect = getConnection();

        String query = "SELECT * FROM classcalendarformembers";
        try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("===============================================================================================");
                System.out.printf("%-15s %-20s %-15s %-20s %-15s %-10s %-15s %-15s %-15s\n",
                        "Availability ID", "Room Name", "Session Type", "Trainer", "Status", "Start Time", "End Time", "Date", "Time Slot");
                System.out.println("===============================================================================================");
                while (rs.next()) {
                    int availabilityID = rs.getInt("availabilityID");
                    String roomName = rs.getString("room_name");
                    String sessionType = rs.getString("description");
                    String trainerName = rs.getString("trainer_fName") + " " + rs.getString("trainer_lName");
                    String status = rs.getString("status");
                    String startTime = rs.getString("start_time");
                    String endTime = rs.getString("end_time");
                    String date = rs.getString("date");
                    String timeSlot = startTime + " - " + endTime;
                    System.out.printf("%-15s %-20s %-15s %-20s %-15s %-10s %-15s %-15s %-15s\n",
                            availabilityID, roomName, sessionType, trainerName, status, startTime, endTime, date, timeSlot);
                }
                System.out.println("===============================================================================================");
                System.out.println("Going back to menu");
                joinClass(memberId);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registerClass(int memberID) {
        Connection newConnect = getConnection();
        System.out.println("Select the class availability ID to register.");
        int availabilityID = scanner.nextInt();
        scanner.nextLine();

        LocalDate localDate = LocalDate.now();
        Date sqlDate = Date.valueOf(localDate);

        if (checkClass(memberID, availabilityID) && checkSession(memberID, availabilityID) && classFull(availabilityID)) {
            String query = "INSERT INTO ClassEnrollment (availabilityID, memberID, enrollment_date, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
                // Set parameter values
                pstmt.setInt(1, availabilityID);
                pstmt.setInt(2, memberID);
                pstmt.setDate(3, sqlDate);
                pstmt.setString(4, "Enrolled");

                pstmt.executeUpdate();
                System.out.println("Enrollment successful.");
                updateClass(availabilityID, 1);
                joinClass(memberID);

            } catch (SQLException e) {
//                if (e.getMessage().startsWith("ERROR")) {
//                    System.out.println("BLOOOP");
//                }
                e.printStackTrace();
            }
        }else {
           System.out.println("Error: cannot register for class.");
            joinClass(memberID);
        }

    }

    public static void updateClass(int availabilityID, int value) {
        Connection newConnect = getConnection();

        int numberEnrolled = getNoMembersEnrolled(availabilityID) + value;

        String query = "UPDATE Classes SET no_members_enrolled = ? WHERE availabilityID = ?";
        try (PreparedStatement statement = newConnect.prepareStatement(query)) {

            statement.setInt(1, numberEnrolled);
            statement.setInt(2, availabilityID);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Number of members enrolled updated successfully.");
                classFull(availabilityID);
            } else {
                System.out.println("No rows were updated. Availability ID not found.");
                classFull(availabilityID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getNoMembersEnrolled(int availabilityID) {
        Connection newConnect = getConnection();
        int noMembersEnrolled = 0;

        String query = "SELECT no_members_enrolled FROM Classes WHERE availabilityID = ?";
        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            statement.setInt(1, availabilityID);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                noMembersEnrolled = rs.getInt("no_members_enrolled");
                return noMembersEnrolled;
            } else {
                System.out.println("Availability ID not found.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean checkClass(int memberID, int availabilityID) {
        Connection newConnect = getConnection();

        String query = "SELECT ta.date, ta.start_time, ta.end_time " +
                "FROM TrainerAvailability ta " +
                "WHERE ta.availabilityID = ?";
        try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
            pstmt.setInt(1, availabilityID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date date = rs.getDate("date");
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                return !isMemberRegistered(newConnect, memberID, date, startTime, endTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMemberRegistered(Connection newConnect, int memberID, Date date, Time startTime, Time endTime) {

        String query = "SELECT COUNT(*) FROM ClassEnrollment ce " +
                "JOIN TrainerAvailability ta ON ce.availabilityID = ta.availabilityID " +
                "JOIN Classes c on ta.availabilityID = c.availabilityID " +
                "WHERE ce.memberID = ? " + "AND ta.date = ? " +
                "AND ((? >= ta.start_time AND ?<ta.end_time) OR " +
                "(? > ta.start_time AND ? <= ta.end_time) OR " +
                "(? <= ta.start_time AND ? >= ta.end_time))";
        try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
            pstmt.setInt(1, memberID);
            pstmt.setDate(2, date);
            pstmt.setTime(3, startTime);
            pstmt.setTime(4, startTime);
            pstmt.setTime(5, endTime);
            pstmt.setTime(6, endTime);
            pstmt.setTime(7, startTime);
            pstmt.setTime(8, endTime);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean checkSession(int memberID, int availabilityID) {
        Connection newConnect = getConnection();

        String query = "SELECT ta.date, ta.start_time, ta.end_time " +
                "FROM TrainerAvailability ta " +
                "WHERE ta.availabilityID = ?";
        try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
            pstmt.setInt(1, availabilityID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date date = rs.getDate("date");
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                return isMemberRegisteredPrivate(newConnect, memberID, date, startTime, endTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean isMemberRegisteredPrivate(Connection newConnect, int memberID, Date date, Time startTime, Time endTime) {

        String query = "SELECT COUNT(*) FROM MemberPurchaseTrainerSession mpts " +
                "JOIN TrainerAvailability ta ON mpts.availabilityID = ta.availabilityID " +
                "WHERE mpts.memberID = ? " + "AND ta.date = ? " +
                "AND ((? >= ta.start_time AND ?<ta.end_time) OR " +
                "(? > ta.start_time AND ? <= ta.end_time) OR " +
                "(? <= ta.start_time AND ? >= ta.end_time))";
        try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
            pstmt.setInt(1, memberID);
            pstmt.setDate(2, date);
            pstmt.setTime(3, startTime);
            pstmt.setTime(4, startTime);
            pstmt.setTime(5, endTime);
            pstmt.setTime(6, endTime);
            pstmt.setTime(7, startTime);
            pstmt.setTime(8, endTime);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean classFull(int availabilityID) {
        Connection newConnect = getConnection();

            String query = "SELECT no_members_enrolled, r.capacity " +
                    "FROM Classes c " +
                    "JOIN Room r ON c.room_id = r.roomID " +
                    "WHERE c.availabilityID = ? " +
                    "GROUP BY no_members_enrolled, r.capacity";
            try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
                pstmt.setInt(1, availabilityID);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int noMembersEnrolled = rs.getInt("no_members_enrolled");
                    int capacity = rs.getInt("capacity");

                    if(noMembersEnrolled == capacity){
                        Connection conn = getConnection();
                        String query2 = "UPDATE Classes SET status = ? WHERE availabilityID = ?";
                        try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {

                            String newStatus = "Full";
                            pstmt2.setString(1, newStatus);
                            pstmt2.setInt(2, availabilityID);
                            int rowsAffected2 = pstmt2.executeUpdate();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        Connection conn = getConnection();
                        String query2 = "UPDATE Classes SET status = ? WHERE availabilityID = ?";
                        try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {
                            // Set parameters for the UPDATE statement
                            String newStatus = "Available";
                            pstmt2.setString(1, newStatus);
                            pstmt2.setInt(2, availabilityID);
                            int rowsAffected2 = pstmt2.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
        }
        return false;
    }

    public static void viewClassesRegistered(int memberId){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT ta.availabilityID, ta.date, tr.trainer_fName, tr.trainer_lName " +
                    "FROM TrainerAvailability ta " +
                    "JOIN Classes c ON ta.availabilityID = c.availabilityID " +
                    "JOIN Trainers tr ON ta.trainerID = tr.trainerID " +
                    "JOIN ClassEnrollment ce ON ta.availabilityID = ce.availabilityID " +
                    "WHERE ce.memberID = ?";

            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int availID = rs.getInt("availabilityID");
                String date = rs.getString("date");
                String trainerFirstName = rs.getString("trainer_fName");
                String trainerLastName = rs.getString("trainer_lName");

                System.out.println("Availability ID: " + availID);
                System.out.println("Date: " + date);
                System.out.println("Trainer: " + trainerFirstName + " " + trainerLastName);
            } else {
                System.out.println("Member with ID " + memberId + " is not enrolled in class with availability ID ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Going back to main menu!");
        joinClass(memberId);

    }
}

