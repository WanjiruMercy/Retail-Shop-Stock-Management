import java.sql.*;
import java.time.LocalDate;

/**
 * Data Access Object for the Sales table.
 * Every sale reduces the stock of the matching product.
 */
public class SaleDAO {

    /**
     * Records a sale and reduces the product's stock.
     * Returns true on success, false if there was not enough stock.
     *
     * This uses a JDBC transaction so a crash mid-way cannot leave
     * a sale recorded without the stock being reduced.
     */
    public boolean recordSale(int productId, int quantity, double unitPrice) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.DB_URL)) {

            // Check stock first
            int currentStock = getStock(conn, productId);
            if (quantity <= 0 || quantity > currentStock) {
                return false;
            }

            conn.setAutoCommit(false);   // start transaction
            try {
                // 1) insert the sale
                String insert = "INSERT INTO Sales (product_id, quantity_sold, sale_date) " +
                                "VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, quantity);
                    ps.setString(3, LocalDate.now().toString());
                    ps.executeUpdate();
                }

                // 2) reduce stock
                String update = "UPDATE Products SET quantity_in_stock = quantity_in_stock - ? " +
                                "WHERE product_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private int getStock(Connection conn, int productId) throws SQLException {
        String sql = "SELECT quantity_in_stock FROM Products WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}