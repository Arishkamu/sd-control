package org.chat.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// Create the main chat class, inherit from the window
public class ChatGUI extends JFrame {
    private final JTextArea chatArea;
    private final JTextArea inputField;
    private final JLabel channelName;
    private final JTextArea channelChangeField;

    public ChatGUI() {
        setTitle("Chat");
        setSize(650, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Top bar: channel name, shows in which channel you are
        channelName = new JLabel("Chanel: ");
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(channelName, BorderLayout.WEST);

        // Message area, can't be edited, view only, can be scrolled
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // Message input right panel + "Send" button
        inputField = new JTextArea(2, 18);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, inputField.getPreferredSize().height * 4));
        JScrollPane inputScrollPane = new JScrollPane(inputField);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton sendButton = new JButton("Send");
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JLabel("Send message:"));
        inputPanel.add(inputScrollPane);
        inputPanel.add(sendButton);

        // Channel change right panel
        channelChangeField = new JTextArea(1, 10);
        channelChangeField.setLineWrap(true);
        channelChangeField.setWrapStyleWord(true);
        channelChangeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, channelChangeField.getPreferredSize().height * 3));
        JScrollPane channelScrollPane = new JScrollPane(channelChangeField);
        channelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton switchChannelButton = new JButton("Change chanel");
        JPanel channelPanel = new JPanel();
        channelPanel.setLayout(new BoxLayout(channelPanel, BoxLayout.Y_AXIS));
        channelPanel.add(new JLabel("Add new channel:"));
        channelPanel.add(channelScrollPane);
        channelPanel.add(switchChannelButton);

        // Combine the two right panels into one vertical one
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(inputPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(channelPanel);

        // Add panels to the main window
        add(topPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Event handlers
        sendButton.addActionListener((ActionEvent _) -> sendMessage());
        switchChannelButton.addActionListener((ActionEvent _) -> switchChannel());

        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append("You: " + message + "\n");
            // clearing the field
            inputField.setText("");
            // send to RabbitMQ
        }
    }

    private void switchChannel() {
        String newChannel = channelChangeField.getText().trim();
        if (!newChannel.isEmpty()) {
            channelName.setText("Channel: " + newChannel);
            chatArea.append("Changed channel to " + newChannel + "\n");
            // clearing the field
            channelChangeField.setText("");
            // change channel to RabbitMQ
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}
