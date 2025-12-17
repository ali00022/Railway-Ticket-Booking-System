import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    public List<String> getDestinations() {
        List<String> cities = new ArrayList<>();
        String query = "SELECT DISTINCT destination FROM trains ORDER BY destination";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while(rs.next()) {
                cities.add(rs.getString("destination"));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return cities;
    }

    public void showTrainsForDestination(String destination) {
        String query = "SELECT * FROM trains WHERE destination ILIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, destination);
            ResultSet rs = pst.executeQuery();
            
            System.out.println("\n--- TRAINS TO " + destination.toUpperCase() + " ---");
            System.out.printf("%-5s | %-20s | %-10s | %-10s | %-10s%n", "ID", "Name", "Departs", "Arrives", "Price");
            System.out.println("-------------------------------------------------------------------");
            
            while(rs.next()) {
                System.out.printf("%-5d | %-20s | %-10s | %-10s | Rs.%-10.2f%n",
                    rs.getInt("train_id"),
                    rs.getString("train_name"),
                    rs.getTime("departure_time"),
                    rs.getTime("arrival_time"),
                    rs.getDouble("price"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void showAddons() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM addons")) {
             
            System.out.println("\n--- AVAILABLE ADD-ONS ---");
            while(rs.next()) {
                System.out.printf("%d. %s (Rs.%.2f)%n", 
                    rs.getInt("addon_id"), rs.getString("item_name"), rs.getDouble("price"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void createBooking(String name, int age, String gender, String phone, int trainId, int addonId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            double price = 0;
            PreparedStatement pStmt = conn.prepareStatement("SELECT price FROM trains WHERE train_id=?");
            pStmt.setInt(1, trainId);
            ResultSet rs = pStmt.executeQuery();
            if(rs.next()) price = rs.getDouble("price");
            else { 
                System.out.println("[ERROR] Invalid Train ID"); 
                return; 
            }

            if(addonId > 0) {
                PreparedStatement aStmt = conn.prepareStatement("SELECT price FROM addons WHERE addon_id=?");
                aStmt.setInt(1, addonId);
                ResultSet rs2 = aStmt.executeQuery();
                if(rs2.next()) price += rs2.getDouble("price");
            }

            String insert = "INSERT INTO bookings (passenger_name, passenger_age, passenger_gender, passenger_phone, train_id, total_amount) VALUES (?, ?, ?, ?, ?, ?) RETURNING booking_id";
            PreparedStatement bStmt = conn.prepareStatement(insert);
            bStmt.setString(1, name);
            bStmt.setInt(2, age);
            bStmt.setString(3, gender);
            bStmt.setString(4, phone);
            bStmt.setInt(5, trainId);
            bStmt.setDouble(6, price);
            
            ResultSet rsBooking = bStmt.executeQuery();
            if(rsBooking.next()) {
                int bookingId = rsBooking.getInt("booking_id");
                
                if(addonId > 0) {
                    PreparedStatement linkStmt = conn.prepareStatement("INSERT INTO booking_addons (booking_id, addon_id) VALUES (?, ?)");
                    linkStmt.setInt(1, bookingId);
                    linkStmt.setInt(2, addonId);
                    linkStmt.executeUpdate();
                }
                
                conn.commit();
                System.out.println("\n[SUCCESS] BOOKING CONFIRMED!");
                System.out.println("   Booking ID: " + bookingId);
                System.out.println("   Total Amount Paid: Rs." + price);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getBookingDetails(int bookingId) {
        String query = "SELECT b.booking_id, b.passenger_name, b.booking_date, b.total_amount, " +
                       "t.train_name, t.source, t.destination, t.departure_time " +
                       "FROM bookings b " +
                       "JOIN trains t ON b.train_id = t.train_id " +
                       "WHERE b.booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, bookingId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("\n-----------------------------------------");
                System.out.println("           BOOKING DETAILS             ");
                System.out.println("-----------------------------------------");
                System.out.println(" Booking ID    : " + rs.getInt("booking_id"));
                System.out.println(" Passenger     : " + rs.getString("passenger_name"));
                System.out.println(" Train         : " + rs.getString("train_name"));
                System.out.println(" Route         : " + rs.getString("source") + " >> " + rs.getString("destination"));
                System.out.println(" Departure     : " + rs.getTime("departure_time"));
                System.out.println(" Total Paid    : Rs." + rs.getDouble("total_amount"));
                System.out.println(" Date Booked   : " + rs.getTimestamp("booking_date"));
                System.out.println("-----------------------------------------");
            } else {
                System.out.println("\n[INFO] No booking found with ID: " + bookingId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelBooking(int bookingId) {
        String deleteAddons = "DELETE FROM booking_addons WHERE booking_id = ?";
        String deleteBooking = "DELETE FROM bookings WHERE booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement pst1 = conn.prepareStatement(deleteAddons)) {
                pst1.setInt(1, bookingId);
                pst1.executeUpdate();
            }

            try (PreparedStatement pst2 = conn.prepareStatement(deleteBooking)) {
                pst2.setInt(1, bookingId);
                int rows = pst2.executeUpdate();

                if (rows > 0) {
                    conn.commit();
                    System.out.println("\n[SUCCESS] Booking " + bookingId + " has been CANCELLED.");
                    System.out.println("   Refund Initiated...");
                } else {
                    System.out.println("\n[ERROR] Could not cancel. Booking ID " + bookingId + " not found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}