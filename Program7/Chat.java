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

public class Chat extends Frame implements ActionListener, WindowListener, Runnable
{
    // GUI Components
    private TextArea chatArea, statusArea;      
    private TextField messageField, serverPortField, hostField;
    private Button changeHostButton, changePortButton, sendButton, startServerButton, connectButton, disconnectButton;

    // Networking
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String host;
    private int port;

    // Thread
    private Thread thread;

    // Connection flags
    private boolean isServer = false;
    private boolean connected = false;

    public Chat()
    {
        setTitle("Chat");
        setSize(640, 480);
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
        add(new Label("Port:"), gbc);

        // Message Field
        messageField = new TextField();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        add(messageField, gbc);

        // Host Field
        hostField = new TextField();
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

        // Change Host Button
        changeHostButton = new Button("Change Host");
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(changeHostButton, gbc);

        // Change Port Button
        changePortButton = new Button("Change Port");
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(changePortButton, gbc);

        // Send Button
        sendButton = new Button("Send");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(sendButton, gbc);

        // Server Button
        startServerButton = new Button("Start Server");
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(startServerButton, gbc);

        // Connect Button
        connectButton = new Button("Connect");
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(connectButton, gbc);

        // Close Button
        disconnectButton = new Button("Disconnect");
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(disconnectButton, gbc);

        // Status Area
        statusArea = new TextArea(3, 50);
        statusArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        add(statusArea, gbc);

        // Add listeners
        changeHostButton.addActionListener(this);
        changePortButton.addActionListener(this);
        sendButton.addActionListener(this);
        startServerButton.addActionListener(this);
        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
        messageField.addActionListener(this);

        // Initial state
        sendButton.setEnabled(false);
        disconnectButton.setEnabled(false);
        hostField.requestFocus();

        setVisible(true);
        addWindowListener(this);
    }

    public static void main(String[] args)
    {
        new Chat();
    }

    // Window listeners
    public void windowClosing(WindowEvent e)
    {
        disconnect();
        dispose();
        System.exit(0);
    }
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}

    // Action listener
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == sendButton || source == messageField) // triggers on Enter keypress
        {
            sendMessage();
        }
        else if (source == startServerButton)
        {
            startServer();
            startServerButton.setEnabled(false);
            connectButton.setEnabled(false);
        }
        else if (source == connectButton)
        {
            startClient();
            startServerButton.setEnabled(false);
            connectButton.setEnabled(false);
        }
        else if (source == disconnectButton)
        {
            disconnect();
            reset();
        }
        else if (source == changeHostButton)
        {
            disconnect();
            if (connected)
            {
                if (isServer) startServer();
                else startClient();
            }
        }
        else if (source == changePortButton)
        {
            disconnect();
            if (connected)
            {
                if (isServer) startServer();
                else startClient();
            }
        }
    }

    // Connection setup for server mode
    private void startServer()
    {
        try
        {
            port = Integer.parseInt(serverPortField.getText());

            serverSocket = new ServerSocket(port);
            isServer = true;
            statusArea.append("Server: listening on port " + port + "\n");

            new Thread(() -> {
                try 
                {
                    socket = serverSocket.accept();
                    setupStreams();

                    statusArea.append("Server: connection from " + port + "\n");
                    setTitle("Server: connection from " + port);
                }
                catch (IOException e) 
                {
                    statusArea.append("Server error\n");
                }
            }).start();
        }
        catch (IOException e)
        {
            statusArea.append("Server error\n");
        }
    }

    // Connection setup for client mode
    private void startClient()
    {
        try
        {
            host = hostField.getText();
            port = Integer.parseInt(serverPortField.getText());

            statusArea.append("Connecting to " + host + ":" + port + "\n");

            socket = new Socket(host, port);
            isServer = false;
            setupStreams();

            statusArea.append("Client: connected to " + host + " at port " + port + "\n");
            setTitle("Client: connected to " + host + " at port " + port);
        }
        catch (IOException e)
        {
            statusArea.append("Client error\n");
        }
    }

    // Network/thread initialization upon start
    private void setupStreams() throws IOException
    {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        thread = new Thread(this);
        thread.start();

        connected = true;

        sendButton.setEnabled(true);
        disconnectButton.setEnabled(true);
        startServerButton.setEnabled(false);
        connectButton.setEnabled(false);
    }

    // Add a message to the chat area
    private void sendMessage()
    {
        if (connected)
        {
            String msg = messageField.getText();
            out.println(msg);

            if (isServer)
                chatArea.append("Server: " + msg + "\n");
            else
                chatArea.append("Client: " + msg + "\n");

            messageField.setText("");
        }
    }

    public void run()
    {
        try
        {
            String msg;
            while ((msg = in.readLine()) != null)
            {
                if (isServer)
                    chatArea.append("Client: " + msg + "\n");
                else
                    chatArea.append("Server: " + msg + "\n");
            }
        }
        catch (IOException e)
        {
            statusArea.append("Connection closed\n");
        }
    }

    private void disconnect()
    {
        try
        {
            connected = false;
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
            statusArea.append("Connection closed\n");
        }
        catch (IOException e)
        {
            statusArea.append("Error closing connection\n");
        }
    }

    private void reset()
    {
        setTitle("Chat");
        chatArea.setText("");
        sendButton.setEnabled(false);
        startServerButton.setEnabled(true);
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        // statusArea.append("Reset complete\n");
    }
}
