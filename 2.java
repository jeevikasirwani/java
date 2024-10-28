import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatBot extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Map<String, ArrayList<String>> dataset;
    private Random random;
    private static final String DEFAULT_RESPONSE = "I'm not sure how to respond to that.";
    private Set<String> conversationHistory;
    private static final int MAX_HISTORY = 10;
    private static final String DATASET_FILE = "chatbot_dataset.txt";

    public ChatBot() {
        super("Enhanced Java ChatBot");
        dataset = new HashMap<>();
        loadDefaultDataset();
        loadCustomDataset();
        random = new Random();
        conversationHistory = new LinkedHashSet<>();
        setupGUI();
        setupStyles();
    }

    private void setupStyles() {
        // Custom fonts and colors
        Font chatFont = new Font("Arial", Font.PLAIN, 14);
        Color backgroundColor = new Color(240, 240, 240);
        Color botMessageColor = new Color(230, 240, 255);
        Color userMessageColor = new Color(255, 240, 230);

        chatArea.setFont(chatFont);
        chatArea.setBackground(backgroundColor);
        inputField.setFont(chatFont);
    }

    private void setupGUI() {
        // Main chat panel with custom border
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Chat area with custom styling
        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Input panel with modern look
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputField = new JTextField(40);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            inputField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Add components to frame
        setLayout(new BorderLayout());
        add(mainPanel);

        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // Window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(500, 400));

        displayWelcomeMessage();
    }

    private void displayWelcomeMessage() {
        String welcomeMessage = """
            Welcome to Enhanced ChatBot!
            
            I can help you with:
            📚 General conversation
            💻 Programming help
            🎬 Movie recommendations
            😄 Jokes and entertainment
            ❓ Questions and answers
            
            Just type your message and press Enter or click Send!
            """;
        appendMessage("Bot", welcomeMessage);
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (!userInput.isEmpty()) {
            appendMessage("You", userInput);
            conversationHistory.add(userInput.toLowerCase());
            if (conversationHistory.size() > MAX_HISTORY) {
                conversationHistory.remove(conversationHistory.iterator().next());
            }

            // Simulate typing with progress indication
            sendButton.setEnabled(false);
            inputField.setEnabled(false);
            Timer timer = new Timer(800, e -> {
                String response = generateResponse(userInput);
                appendMessage("Bot", response);
                sendButton.setEnabled(true);
                inputField.setEnabled(true);
                inputField.requestFocus();
            });
            timer.setRepeats(false);
            timer.start();

            inputField.setText("");
        }
    }

    private void appendMessage(String sender, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String formattedMessage = String.format("[%s] %s: %s%n", timestamp, sender, message);
        chatArea.append(formattedMessage);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private String generateResponse(String input) {
        input = input.toLowerCase();
        
        // Check for context-aware responses
        if (shouldGenerateContextAwareResponse()) {
            String contextResponse = generateContextAwareResponse(input);
            if (contextResponse != null) {
                return contextResponse;
            }
        }

        // Enhanced matching algorithm
        double bestMatchScore = 0;
        ArrayList<String> bestResponses = null;

        for (Map.Entry<String, ArrayList<String>> entry : dataset.entrySet()) {
            double score = calculateMatchScore(input, entry.getKey());
            if (score > bestMatchScore) {
                bestMatchScore = score;
                bestResponses = entry.getValue();
            }
        }

        if (bestMatchScore > 0.5 && bestResponses != null) {
            return bestResponses.get(random.nextInt(bestResponses.size()));
        }

        return generateFallbackResponse();
    }

    private double calculateMatchScore(String input, String key) {
        String[] inputWords = input.toLowerCase().split("\\s+");
        String[] keyWords = key.toLowerCase().split("\\s+");
        
        int matches = 0;
        for (String inputWord : inputWords) {
            for (String keyWord : keyWords) {
                if (inputWord.equals(keyWord)) {
                    matches++;
                } else if (calculateLevenshteinDistance(inputWord, keyWord) <= 2) {
                    matches += 0.5;
                }
            }
        }
        
        return (double) matches / Math.max(inputWords.length, keyWords.length);
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j] + 1,     // deletion
                    dp[i][j - 1] + 1),    // insertion
                    dp[i - 1][j - 1] + cost); // substitution
            }
        }

        return dp[s1.length()][s2.length()];
    }

    private void loadDefaultDataset() {
        // Load default responses (your existing dataset)
        // ... (keep your existing dataset initialization here)
    }

    private void loadCustomDataset() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATASET_FILE))) {
            String line;
            String currentKey = null;
            ArrayList<String> currentResponses = new ArrayList<>();

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

            if (currentKey != null && !currentResponses.isEmpty()) {
                dataset.put(currentKey, currentResponses);
            }
        } catch (IOException e) {
            System.out.println("Custom dataset file not found. Using default responses only.");
        }
    }

    private boolean shouldGenerateContextAwareResponse() {
        return !conversationHistory.isEmpty();
    }

    private String generateContextAwareResponse(String input) {
        // Implement context-aware response generation based on conversation history
        return null;
    }

    private String generateFallbackResponse() {
        String[] fallbacks = {
            "I'm not quite sure about that. Could you rephrase?",
            "Interesting! Tell me more about that.",
            "I'm still learning about that topic. Could you elaborate?",
            "That's a good question! Let me think about it...",
            DEFAULT_RESPONSE
        };
        return fallbacks[random.nextInt(fallbacks.length)];
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ChatBot chatBot = new ChatBot();
            chatBot.setVisible(true);
        });
    }
}
