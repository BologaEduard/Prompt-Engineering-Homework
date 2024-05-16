import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * EnvironmentDialog provides a GUI to generate essays about environmental aspects of the Danube River.
 * It allows users to select from predefined topics via a JComboBox and view the generated essay.
 */
public class EnvironmentDialog extends JDialog implements ActionListener {
    private JTextArea txtResponse;
    private JButton btnGenerate;
    private JComboBox<String> cmbTopics;
    private String API_KEY = ""; // Replace with your actual API key

    /**
     * Constructs an EnvironmentDialog window with controls for selecting a topic and initiating essay generation.
     *
     * @param parent The parent frame to which this dialog is attached.
     */
    public EnvironmentDialog(Frame parent) {
        super(parent, "Environment Essay", true);
        setSize(800, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // ComboBox for environmental topics
        String[] topics = {
                "Water Quality",
                "Biodiversity",
                "Flood Management",
                "Pollution Control",
                "Climate Impact"
        };
        cmbTopics = new JComboBox<>(topics);
        cmbTopics.setSelectedIndex(-1); // No selection by default

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Select Topic:"));
        controlPanel.add(cmbTopics);

        btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(this);
        controlPanel.add(btnGenerate);

        getContentPane().add(controlPanel, BorderLayout.NORTH);

        txtResponse = new JTextArea();
        txtResponse.setLineWrap(true);
        txtResponse.setWrapStyleWord(true);
        txtResponse.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtResponse);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Handles action events from the Generate button by initiating essay generation.
     *
     * @param e The ActionEvent that triggered the handler.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGenerate && cmbTopics.getSelectedIndex() != -1) {
            generateEssay();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a topic before generating an essay.", "No Topic Selected", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Generates an essay based on the selected environmental topic and updates the text area with the response.
     */
    private void generateEssay() {
        String selectedTopic = (String) cmbTopics.getSelectedItem();
        String prompt = "Generate an essay about the environmental features of the Danube in the chosen topic: " + selectedTopic;

        Thread thread = new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + API_KEY)
                        .POST(BodyPublishers.ofString("{\"model\": \"gpt-3.5-turbo\", \"messages\":[{\"role\":\"user\",\"content\":\"" + prompt + "\"}]}"))
                        .build();
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                // Parse the JSON response to extract only the content of the essay
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String content = message.getString("content");

                    SwingUtilities.invokeLater(() -> txtResponse.setText(content));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> txtResponse.setText("Error: " + ex.getMessage()));
            }
        });
        thread.start();
    }
}

