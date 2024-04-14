import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class MemberPrivateRegistration {

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

    public static void joinPrivateSession(int memberId) {
        boolean running = true;

        while (running) {
            System.out.println("Welcome to Private Sesssion Registration");
            System.out.println("1. View private class schedule."); //done
            System.out.println("2. View the private classes that you are registered in");
            System.out.println("3. Register for a private class."); // done
            System.out.println("4. Cancel a private class with a trainer.");
            System.out.println("5. Go back to the previous page."); // done
            System.out.println("6. Exit"); // done
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewPrivateSession(memberId);
                    running = false;
                    break;
                case 2:
                    viewPrivateSessionRegistered(memberId);
                    running = false;
                    break;
                case 3:
                    registerPrivate(memberId);
                    running = false;
                    break;
                case 4:
                    cancelPrivateSession(memberId);
                    running = false;
                    break;
                case 5:
                    MemberFunctions.nextScreen("Member", memberId);
                    running = false;
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
    public static void viewPrivateSession(int memberId) {
        Connection connection = getConnection();

            String query = "SELECT * FROM PrivateClassesCalendar";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();

                System.out.println("===============================================================================================");
                System.out.printf("%-15s%-20s%-20s%-15s%-15s%-15s%-15s%n",
                        "Availability ID ", "Trainer First Name ", "Trainer Last Name",
                        "Status", "Date", "Start Time", "End Time");
                System.out.println("===============================================================================================");
                while (rs.next()) {
                    int availabilityID = rs.getInt("availabilityID");
                    String trainerFirstName = rs.getString("trainer_fName");
                    String trainerLastName = rs.getString("trainer_lName");
                    String status = rs.getString("status");
                    String date = rs.getDate("date").toString();
                    String startTime = rs.getTime("start_time").toString();
                    String endTime = rs.getTime("end_time").toString();

                    System.out.printf("%-15s%-20s%-20s%-15s%-15s%-15s%-15s%n",
                            availabilityID, trainerFirstName, trainerLastName,
                            status, date, startTime, endTime);
                }
                System.out.println("===============================================================================================");
                System.out.println("Going back to menu");
                joinPrivateSession(memberId);
            }catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void registerPrivate(int memberID) {

        System.out.println("Select the class availability ID to register.");
        int availabilityID = scanner.nextInt();
        scanner.nextLine();

        LocalDate localDate = LocalDate.now();
        Date sqlDate = Date.valueOf(localDate);

        if (MemberRegistration.checkClass(memberID, availabilityID) && MemberRegistration.checkSession(memberID, availabilityID) && SessionFull(availabilityID)) {
            if (checkSubscription(memberID).equals("Basic")) {

                int invoice = generateInvoice();

                if (invoice != -1) {
                    Connection newConnect = getConnection();
                    String query = "INSERT INTO MemberPurchaseTrainerSession (memberID, availabilityID, invoice_id, enrollment_date) " +
                            "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
                        pstmt.setInt(1, memberID);
                        pstmt.setInt(2, availabilityID);
                        pstmt.setInt(3, invoice);
                        pstmt.setDate(4, sqlDate);

                        // Execute the INSERT statement
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Purchase recorded successfully.");
                            updateSession(availabilityID);
                            System.out.println("Going back to menu");
                            joinPrivateSession(memberID);
                        } else {
                            System.out.println("Failed to record purchase.");
                            joinPrivateSession(memberID);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                System.out.println("No payment necessary! Since you have a permium subscription");
                Connection newConnect = getConnection();
                String query = "INSERT INTO MemberPurchaseTrainerSession (memberID, availabilityID, invoice_id, enrollment_date) " +
                        "VALUES (?, ?, NULL, ?)";

                try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
                    pstmt.setInt(1, memberID);
                    pstmt.setInt(2, availabilityID);
                    pstmt.setDate(3, sqlDate);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Session Registered");
                        updateSession(availabilityID);
                        System.out.println("Going back to menu");
                        joinPrivateSession(memberID);
                    } else {
                        System.out.println("Failed to register");
                        joinPrivateSession(memberID);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public static void updateSession(int availabilityID) {
        Connection conn = getConnection();

            String query = "UPDATE TrainerAvailability SET status = 'Full' WHERE availabilityID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Set availabilityID as parameter
                pstmt.setInt(1, availabilityID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Status updated to 'Full' for availabilityID: " + availabilityID);
                } else {
                    System.out.println("Availability ID not found.");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static boolean SessionFull(int availabilityID){
        Connection newConnect = getConnection();

        boolean isAvailable = false;

            String query = "SELECT status FROM TrainerAvailability WHERE availabilityID = ?";
            try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
                pstmt.setInt(1, availabilityID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String status = rs.getString("status");
                    // Check if trainer status is 'Available'
                    if (status.equals("Available")) {
                        isAvailable = true;
                    }
                } else {
                    System.out.println("Availability ID not found.");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        return isAvailable;
    }

    public static String checkSubscription(int memberID) {
        Connection newConnect = getConnection();

            String query = "SELECT s.subscription_type " +
                    "FROM SubscribesTo st " +
                    "JOIN Subscriptions s ON st.subscription_id = s.subscription_id " +
                    "WHERE st.memberID = ?";
            try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
                // Set memberID as parameter
                pstmt.setInt(1, memberID);

                // Execute the SELECT statement
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String subscriptionType = rs.getString("subscription_type");
                    return subscriptionType;
                } else {
                    System.out.println("Member with ID " + memberID + " does not have a subscription.");
                    return "";
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            return "";
    }

    public static int generateInvoice() {

        System.out.println("You are about to purchase a private session, would you like to proceed? (Yes/No)");
        String input = scanner.nextLine();

        java.util.Date utilDate = new java.util.Date();
        java.sql.Date paymentDate = new java.sql.Date(utilDate.getTime());

        if (input.equals("Yes")) {
            Connection newConnect = getConnection();
            String query = "INSERT INTO InvoicesTrainerSession (processedby, amount, paid, payment_date, processed_date) " +
                    "VALUES (NULL, ?, ?, ?, NULL)";
            try (PreparedStatement pstmt = newConnect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDouble(1, 50.00);
                pstmt.setBoolean(2, true);
                pstmt.setDate(3, paymentDate);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int invoiceId = generatedKeys.getInt(1);
                        System.out.println("Invoice paid successfully. Invoice ID: " + invoiceId);
                        return invoiceId;
                    }
                } else {
                    System.out.println("Failed to pay invoice.");
                    return -1;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return -1;
    }

    public static void cancelPrivateSession(int memberId){

        System.out.println("Welcome to private session cancellation!");
        System.out.println("Enter the availabilityID of the private session that you would like to cancel");
        int availabilityID = scanner.nextInt();
        scanner.nextLine();

        Connection newConnect = getConnection();
        int invoiceID = -1;
        String selectInvoiceSQL = "SELECT invoice_id FROM MemberPurchaseTrainerSession WHERE memberID = ? AND availabilityID = ?";
        try (PreparedStatement selectStmt = newConnect.prepareStatement(selectInvoiceSQL)) {
            selectStmt.setInt(1, memberId);
            selectStmt.setInt(2, availabilityID);
            // Execute query to get invoice ID
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    invoiceID = rs.getInt("invoice_id");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if (invoiceID != -1) {
            String deleteMemberPurchaseSQL = "DELETE FROM MemberPurchaseTrainerSession WHERE memberID = ? AND availabilityID = ?";
            try (PreparedStatement deleteMemberStmt = newConnect.prepareStatement(deleteMemberPurchaseSQL)) {
                deleteMemberStmt.setInt(1, memberId);
                deleteMemberStmt.setInt(2, availabilityID);
                deleteMemberStmt.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }

            String deleteInvoiceSQL = "DELETE FROM InvoicesTrainerSession WHERE invoice_id = ?";
            try (PreparedStatement deleteStmt = newConnect.prepareStatement(deleteInvoiceSQL)) {
                deleteStmt.setInt(1, invoiceID);
                deleteStmt.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }

            String updateStatusSQL = "UPDATE TrainerAvailability SET status = 'Available' WHERE availabilityID = ?";
            try (PreparedStatement pstmt = newConnect.prepareStatement(updateStatusSQL)) {
                pstmt.setInt(1, availabilityID);
                pstmt.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Cancellation completed successfully.");
        } else {
            System.out.println("No invoice found for the given member and availability.");
        }
        System.out.println("Going back to main menu!");
        joinPrivateSession(memberId);
    }

    public static void viewPrivateSessionRegistered(int memberId){

        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT ta.availabilityID, tr.trainer_fName, tr.trainer_lName, " +
                    "ta.date, ta.start_time, ta.end_time " +
                    "FROM TrainerAvailability ta " +
                    "JOIN Trainers tr ON ta.trainerID = tr.trainerID " +
                    "JOIN MemberPurchaseTrainerSession mp ON ta.availabilityID = mp.availabilityID " +
                    "WHERE mp.memberID = ?";

            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, memberId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int availabilityID = rs.getInt("availabilityID");
                String trainerFirstName = rs.getString("trainer_fName");
                String trainerLastName = rs.getString("trainer_lName");
                String date = rs.getString("date");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");

                System.out.println("Availability ID: " + availabilityID);
                System.out.println("Trainer: " + trainerFirstName + " " + trainerLastName);
                System.out.println("Date: " + date);
                System.out.println("Time: " + startTime + " - " + endTime);
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Going back to main menu!");
        joinPrivateSession(memberId);
    }

}
