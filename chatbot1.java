// ChatBot.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class ChatBot extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Map<String, List<String>> responses;
    
    public ChatBot() {
        responses = loadDataset("responses.txt");
        setupGUI();
    }
    
    private void setupGUI() {
        setTitle("Custom Chatbot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400, 500);
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Action listeners
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        
        // Welcome message
        appendMessage("Bot", "Hello! How can I help you today?");
    }
    
    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty()) {
            appendMessage("You", userMessage);
            String botResponse = generateResponse(userMessage);
            appendMessage("Bot", botResponse);
            inputField.setText("");
        }
    }
    
    private void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private String generateResponse(String userInput) {
        userInput = userInput.toLowerCase();
        
        // Find the best matching key from the dataset
        String bestMatch = findBestMatch(userInput);
        if (bestMatch != null && responses.containsKey(bestMatch)) {
            List<String> possibleResponses = responses.get(bestMatch);
            int index = new Random().nextInt(possibleResponses.size());
            return possibleResponses.get(index);
        }
        
        return "I'm not sure how to respond to that.";
    }
    
    private String findBestMatch(String userInput) {
        int maxScore = 0;
        String bestMatch = null;
        
        for (String key : responses.keySet()) {
            int score = calculateSimilarity(userInput, key);
            if (score > maxScore) {
                maxScore = score;
                bestMatch = key;
            }
        }
        
        return maxScore > 0 ? bestMatch : null;
    }
    
    private int calculateSimilarity(String s1, String s2) {
        // Simple word matching algorithm
        Set<String> words1 = new HashSet<>(Arrays.asList(s1.split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(s2.split("\\s+")));
        int matches = 0;
        
        for (String word : words1) {
            if (words2.contains(word)) {
                matches++;
            }
        }
        
        return matches;
    }
    
    private Map<String, List<String>> loadDataset(String filename) {
        Map<String, List<String>> dataset = new HashMap<>();
        
        // Default responses if file not found
        dataset.put("hello", Arrays.asList(
            "Hi there!",
            "Hello!",
            "Greetings!"
        ));
        
        dataset.put("how are you", Arrays.asList(
            "I'm doing well, thank you!",
            "I'm great, how are you?",
            "All good, thanks for asking!"
        ));
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String currentKey = null;
            List<String> currentResponses = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Q:")) {
                    if (currentKey != null) {
                        dataset.put(currentKey, new ArrayList<>(currentResponses));
                    }
                    currentKey = line.substring(2).trim().toLowerCase();
                    currentResponses.clear();
                } else if (line.startsWith("A:")) {
                    currentResponses.add(line.substring(2).trim());
                }
            }
            
            if (currentKey != null) {
                dataset.put(currentKey, new ArrayList<>(currentResponses));
            }
        } catch (IOException e) {
            System.out.println("Using default dataset as responses.txt not found");
        }
        
        return dataset;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatBot().setVisible(true);
        });
    }
}
