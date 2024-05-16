import org.json.JSONArray;
import org.json.JSONObject;

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

/**
 * TechnologyDialog provides a user interface to generate essays about technological advancements and improvements of the Danube,
 * with selectable style options such as formal, informal, and funny.
 */
public class TechnologyDialog extends JDialog implements ActionListener {
    private JTextArea txtResponse;
    private JButton btnGenerate;
    private JCheckBox chkFormal, chkInformal, chkFunny;
    private String API_KEY = ""; // Use your actual API key

    /**
     * Constructs a TechnologyDialog window which allows the user to select an essay style and initiate the generation process.
     *
     * @param parent The parent frame to which this dialog is attached.
     */
    public TechnologyDialog(Frame parent) {
        super(parent, "Technology Essay", true);
        setSize(800, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        chkFormal = new JCheckBox("Formal");
        chkInformal = new JCheckBox("Informal");
        chkFunny = new JCheckBox("Funny");

        // Group the checkboxes to ensure only one can be selected at a time
        ButtonGroup styleGroup = new ButtonGroup();
        styleGroup.add(chkFormal);
        styleGroup.add(chkInformal);
        styleGroup.add(chkFunny);

        controlPanel.add(chkFormal);
        controlPanel.add(chkInformal);
        controlPanel.add(chkFunny);

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
     * Responds to the Generate button click by initiating the essay generation process based on the selected style.
     *
     * @param e The action event triggered by clicking the Generate button.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGenerate) {
            generateEssay();
        }
    }

    /**
     * Generates an essay based on the selected style and updates the text area with the generated content.
     * It communicates with an external service to obtain the essay content.
     */
    private void generateEssay() {
        String style = "";
        if (chkFormal.isSelected()) style = "Formal";
        else if (chkInformal.isSelected()) style = "Informal";
        else if (chkFunny.isSelected()) style = "Funny";

        String prompt = "Generate an essay about the technological improvements and advances of the Danube. Style: " + style;

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

                // Parse the JSON response to extract the essay content
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
