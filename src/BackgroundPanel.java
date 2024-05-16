import javax.swing.*;
import java.awt.*;

/**
 * The BackgroundPanel class extends JPanel to provide a background image feature.
 * It loads an image from a file and displays it as the background of the panel.
 */
public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    /**
     * Constructs a BackgroundPanel object that loads an image to be used as the background.
     * The image is specified by the filename "duna.jpg".
     */
    public BackgroundPanel() {
        try {
            backgroundImage = new ImageIcon("duna.jpg").getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Paints the background image onto the panel.
     *
     * @param g the Graphics object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}