/**
 * Created by Umar on 10-Mar-16.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/geocitylite";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "seecs@123";

    public static Connection getConnection() {
        Connection con = null;
        try {
            // load the Driver Class
            Class.forName(DB_DRIVER_CLASS);

            // create the connection now
            con = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}