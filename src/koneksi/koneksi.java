package koneksi;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class koneksi {
    private static Connection mysqlconfig;
    public static Connection configDB()throws SQLException {
        try {
            String url ="jdbc:mysql://localhost:3307/absensiez"; 
            String user ="root";
            String pass ="";
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            mysqlconfig = DriverManager.getConnection(url, user,pass);
        } catch(Exception e) {
            System.err.println("Koneksi gagal"+e.getMessage());
        } 
        return mysqlconfig;
    } 
}
