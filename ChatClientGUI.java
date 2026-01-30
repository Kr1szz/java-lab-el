import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClientGUI extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    // UI Components
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton connectButton;
    private JTextField nameField;
    private JLabel statusLabel;
    private JPanel onlineUsersPanel;

    // Network components
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;

    public ChatClientGUI() {
        setTitle("💬 Java Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(500, 400));

        initializeUI();
        applyStyles();
    }

    private void initializeUI() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(30, 30, 40));

        // === Top Panel (Connection) ===
        JPanel topPanel = createConnectionPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // === Center Panel (Messages) ===
        JPanel centerPanel = createMessagePanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // === Bottom Panel (Input) ===
        JPanel bottomPanel = createInputPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 130, 180), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // Left side - Name input
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setForeground(new Color(200, 200, 220));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        nameField = new JTextField(15);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setBackground(new Color(50, 50, 70));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));

        leftPanel.add(nameLabel);
        leftPanel.add(nameField);

        // Right side - Connect button and status
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        statusLabel = new JLabel("⚫ Disconnected");
        statusLabel.setForeground(new Color(200, 100, 100));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        connectButton = new JButton("🔗 Connect");
        styleButton(connectButton, new Color(46, 204, 113), Color.WHITE);
        connectButton.addActionListener(e -> toggleConnection());

        rightPanel.add(statusLabel);
        rightPanel.add(connectButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(35, 35, 50));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 80), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Header
        JLabel headerLabel = new JLabel("  📨 Messages");
        headerLabel.setForeground(new Color(150, 180, 220));
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(45, 45, 65));
        headerLabel.setBorder(new EmptyBorder(8, 10, 8, 10));

        // Message area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        messageArea.setBackground(new Color(25, 25, 35));
        messageArea.setForeground(new Color(220, 220, 230));
        messageArea.setCaretColor(Color.WHITE);
        messageArea.setMargin(new Insets(10, 10, 10, 10));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setBackground(new Color(40, 40, 55));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 130, 180), 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));

        // Input field
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBackground(new Color(50, 50, 70));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        inputField.setEnabled(false);
        inputField.addActionListener(e -> sendMessage());

        // Send button
        sendButton = new JButton("📤 Send");
        styleButton(sendButton, new Color(52, 152, 219), Color.WHITE);
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> sendMessage());

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 20, 8, 20));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private void applyStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
    }

    private void toggleConnection() {
        if (!isConnected) {
            connect();
        } else {
            disconnect();
        }
    }

    private void connect() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your name before connecting!", 
                "Name Required", 
                JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }

        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read the "Enter your name" prompt and send name
            in.readLine(); // Read prompt
            out.println(name);

            isConnected = true;
            updateUIState(true);
            appendMessage("✅ Connected to server as " + name);

            // Start receiving messages
            new Thread(this::receiveMessages).start();

        } catch (IOException e) {
            appendMessage("❌ Failed to connect: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Could not connect to server at " + SERVER_ADDRESS + ":" + SERVER_PORT,
                "Connection Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnect() {
        try {
            if (out != null) {
                out.println("exit");
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isConnected = false;
            updateUIState(false);
            appendMessage("🔌 Disconnected from server");
        }
    }

    private void updateUIState(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            inputField.setEnabled(connected);
            sendButton.setEnabled(connected);
            nameField.setEnabled(!connected);
            connectButton.setText(connected ? "🔌 Disconnect" : "🔗 Connect");
            connectButton.setBackground(connected ? new Color(231, 76, 60) : new Color(46, 204, 113));
            statusLabel.setText(connected ? "🟢 Connected" : "⚫ Disconnected");
            statusLabel.setForeground(connected ? new Color(100, 200, 100) : new Color(200, 100, 100));
            
            if (connected) {
                inputField.requestFocus();
            }
        });
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && isConnected) {
            out.println(message);
            appendMessage("You: " + message);
            inputField.setText("");
            
            if ("exit".equalsIgnoreCase(message)) {
                disconnect();
            }
        }
    }

    private void receiveMessages() {
        try {
            String serverMessage;
            while (isConnected && (serverMessage = in.readLine()) != null) {
                final String msg = serverMessage;
                SwingUtilities.invokeLater(() -> appendMessage(msg));
            }
        } catch (IOException e) {
            if (isConnected) {
                SwingUtilities.invokeLater(() -> {
                    appendMessage("⚠️ Connection lost: " + e.getMessage());
                    disconnect();
                });
            }
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(getTimestamp() + " " + message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    private String getTimestamp() {
        return "[" + java.time.LocalTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }

    // Custom scrollbar UI for modern look
    static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(80, 80, 110);
            this.trackColor = new Color(40, 40, 55);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 8, 8);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClientGUI client = new ChatClientGUI();
            client.setVisible(true);
        });
    }
}
