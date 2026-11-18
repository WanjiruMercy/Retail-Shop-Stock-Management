import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ShopAppFrame extends JFrame {

    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    // --- Form fields (top-left: product management) ---
    private final JTextField idField    = new JTextField(6);
    private final JTextField nameField  = new JTextField(15);
    private final JTextField priceField = new JTextField(8);
    private final JTextField stockField = new JTextField(6);

    // --- Sale panel fields (top-right) ---
    private final JComboBox<Product> saleProductBox = new JComboBox<>();
    private final JTextField saleQtyField = new JTextField(5);
    private final JComboBox<String> paymentBox = new JComboBox<>(
            new String[]{"Cash", "Mobile money", "Card"});
    private final JLabel totalLabel = new JLabel("Total: KSh 0.00");

    // --- Table showing all products ---
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Price", "Stock"}, 0);
    private final JTable productTable = new JTable(tableModel);

    public ShopAppFrame() {
        super("Retail Shop Stock Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildProductForm(),  BorderLayout.NORTH);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(buildSalePanel(),    BorderLayout.SOUTH);

        refreshProducts();
    }

    // ---------------------------------------------------------------
    // UI BUILDERS
    // ---------------------------------------------------------------

    private JPanel buildProductForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Products"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("ID:"), c);
        c.gridx = 1;              panel.add(idField, c);

        c.gridx = 2;              panel.add(new JLabel("Name:"), c);
        c.gridx = 3;              panel.add(nameField, c);

        c.gridx = 4;              panel.add(new JLabel("Price:"), c);
        c.gridx = 5;              panel.add(priceField, c);

        c.gridx = 6;              panel.add(new JLabel("Stock:"), c);
        c.gridx = 7;              panel.add(stockField, c);

        JButton addBtn    = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn  = new JButton("Clear");
        JButton totalBtn  = new JButton("Total Stock Value");

        c.gridy = 1;
        c.gridx = 0; panel.add(addBtn, c);
        c.gridx = 1; panel.add(updateBtn, c);
        c.gridx = 2; panel.add(deleteBtn, c);
        c.gridx = 3; panel.add(clearBtn, c);
        c.gridx = 4; panel.add(totalBtn, c);

        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        clearBtn.addActionListener(e -> clearForm());
        totalBtn.addActionListener(e -> onShowTotalStockValue());

        return panel;
    }

    private JPanel buildSalePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Record Sale"));

        panel.add(new JLabel("Product:"));
        saleProductBox.setPreferredSize(new Dimension(220, 25));
        panel.add(saleProductBox);

        panel.add(new JLabel("Qty:"));
        panel.add(saleQtyField);

        panel.add(new JLabel("Payment:"));
        panel.add(paymentBox);

        JButton sellBtn = new JButton("Sell");
        panel.add(sellBtn);

        panel.add(totalLabel);

        sellBtn.addActionListener(e -> onSell());

        return panel;
    }

    // ---------------------------------------------------------------
    // ACTIONS
    // ---------------------------------------------------------------

    private void refreshProducts() {
        tableModel.setRowCount(0);                       // clear table
        saleProductBox.removeAllItems();

        List<Product> products = productDAO.getAllProducts();

        // LOOP over products to fill the table AND the combo box
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(),
                String.format("%.2f", p.getPrice()),
                p.getQuantityInStock()
            });
            saleProductBox.addItem(p);

            // LOW-STOCK WARNING (if/else)
            if (p.getQuantityInStock() < 5) {
                System.out.println("Low stock: " + p.getName() +
                        " (only " + p.getQuantityInStock() + " left)");
            }
        }
    }

    private void onAdd() {
        // VALIDATION (if/else)
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a product name.");
            return;
        }
        double price;
        int stock;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            stock = Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Price must be a number and stock must be a whole number.");
            return;
        }
        if (price < 0 || stock < 0) {
            JOptionPane.showMessageDialog(this, "Price and stock cannot be negative.");
            return;
        }

        try {
            productDAO.insertProduct(nameField.getText().trim(), price, stock);
            refreshProducts();
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Insert failed: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a product from the table first.");
            return;
        }
        int id;
        double price;
        int stock;
        try {
            id    = Integer.parseInt(idField.getText().trim());
            price = Double.parseDouble(priceField.getText().trim());
            stock = Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
            return;
        }
        try {
            productDAO.updateProduct(id, nameField.getText().trim(), price, stock);
            refreshProducts();
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void onDelete() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a product from the table first.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid id.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete product " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            productDAO.deleteProduct(id);
            refreshProducts();
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void onShowTotalStockValue() {
        // THE GRADED ARRAY + LOOP
        double[] prices = productDAO.loadAllPricesAsArray();
        List<Product> products = productDAO.getAllProducts();

        double total = 0;
        for (int i = 0; i < prices.length; i++) {
            total += prices[i] * products.get(i).getQuantityInStock();
        }
        JOptionPane.showMessageDialog(this,
                String.format("Total stock value: KSh %.2f", total));
    }

    private void onSell() {
        Product p = (Product) saleProductBox.getSelectedItem();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Choose a product first.");
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(saleQtyField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number.");
            return;
        }

        // REFUSE SALE (if/else)
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
            return;
        }
        if (qty > p.getQuantityInStock()) {
            JOptionPane.showMessageDialog(this,
                    "Not enough stock. Only " + p.getQuantityInStock() + " left.");
            return;
        }

        double subtotal = qty * p.getPrice();
        double discount = 0;
        if (subtotal > 5000) {
            discount = subtotal * 0.05;         // 5% discount
        }
        double total = subtotal - discount;

        // PAYMENT METHOD (switch)
        String payment = (String) paymentBox.getSelectedItem();
        String receiptMessage;
        switch (payment) {
            case "Cash":
                receiptMessage = "Receipt: Cash payment received.";
                break;
            case "Mobile money":
                receiptMessage = "Receipt: Mobile money confirmed.";
                break;
            case "Card":
                receiptMessage = "Receipt: Card payment approved.";
                break;
            default:
                receiptMessage = "Receipt: payment method unknown.";
        }

        try {
            boolean ok = saleDAO.recordSale(p.getId(), qty, p.getPrice());
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Sale failed: stock changed.");
                return;
            }
            totalLabel.setText(String.format("Total: KSh %.2f", total));

            String receipt = String.format(
                    "Sold %d x %s\nSubtotal: KSh %.2f\nDiscount: KSh %.2f\nTotal: KSh %.2f\n%s",
                    qty, p.getName(), subtotal, discount, total, receiptMessage);
            JOptionPane.showMessageDialog(this, receipt, "Sale complete",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshProducts();
            saleQtyField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Sale failed: " + ex.getMessage());
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        saleQtyField.setText("");
    }
}