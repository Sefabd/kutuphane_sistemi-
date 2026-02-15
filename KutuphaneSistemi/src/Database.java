import java.sql.Connection;
import java.sql.DriverManager;


public class Database {
    // XAMPP : kullanıcı "root", şifre boş
    private static final String URL = "jdbc:mysql://localhost:3306/kutuphane_db?useUnicode=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection baglantiGetir() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Bağlantı Hatası: " + e.getMessage());
        }
        return conn;
    }
}