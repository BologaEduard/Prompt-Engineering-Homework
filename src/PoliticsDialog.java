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
 * PoliticsDialog provides a GUI to generate essays about the political impacts of the Danube River on various cities.
 * Users select a city from a list and view the generated essay discussing the political influences.
 */
public class PoliticsDialog extends JDialog implements ActionListener {
    private JTextArea txtResponse;
    private JButton btnGenerate;
    private JList<String> listCities;
    private String API_KEY = ""; // Use your actual API key

    /**
     * Constructs a PoliticsDialog window with controls for selecting a city and initiating essay generation.
     *
     * @param parent The parent frame to which this dialog is attached.
     */
    public PoliticsDialog(Frame parent) {
        super(parent, "Political Impacts of the Danube", true);
        setSize(800, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // List of cities
        String[] cities = { "Vienna", "Bratislava", "Budapest", "Belgrade" };
        listCities = new JList<>(cities);
        listCities.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroller = new JScrollPane(listCities);
        listScroller.setPreferredSize(new Dimension(250, 80));

        JPanel controlPanel = new JPanel();
        controlPanel.add(listScroller);

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
        if (e.getSource() == btnGenerate && !listCities.isSelectionEmpty()) {
            generateEssay();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a city from the list.", "No City Selected", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Generates an essay based on the selected city and updates the text area with the response.
     */
    private void generateEssay() {
        String selectedCity = listCities.getSelectedValue();
        String prompt = "Generate an essay about the political influences of the Danube in the given city: " + selectedCity;

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
