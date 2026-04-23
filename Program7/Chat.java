/*
    Program name: Program 6, Chat Application Program
    Course: CMSC 3320, Technical Computing Using Java
    Group: #3
    Members:
        Shawn Gallagher - GAL82896@pennwest.edu
        Lucas Giovannelli - GIO07221@pennwest.edu
        Joshua Watson - WAT93888@pennwest.edu
*/
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Chat extends Frame implements ActionListener, Runnable {

    // GUI Components
    private TextArea chatArea, statusArea;      
    private TextField messageField, serverPortField, hostField, clientPortField;
    private Button changeHostButton, changeClientButton, sendButton, serverButton, connectButton, closeButton;

    // Networking
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Thread
    private Thread thread;

    // Mode flags
    private boolean isServer = false;
    private boolean connected = false;

    public Chat() {
        setTitle("Chat Application");
        setSize(600, 500);
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbl);
        gbl.columnWeights = new double[] {0.5, 1.0, 0.5, 0.5};

        // Chat Area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(chatArea, gbc);

        // Host Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        add(new Label("Host:"), gbc);

        // Port Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(new Label("Server Port:"), gbc);

        // Client Label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(new Label("Client Port:"), gbc);

        // Message Field
        messageField = new TextField();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        add(messageField, gbc);

        // Host Field
        hostField = new TextField("localhost");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(hostField, gbc);

        // Server Field
        serverPortField = new TextField("44004");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(serverPortField, gbc);

        // Client Field
        clientPortField = new TextField("44004");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(clientPortField, gbc);

        // Change Host Button
        changeHostButton = new Button("Change Host");
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(changeHostButton, gbc);

        // Change Client Button
        changeClientButton = new Button("Change Port");
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(changeClientButton, gbc);

        // Send Button
        sendButton = new Button("Send");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(sendButton, gbc);

        // Server Button
        serverButton = new Button("Start Server");
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(serverButton, gbc);

        // Connect Button
        connectButton = new Button("Connect");
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(connectButton, gbc);

        // Close Button
        closeButton = new Button("Close");
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(closeButton, gbc);

        // Status Area
        statusArea = new TextArea(3, 50);
        statusArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        add(statusArea, gbc);

        // Add listeners
        sendButton.addActionListener(this);
        serverButton.addActionListener(this);
        connectButton.addActionListener(this);
        closeButton.addActionListener(this);

        messageField.addActionListener(this); // Enter key sends

        // Initial state
        sendButton.setEnabled(false);
        closeButton.setEnabled(false);

        // Window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeConnection();
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == serverButton) {
            startServer();
        } else if (source == connectButton) {
            startClient();
        } else if (source == sendButton || source == messageField) {
            sendMessage();
        } else if (source == closeButton) {
            closeConnection();
            reset();
        }
    }

    private void startServer() {
        try {
            int port = Integer.parseInt(serverPortField.getText());
            serverSocket = new ServerSocket(port);
            isServer = true;

            statusArea.append("Server waiting on port " + port + "\n");

            new Thread(() -> {
                try 
                {
                    socket = serverSocket.accept();
                    statusArea.append("Connection made with client\n");
                    setupStreams();
                } catch (IOException ex) 
                {
                    statusArea.append("Server error\n");
                }
}).start();

        } catch (IOException ex) {
            statusArea.append("Server error\n");
        }
    }

    private void startClient() {
        try {
            String host = hostField.getText();
            int port = Integer.parseInt(clientPortField.getText());

            statusArea.append("Requesting connection to " + host + "\n");

            socket = new Socket(host, port);
            isServer = false;

            statusArea.append("Connected to server\n");

            setupStreams();

        } catch (IOException ex) {
            statusArea.append("Client error\n");
        }
    }

    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        connected = true;

        sendButton.setEnabled(true);
        closeButton.setEnabled(true);
        serverButton.setEnabled(false);
        connectButton.setEnabled(false);

        thread = new Thread(this);
        thread.start();
    }

    private void sendMessage() {
        if (connected) {
            String msg = messageField.getText();
            out.println(msg);

            if (isServer) {
                chatArea.append("Server: " + msg + "\n");
            } else {
                chatArea.append("Client: " + msg + "\n");
            }

            messageField.setText("");
        }
    }

    public void run() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {

                if (isServer) {
                    chatArea.append("Client: " + msg + "\n");
                } else {
                    chatArea.append("Server: " + msg + "\n");
                }
            }
        } catch (IOException e) {
            statusArea.append("Connection closed\n");
        }
    }

    private void closeConnection() {
        try {
            connected = false;

            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();

            statusArea.append("Connection closed\n");

        } catch (IOException e) {
            statusArea.append("Error closing connection\n");
        }
    }

    private void reset() {
        sendButton.setEnabled(false);
        closeButton.setEnabled(false);
        serverButton.setEnabled(true);
        connectButton.setEnabled(true);

        chatArea.setText("");
        statusArea.append("Reset complete\n");
    }

    public static void main(String[] args) {
        new Chat();
    }
}
