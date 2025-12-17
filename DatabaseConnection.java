import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    // 1. Database Name: 'railway_system' (You must create this in Windows Postgres!)
    private static final String URL = "jdbc:postgresql://localhost:5432/railway_system";
    
    // 2. User: On Windows, the default is usually 'postgres'
    private static final String USER = "postgres"; 
    
    // 3. Password: The one you typed in the Windows Installer
    private static final String PASSWORD = "661400"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Windows Connection Successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}