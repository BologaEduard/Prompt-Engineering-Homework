import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Menu class extends JFrame and implements ActionListener to handle user interactions
 * with various buttons that navigate different features of the application.
 * Each button corresponds to a different aspect of essay management on topics such as Technology,
 * Environment, Politics, and Custom essay generation, along with an exit option.
 */
public class Menu extends JFrame implements ActionListener {
    private JButton btnTechnology;
    private JButton btnEnvironment;
    private JButton btnPolitics;
    private JButton btnExit;
    private JButton btnCustom;
    private JPanel panel;

    /**
     * Constructs the main menu window with layout and button initialization.
     * Sets the window properties and organizes buttons within a GridBagLayout.
     */
    public Menu() {
        setTitle("Essay Topics");
        setSize(900, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new BackgroundPanel();
        panel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("Danube Essays");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 75)); // Changed font to Impact and increased size
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridwidth = GridBagConstraints.REMAINDER;
        titleConstraints.insets = new Insets(20, 0, 20, 0); // Top padding
        titleConstraints.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, titleConstraints);

        btnTechnology = new JButton("Technology");
        btnTechnology.setActionCommand("Technology");
        btnTechnology.addActionListener(this);

        btnEnvironment = new JButton("Environment");
        btnEnvironment.setActionCommand("Environment");
        btnEnvironment.addActionListener(this);

        btnPolitics = new JButton("Politics");
        btnPolitics.setActionCommand("Politics");
        btnPolitics.addActionListener(this);

        btnExit = new JButton("Exit");
        btnExit.setActionCommand("Exit");
        btnExit.addActionListener(this);

        btnCustom = new JButton("Custom");
        btnCustom.setActionCommand("Custom");
        btnCustom.addActionListener(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        panel.add(btnTechnology, gbc);
        panel.add(btnEnvironment, gbc);
        panel.add(btnPolitics, gbc);
        panel.add(btnCustom, gbc);
        panel.add(btnExit, gbc);

        add(panel, BorderLayout.CENTER);
    }

    /**
     * Handles action events triggered by the menu buttons.
     * This method responds by opening the appropriate dialog based on which button is pressed.
     *
     * @param e The event that triggered the method.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Technology".equals(command)) {
            TechnologyDialog dialog = new TechnologyDialog(this);
            dialog.setVisible(true);
        } else if ("Environment".equals(command)) {
            EnvironmentDialog dialog = new EnvironmentDialog(this);
            dialog.setVisible(true);
        } else if ("Politics".equals(command)) {
            PoliticsDialog dialog = new PoliticsDialog(this);
            dialog.setVisible(true);
        } else if ("Exit".equals(command)) {
            System.exit(0);
        } else if ("Custom".equals(command)) {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setVisible(true);
        }
    }


}
