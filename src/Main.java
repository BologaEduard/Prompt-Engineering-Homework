/**
 * The Main class serves as the entry point for the application.
 * It initializes the menu window and makes it visible.
 */
public class Main {
    /**
     * The main method creates an instance of the Menu class and sets it to be visible.
     * This starts the graphical user interface for the application.
     *
     * @param args The command-line arguments (not used).
     */
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.setVisible(true);
    }
}