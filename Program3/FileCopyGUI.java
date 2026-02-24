/*
	Program name: Program 3, GUI File Copy Program
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

public class FileCopyGUI
{
    private static final long serialVersionUID = 1L;
    
    public static void main(String[] args)
    {
        String startPath;

        if (args.length > 0) {
            startPath = args[0];
        } else {
            startPath = System.getProperty("user.dir");
        }

        new Window(startPath);
    }
}

class Window extends Frame implements ActionListener, WindowListener,ItemListener
{
    private List directoryList;
    private Label sourceLabelTitle;
    private Label sourceFileLabel;
    private Button targetButton;
    private Label targetPathLabel;
    private Label fileNameLabel;
    private TextField targetFileField;
    private Button okButton;
    private Label messageLabel;

    private File curDir;
    private File sourceFile;
    private File targetDir;
    private boolean targetMode;
    private boolean copyLocked;

    public Window(String startPath)
    {
        File startDir = new File(startPath);
        if (startDir.exists() && startDir.isDirectory()) {
            curDir = startDir;
        } else {
            curDir = new File(System.getProperty("user.dir"));
        }

        targetMode = false;
        copyLocked = false;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        directoryList = new List(15);
        directoryList.addItemListener(this);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(directoryList, gbc);

        gbc.weighty = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        sourceLabelTitle = new Label("Source:");
        gbc.gridx = 0;
        add(sourceLabelTitle, gbc);

        sourceFileLabel = new Label("");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(sourceFileLabel, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;

        targetButton = new Button("Target");
        targetButton.setEnabled(false);
        targetButton.addActionListener(this);

        gbc.gridx = 0;
        add(targetButton, gbc);

        targetPathLabel = new Label("");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(targetPathLabel, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;

        fileNameLabel = new Label("File Name:");
        gbc.gridx = 0;
        add(fileNameLabel, gbc);

        targetFileField = new TextField(20);
        targetFileField.setEnabled(false);
        targetFileField.addActionListener(this);
        // Enable the OK button as the user types
        targetFileField.addTextListener(new TextListener() {
            public void textValueChanged(TextEvent e) {
                checkEnableOk();
            }
        }); 
        gbc.gridx = 1;
        add(targetFileField, gbc);

        okButton = new Button("Ok");
        okButton.setEnabled(false);
        okButton.addActionListener(this);
        gbc.gridx = 2;
        add(okButton, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;

        messageLabel = new Label("");
        add(messageLabel, gbc);

        setSize(600, 500);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        updateDirectoryList();
        setVisible(true);
    }

    private void updateDirectoryList()
    {
        directoryList.removeAll();
        setTitle(curDir.getAbsolutePath());

        File parent = curDir.getParentFile();

        if (parent != null) {
            directoryList.add("..");
        }

        File[] files = curDir.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];

                if (f.isDirectory()) {
                    boolean hasChildDirectory = false;
                    File[] sub = f.listFiles();
                    if (sub != null) {
                        for (int j = 0; j < sub.length; j++) {
                            if (sub[j].isDirectory()) {
                                hasChildDirectory = true;
                            }
                        }
                    }

                    if (hasChildDirectory) {
                        directoryList.add(f.getName() + "+");
                    } else {
                        directoryList.add(f.getName());
                    }
                } else {
                    directoryList.add(f.getName());
                }
            }
        }
    }

    public void itemStateChanged(ItemEvent e)
    {
        if (copyLocked) {
            return;
        }

        messageLabel.setText("");

        String selected = directoryList.getSelectedItem();
        File selectedFile;

        if (selected.equals("..")) {
            curDir = curDir.getParentFile();
            updateDirectoryList();
        } else {
            String cleanName = selected.replace("+", "");
            selectedFile = new File(curDir, cleanName);

            if (selectedFile.isDirectory()) {
                curDir = selectedFile;
                updateDirectoryList();
            } else {
                if (targetDir != null) {
                    targetFileField.setText(selectedFile.getName());
                    checkEnableOk();
                } else {
                    sourceFile = selectedFile;
                    sourceFileLabel.setText(sourceFile.getName());
                    targetButton.setEnabled(true);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        messageLabel.setText("");

        if (e.getSource() == targetButton) {
            targetMode = true;
            targetDir = curDir;
            targetPathLabel.setText(curDir.getAbsolutePath());
            targetFileField.setEnabled(true);
        } else if (e.getSource() == okButton || e.getSource() == targetFileField) {
            performCopy();
        }
    }

    private void checkEnableOk()
    {
        if (targetFileField.getText().length() > 0) {
            okButton.setEnabled(true);
        }
    }

    private void performCopy()
    {
        copyLocked = true;//Immediately lock navigation when run
        if (sourceFile == null) {
            messageLabel.setText("Source file not specified.");
            return;
        }

        if (targetDir == null) {
            messageLabel.setText("Target directory not specified.");
            return;
        }

        if (targetFileField.getText().length() == 0) {
            messageLabel.setText("Target file not specified.");
            return;
        }

        File targetFile = new File(targetDir, targetFileField.getText());

        // Overwrite warning
        if (targetFile.exists()) {
            messageLabel.setText("Output file " + targetFile.getName() + " exists. It will be overwritten.");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
            PrintWriter writer = new PrintWriter(targetFile);

            int data = reader.read();

            while (data != -1) {
                writer.write(data);
                data = reader.read();
            }

            reader.close();
            writer.close();

            messageLabel.setText("File Copied");

            resetProgramState();
        } catch (FileNotFoundException ex) {
            messageLabel.setText("Error Opening File " + sourceFile.getName());
            copyLocked = false;
        } catch (IOException ex) {
            // Catches any other IO failures during r/w
            messageLabel.setText("An IO Error occurred, terminating.");
            copyLocked = false;
        }
    }

    private void resetProgramState()
    {
        sourceFile = null;
        targetDir = null;
        targetMode = false;

        sourceFileLabel.setText("");
        targetPathLabel.setText("");
        targetFileField.setText("");

        targetFileField.setEnabled(false);
        targetButton.setEnabled(false);
        okButton.setEnabled(false);

        copyLocked = false;
    }

    public void windowClosing(WindowEvent e)
    {
        this.removeWindowListener(this);
        this.dispose();
    }
    public void windowClosed(WindowEvent e)
    {}
    public void windowOpened(WindowEvent e)
    {}
    public void windowActivated(WindowEvent e)
    {}
    public void windowDeactivated(WindowEvent e)
    {}
    public void windowIconified(WindowEvent e)
    {}
    public void windowDeiconified(WindowEvent e)
    {}
}
