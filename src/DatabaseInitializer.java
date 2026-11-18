import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the SQLite database file, tables, and sample data
 * the first time the program runs. If the database already
 * has products, it does nothing.
 */
public class DatabaseInitializer {

    // Path to the SQLite file. Relative to where you run the program.
    public static final String DB_URL = "jdbc:sqlite:shop.db";

    /**
     * Call this once at program start.
     * It is safe to call every time - it only seeds data when empty.
     */
    public static void initialize() {
        // Explicitly load the SQLite driver so we get a clear
        // error if the jar is missing from the classpath.
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: SQLite JDBC driver not on classpath.");
            System.out.println("Check .vscode/settings.json and the lib/ folder.");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            createTables(conn);

            if (isEmpty(conn)) {
                insertSampleData(conn);
                System.out.println("Database created and sample data inserted.");
            } else {
                System.out.println("Database already exists - skipping sample data.");
            }

        } catch (SQLException e) {
            System.out.println("Database initialization failed:");
            e.printStackTrace();
        }
    }

    /** Creates Products and Sales tables if they do not already exist. */
    private static void createTables(Connection conn) throws SQLException {
        String createProducts =
            "CREATE TABLE IF NOT EXISTS Products (" +
            "  product_id INTEGER PRIMARY KEY, " +
            "  product_name TEXT NOT NULL, " +
            "  price REAL NOT NULL, " +
            "  quantity_in_stock INTEGER NOT NULL" +
            ")";

        String createSales =
            "CREATE TABLE IF NOT EXISTS Sales (" +
            "  sale_id INTEGER PRIMARY KEY, " +
            "  product_id INTEGER NOT NULL, " +
            "  quantity_sold INTEGER NOT NULL, " +
            "  sale_date TEXT NOT NULL, " +
            "  FOREIGN KEY (product_id) REFERENCES Products(product_id)" +
            ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createProducts);
            stmt.execute(createSales);
        }
    }

    /** Returns true if the Products table has zero rows. */
    private static boolean isEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Products")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    /** Inserts the same sample data that is in database.sql. */
    private static void insertSampleData(Connection conn) throws SQLException {
        Object[][] products = {
            {1, "Sugar 2kg",         250.00, 20},
            {2, "Rice 5kg",          650.00, 15},
            {3, "Soap Bar",          120.00,  3},
            {4, "Cooking Oil 1L",    380.00, 10},
            {5, "Salt 1kg",           60.00, 25},
            {6, "Tea Leaves 500g",   450.00,  8}
        };

        String insertProduct =
            "INSERT INTO Products (product_id, product_name, price, quantity_in_stock) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertProduct)) {
            for (Object[] p : products) {
                ps.setInt(1, (Integer) p[0]);
                ps.setString(2, (String) p[1]);
                ps.setDouble(3, (Double) p[2]);
                ps.setInt(4, (Integer) p[3]);
                ps.executeUpdate();
            }
        }

        Object[][] sales = {
            {1, 1, 2, "2026-11-10"},
            {2, 2, 1, "2026-11-10"},
            {3, 4, 3, "2026-11-11"}
        };

        String insertSale =
            "INSERT INTO Sales (sale_id, product_id, quantity_sold, sale_date) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertSale)) {
            for (Object[] s : sales) {
                ps.setInt(1, (Integer) s[0]);
                ps.setInt(2, (Integer) s[1]);
                ps.setInt(3, (Integer) s[2]);
                ps.setString(4, (String) s[3]);
                ps.executeUpdate();
            }
        }
    }
}