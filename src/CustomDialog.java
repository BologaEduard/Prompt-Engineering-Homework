import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * CustomDialog extends JDialog and provides a user interface for generating and managing essays about the Danube.
 * Users can select the length of the essay, view the generated essay, save it to a file, or navigate back to the main menu.
 */
public class CustomDialog extends JDialog implements ActionListener {
    private JTextField txtField;
    private JButton btnSubmit, btnSave;
    private JTextArea txtResponse;
    private JButton btnShort, btnMedium, btnLong;
    private JButton btnBack;
    private String essayLength = "";
    private int maxTokens = 40;
    private String API_KEY = "";

    /**
     * Constructs a CustomDialog which sets up the user interface for essay interaction.
     *
     * @param parent The parent frame to which this dialog is attached.
     */
    public CustomDialog(Frame parent) {
        super(parent, "Custom Prompt", true);
        setSize(800, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        setContentPane(new BackgroundPanel()); // Specify the path to your background image
        getContentPane().setLayout(new BorderLayout(10, 10));

        txtField = new JTextField("I want you to write an essay about the Danube, in this topic: ", 30); // Increase the size for better visual
        txtField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Add padding around the text field
        getContentPane().add(txtField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnShort = new JButton("Short");
        btnMedium = new JButton("Medium");
        btnLong = new JButton("Long");
        btnSubmit = new JButton("Submit");
        btnBack = new JButton("Back");
        btnSave = new JButton("Save");
        btnSubmit.setEnabled(false);
        buttonPanel.add(btnShort);
        buttonPanel.add(btnMedium);
        buttonPanel.add(btnLong);
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnSave);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveEssay());
        btnShort.addActionListener(e -> setEssayLength("Short"));
        btnMedium.addActionListener(e -> setEssayLength("Medium"));
        btnLong.addActionListener(e -> setEssayLength("Long"));
        btnSubmit.addActionListener(this);
        btnBack.addActionListener(e -> {
            dispose(); // Closes the dialog
            parent.setVisible(true); // Assuming 'parent' is your main menu, make sure it's visible again
        });

        txtResponse = new JTextArea();
        txtResponse.setLineWrap(true);
        txtResponse.setWrapStyleWord(true);
        txtResponse.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtResponse);
        scrollPane.setBorder(null); // Remove border to blend with the background
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Handles button click events to trigger essay generation or management functions.
     *
     * @param e the event triggered by button clicks
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            String topic = txtField.getText().replace("I want you to write an essay about the Danube, in this topic: ", "");
            String prompt = "I want you to write an essay about the Danube, in this topic: " + topic;
            fetchEssay(prompt);
        }
    }

    /**
     * Sets the essay length and updates the user interface to reflect the selected length.
     *
     * @param length The desired length of the essay: Short, Medium, or Long.
     */
    private void setEssayLength(String length) {
        essayLength = length;
        switch (length) {
            case "Short":
                maxTokens = 1; // Approximate range for 1-3 sentences
                break;
            case "Medium":
                maxTokens = 50; // Approximate range for 3-5 sentences
                break;
            case "Long":
                maxTokens = 200; // More than 5 sentences
                break;
        }
        btnSubmit.setEnabled(true); // Enable the submit button once a length is selected
    }

    /**
     * Fetches an essay based on user inputs and displays it in the text area.
     *
     * @param prompt The prompt to send to the essay generation API.
     */
    private void fetchEssay(String prompt) {
        Thread thread = new Thread(() -> {
            try {
                String fullPrompt = prompt + " Please keep the essay length: " + essayLength + ".";  // Append the selected essay length to the prompt
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + API_KEY)
                        .POST(BodyPublishers.ofString("{\"model\": \"gpt-3.5-turbo\", \"messages\":[{\"role\":\"user\",\"content\":\"" + fullPrompt + "\"}]}"))
                        .build();
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                // Parse the JSON response
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);
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

    /**
     * Saves the currently displayed essay to a file chosen by the user.
     */
    private void saveEssay() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        File directory = new File("./essays");
        if (!directory.exists()) {
            directory.mkdirs();  // Make the directory if it does not exist
        }
        fileChooser.setCurrentDirectory(directory);
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter fileWriter = new FileWriter(fileToSave)) {
                fileWriter.write(txtResponse.getText());
                JOptionPane.showMessageDialog(this, "Essay saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving the file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
