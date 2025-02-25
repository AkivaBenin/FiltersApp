import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private ImagePanel imagePanel;

    public MainWindow() {
        setTitle("Image Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        imagePanel = new ImagePanel(this);
        add(imagePanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
