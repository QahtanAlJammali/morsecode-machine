/*
 * Please start the Server first before starting the MorseCodeGui! this is really important for the networking functionality.
 * 
 * */

// this is where most of the work sits. the GUI drove me crazy, and it still doesn't look prefect
// but I am wasn't very interested in making it look pretty as much as to make it actually work 
// and do the morse translation, playing the sound and sending it over to a sever to broadcast

package com.Qahtan.morsecodemachine;

import javax.swing.*; // I was wondering whether I should use swing or Javax, but I stuck with what we learned in class
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class MorseCodeGUI extends JFrame {
    private JTextArea inputArea; 				// where you write the english to be translated to morse, or the morse to be translated to english
    private JButton translateToMorseButton; 	// translates english in the input to morse in the output
    private JButton translateToEnglishButton; 	// translates morse in the innput to english in the output (but you cant send the english in the output)
    private JTextArea outputArea; 				// has the translated stuff and to be sent to the server
    private JTextArea receivedArea;				// shows stuff sent and recieved via the server
    private JButton playSoundButton;			// plays the morse in the output
    private JButton sendButton;					// sends what is in the output to the sever if it is in morse only
    private JButton playReceivedButton;			// plays the last recieved morse from the server, but will also play your sent item if it was the last item 
    private JTextField clientNameField;			// put your name so people know who is sending
    private Socket socket;						// socket to interact with the server
    private BufferedReader in;					// to read incoming from the server
    private PrintWriter out;					// to send to the sever
    private SoundManager soundManager;			// the sound manager discussed in the SoundManager.java (see more information there)
    private List<String> receivedMessages = new LinkedList<>();
    private JLabel imageLabel;					// to put an image i downloaded and put my name on it

    public MorseCodeGUI() {
        super("Morse Code Machine");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 800);
        setLayout(new BorderLayout());

        soundManager = new SoundManager(); // my sound manager to make the sounds actually play seamlessly with pre-loading 

        clientNameField = new JTextField("ClientName");
        clientNameField.setBorder(BorderFactory.createTitledBorder("Your Identifier"));

        inputArea = new JTextArea(5, 20);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Input"));

        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));

        receivedArea = new JTextArea(10, 20);
        receivedArea.setEditable(false);
        JScrollPane receivedScroll = new JScrollPane(receivedArea);
        receivedScroll.setBorder(BorderFactory.createTitledBorder("Received Morse Code"));

        translateToMorseButton = new JButton("Translate to Morse");
        translateToMorseButton.addActionListener(e -> {
            String text = inputArea.getText();
            String morseCode = MorseCodeTranslator.toMorseCode(text);
            outputArea.setText(morseCode);
        });

        translateToEnglishButton = new JButton("Translate to English");
        translateToEnglishButton.addActionListener(e -> {
            String morseCode = inputArea.getText();
            String text = MorseCodeTranslator.fromMorseCode(morseCode);
            outputArea.setText(text); // Output the English translation in the outputArea
        });

        playSoundButton = new JButton("Play Sound");
        playSoundButton.addActionListener(e -> playMorseSound(outputArea.getText()));

        sendButton = new JButton("Send Morse");
        sendButton.addActionListener(this::sendMessage);

        playReceivedButton = new JButton("Play Last Received");
        playReceivedButton.addActionListener(e -> {
            if (!receivedMessages.isEmpty()) {
                playMorseSound(receivedMessages.get(receivedMessages.size() - 1));
            }
        });

        ImageIcon imageIcon = new ImageIcon("./morse_code_machine.jpg"); // I put an image for fun. 
        imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(clientNameField, BorderLayout.NORTH);
        topPanel.add(inputScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(translateToMorseButton);
        buttonPanel.add(translateToEnglishButton);
        buttonPanel.add(playSoundButton);
        buttonPanel.add(sendButton);
        buttonPanel.add(playReceivedButton);

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(outputScroll, BorderLayout.WEST);
        add(receivedScroll, BorderLayout.EAST);
        add(imageLabel, BorderLayout.SOUTH);

        pack();
        setVisible(true);

        setupNetworking();
    }
    
    // setting up the interaction between the MorseCodeGUI and the MorseCodeServer using port 9898, and currently it is set up for working locally
    // but can also work non-locally with a tad of modification
    private void setupNetworking() {
        try {
            socket = new Socket("localhost", 9898);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        receivedMessages.add(line);
                        final String displayText = String.join("\n", receivedMessages);
                        SwingUtilities.invokeLater(() -> receivedArea.setText(displayText));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (IOException ex) {
            System.out.println("Error connecting to server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // only messages in morse code can be sent and recieved to and from the server. 
    // if you try to send something in english (or another language) it will fail and show a pop up!
    private void sendMessage(ActionEvent e) {
        String clientName = clientNameField.getText().trim();
        String morseCode = outputArea.getText();
        // Check if the outputArea text is valid morse code before sending otherwise show a pop up saying its not morse and can't be sent over
        if (morseCode.matches("[\\.\\-\\s]+")) {
            out.println(clientName + ": " + morseCode);
        } else {
        	// pop up that will let you know that you can't send since this is invalid morse code!
            JOptionPane.showMessageDialog(this, "Invalid Morse code. Cannot send.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // when playing sounds, I had ot debug for it to play it correctly. a part of it was the need to introduce a delay when playing the sound 
    // to make sure all the dots and dashes played and nothing would start prematurely.
    
    private void playMorseSound(String morseCode) {
        new Thread(() -> {
            try {
                int unitDelay = 150;
                for (int i = 0; i < morseCode.length(); i++) {
                    char ch = morseCode.charAt(i);
                    switch (ch) {
                        case '.':
                            soundManager.playSound(ch);
                            Thread.sleep(unitDelay);
                            break;
                        case '-':
                            soundManager.playSound(ch);
                            Thread.sleep(unitDelay * 3);
                            break;
                        case ' ':
                            if (i + 1 < morseCode.length() && morseCode.charAt(i + 1) == ' ') {
                                Thread.sleep(unitDelay * 7);
                                i += 2;
                            } else {
                                Thread.sleep(unitDelay * 3);
                            }
                            break;
                    }
                    if (ch != ' ') {
                        Thread.sleep(unitDelay);
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        new MorseCodeGUI();
    }
    
}