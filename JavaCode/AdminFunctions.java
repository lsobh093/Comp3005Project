import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class AdminFunctions {
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

    public static void nextScreen(String userType, int adminID) {

        int choice = 0;
        boolean running = true;

        while (running) {
            System.out.println("Please select from the following options: ");
            System.out.println("1. View trainer availability and assign room bookings.");
            System.out.println("2. View transactions to process");
            System.out.println("3. Monitor equipment maintenance");
            System.out.println("4. Exist the system");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewTrainerCalendar(adminID);
                    running = false;
                    break;
                case 2:
                    viewTransactions(adminID);
                    running = false;
                    break;
                case 3:
                    checkEquipment(adminID);
                    running = false;
                    break;
                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 4.");
            }

        }
        scanner.close();
    }

    public static void viewTrainerCalendar(int adminID) {

        Connection conn = getConnection();

        String query = "SELECT * FROM ClassAvailability";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print table header
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", metaData.getColumnName(i));
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-20s", rs.getString(i));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Would you like to assign rooms to a specific trainer class availability? (Yes/No)");
        String choice = scanner.nextLine();

        if (choice.equals("Yes")) {
            System.out.println("Choose the availabilityid that you would like to assign");
            int input = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Would you like to print out the gym rooms? (Yes/No)");
            String choice2 = scanner.nextLine();
            if (choice2.equals("Yes")) {
                printRooms();
            }
            assignRoomtoClass(adminID, input);
        } else {
            System.out.println("Goin back to menu");
            nextScreen("Staff", adminID);
        }

    }

    public static void printRooms() {
        Connection conn = getConnection();

        String query = "SELECT * FROM Room";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            System.out.println("=========================================");
            System.out.printf("%-10s %-20s %-10s%n", "RoomID", "Room Name", "Capacity");
            System.out.println("=========================================");

            while (rs.next()) {
                int roomID = rs.getInt("roomID");
                String roomName = rs.getString("room_name");
                int capacity = rs.getInt("capacity");

                System.out.printf("%-10d %-20s %-10d%n", roomID, roomName, capacity);
            }
            System.out.println("=========================================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void assignRoomtoClass(int adminID, int input) {
        Connection conn = getConnection();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a room ID to check if it is available for the chosen availability date:");
        int roomID = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Checking room availability...");
        boolean available = availability(roomID, input);

        if (available) {
            String sqlQuery = "INSERT INTO Classes (room_id, assigned_by_id, availabilityID, no_members_enrolled, status) " +
                    "VALUES (?, ?, ?, NULL, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
                pstmt.setInt(1, roomID);
                pstmt.setInt(2, adminID);
                pstmt.setInt(3, input);
                pstmt.setString(4, "Open");

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Room has been assigned and class has been created!");
                } else {
                    System.out.println("Failed to assign room and create class!");
                }

                System.out.println("Going back to main menu:");
                nextScreen("Staff", adminID);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Room is not available! Choose another room.");
            assignRoomtoClass(adminID, input);
        }
    }

    public static boolean availability(int roomID, int availabilityID) {
        Connection newConnect = getConnection();

        String query = "SELECT ta.date, ta.start_time, ta.end_time " +
                "FROM TrainerAvailability ta " +
                "WHERE ta.availabilityID = ?";
        try (PreparedStatement pstmt = newConnect.prepareStatement(query)) {
            pstmt.setInt(1, availabilityID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String date = rs.getString("date");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                return !isRoomAlreadyAssigned(newConnect, roomID, date, startTime, endTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if an error occurs
    }

    // Method to check if the room is already assigned for the given date and time
    private static boolean isRoomAlreadyAssigned(Connection conn, int roomID, String date, String startTime, String endTime) throws SQLException {
        String query = "SELECT COUNT(*) FROM Classes c " +
                "JOIN TrainerAvailability ta ON c.availabilityID = ta.availabilityID " +
                "WHERE c.room_id = ? " +
                "AND ta.date = ?::date " + // Cast to DATE type
                "AND ((?::time >= ta.start_time AND ?::time < ta.end_time) OR " + // Cast to TIME type
                "(?::time > ta.start_time AND ?::time <= ta.end_time) OR " + // Cast to TIME type
                "(?::time <= ta.start_time AND ?::time >= ta.end_time))"; // Cast to TIME type
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomID);
            pstmt.setString(2, date);
            pstmt.setString(3, startTime);
            pstmt.setString(4, startTime);
            pstmt.setString(5, endTime);
            pstmt.setString(6, endTime);
            pstmt.setString(7, startTime);
            pstmt.setString(8, endTime);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // If count is greater than 0, room is already assigned; otherwise, room is available
            }
        }
        return false;
    }

    public static void checkEquipment(int adminID) {

        System.out.println("Here is a list of the gym equipment:");
        equipmentPrintout();

        System.out.println("Enter the equipment ID number to maintain: (1-10)");
        int choice = scanner.nextInt();
        scanner.nextLine();
        maintainEquipment(adminID, choice);
    }

    public static void maintainEquipment(int adminID, int choice) {
        Connection newConnect = getConnection();

        System.out.println("Enter the nature of maintenance and fee paid");
        System.out.println("Enter Maintenance Type:");
        String maintenanceType = scanner.nextLine();

        System.out.println("Enter Maintenance Description:");
        String maintenanceDescription = scanner.nextLine();

        System.out.println("Enter Maintenance Cost:");
        double maintenanceCost = scanner.nextDouble();
        scanner.nextLine();

        LocalDate localDate = LocalDate.now();
        Date sqlDate = Date.valueOf(localDate);

        String sqlQuery = "INSERT INTO EquipmentMaintenance (equipID, adminID, maintenance_date, maintenance_type, maintenance_description, maintenance_cost) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = newConnect.prepareStatement(sqlQuery)) {
            pstmt.setInt(1, choice);
            pstmt.setInt(2, adminID);
            pstmt.setDate(3, sqlDate);
            pstmt.setString(4, maintenanceType);
            pstmt.setString(5, maintenanceDescription);
            pstmt.setDouble(6, maintenanceCost);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Maintenance record update successfully.");
            } else {
                System.out.println("Failed to update maintenance record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Going back to menu");
        nextScreen("Staff", adminID);

    }
    public static void equipmentPrintout() {
        Connection newConnect = getConnection();

        String query = "SELECT equipID, equipName FROM Equipment";

        try (PreparedStatement statement = newConnect.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            // Print equipment list
            System.out.println("Equipment List:");
            System.out.println("---------------");
            while (resultSet.next()) {
                int equipID = resultSet.getInt("equipID");
                String equipName = resultSet.getString("equipName");
                System.out.println("ID: " + equipID + ", Name: " + equipName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewTransactions(int adminID){

        System.out.println("Welcome to the pending transactions page:");
        System.out.println("Here is a list of the membership purchase transactions");
        memberSubscriptionsTransactions();
        System.out.println();
        System.out.println("Here is a list of the private sessions purchase transactions:");
        privateMemberPurchases();

        System.out.println();

        System.out.println("Would you like to process all transactions? (Yes/No)");
        String answer = scanner.nextLine();
        if (answer.equals("Yes")) {
            processMemberSubscriptions(adminID);
            processPrivateMemberPurchases(adminID);
            System.out.println("Success! Going back to main menu!");
            nextScreen("Staff", adminID);
        }else{
            System.out.println("Going back to main menu!");
            nextScreen("Staff", adminID);
        }
    }

    public static void memberSubscriptionsTransactions() {

        Connection conn = getConnection();

        String sqlQuery = "SELECT * FROM ProcessMemberShipPayment";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            ResultSet rs = pstmt.executeQuery();

            System.out.printf("%-18s | %-10s | %-12s | %-15s\n", "Payment Processed", "Invoice ID", "Processed By", "Date Processed");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                int paymentProcessed = rs.getInt("paymentProcessed");
                int invoiceId = rs.getInt("invoice_id");
                int processedBy = rs.getInt("processedBy");
                Date dateProcessed = rs.getDate("date_processed");

                System.out.printf("%-18d | %-10d | %-12d | %-15s\n", paymentProcessed, invoiceId, processedBy, dateProcessed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void privateMemberPurchases() {

        Connection conn = getConnection();

        String sqlQuery = "SELECT * FROM InvoicesTrainerSession";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
            ResultSet rs = pstmt.executeQuery();

            System.out.printf("%-10s | %-10s | %-10s | %-10s | %-15s | %-15s\n",
                    "Invoice ID", "Processed By", "Amount", "Paid", "Payment Date", "Processed Date");
            System.out.println("---------------------------------------------------------------------------------------");

            while (rs.next()) {
                int invoiceId = rs.getInt("invoice_id");
                int processedBy = rs.getInt("processedby");
                double amount = rs.getDouble("amount");
                boolean paid = rs.getBoolean("paid");
                Date paymentDate = rs.getDate("payment_date");
                Date processedDate = rs.getDate("processed_date");

                System.out.printf("%-10d | %-10d | %-10.2f | %-10s | %-15s | %-15s\n",
                        invoiceId, processedBy, amount, paid, paymentDate, processedDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void processMemberSubscriptions(int adminID){

        LocalDate localDate = LocalDate.now();
        Date sqlDate = Date.valueOf(localDate);

        Connection connection = getConnection();

        String sql = "UPDATE ProcessMemberShipPayment SET processedBy = ?, date_processed = ? WHERE processedBy IS NULL AND date_processed IS NULL";

        try {
            // Create a PreparedStatement object
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Set values for processedBy and date_processed
            preparedStatement.setInt(1, adminID);
            preparedStatement.setDate(2, sqlDate);

            // Execute the update statement
            int rowsUpdated = preparedStatement.executeUpdate();
            if(rowsUpdated > 0) {
                System.out.println("Successfully processed all membership payments");
            }else{
                System.out.println("Failed to process all membership payments");
            }

        } catch (SQLException e) {
            System.err.println("Error updating entries: " + e.getMessage());
        }

    }

    public static void processPrivateMemberPurchases(int adminID){
        Connection newConnect = getConnection();
        LocalDate localDate = LocalDate.now();
        Date sqlDate = Date.valueOf(localDate);

        String sql = "UPDATE InvoicesTrainerSession SET processedby = ?, processed_date = ? WHERE processedBy IS NULL AND processed_date IS NULL";

        try (PreparedStatement pstmt = newConnect.prepareStatement(sql)){
            pstmt.setInt(1, adminID);
            pstmt.setDate(2, sqlDate);

            int rowsUpdated = pstmt.executeUpdate();
            if(rowsUpdated > 0) {
                System.out.println("Successfully processed all private session payments");
            }else{
                System.out.println("Failed to process all private session payments");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
