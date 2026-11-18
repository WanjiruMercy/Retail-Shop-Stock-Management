import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {

    public static void main(String[] args) {
        // Step 1: create the DB and seed sample data (only does work the first time)
        DatabaseInitializer.initialize();

        // Step 2: read it back to prove the connection works
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: SQLite JDBC driver not on classpath.");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.DB_URL);
             Statement stmt = conn.createStatement()) {

            System.out.println("Connected to SQLite successfully!");
            System.out.println("--- Products in database ---");

            ResultSet rs = stmt.executeQuery(
                "SELECT product_id, product_name, price, quantity_in_stock " +
                "FROM Products ORDER BY product_id");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("product_id") + " | " +
                    rs.getString("product_name") + " | KSh " +
                    rs.getDouble("price") + " | stock: " +
                    rs.getInt("quantity_in_stock")
                );
            }
            rs.close();

            System.out.println("--- Sales in database ---");
            ResultSet rs2 = stmt.executeQuery(
                "SELECT sale_id, product_id, quantity_sold, sale_date FROM Sales ORDER BY sale_id");
            while (rs2.next()) {
                System.out.println(
                    "Sale " + rs2.getInt("sale_id") +
                    " | product " + rs2.getInt("product_id") +
                    " | qty " + rs2.getInt("quantity_sold") +
                    " | " + rs2.getString("sale_date")
                );
            }
            rs2.close();

        } catch (Exception e) {
            System.out.println("Connection failed:");
            e.printStackTrace();
        }
    }
}