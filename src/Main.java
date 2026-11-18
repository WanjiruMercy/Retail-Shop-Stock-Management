import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // First-time DB setup
        DatabaseInitializer.initialize();

        // Launch the window on the Swing event thread (standard practice)
        SwingUtilities.invokeLater(() -> {
            ShopAppFrame frame = new ShopAppFrame();
            frame.setVisible(true);
        });
    }
}