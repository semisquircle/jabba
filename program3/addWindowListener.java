import java.awt.*;
import java.awt.event.*;
import java.io*;

public class addWindowListener extends Frame implements WindowListener 
{
    private static final long serialVersionUID = 1L;
    Label Heading1 = new Label("Chip");
    Label Heading2 = new Label("Dale");

    Label Information1 = new Label();
    Label Information2 = new Label();
    Button Button1 = new Button("Click Me");
    Button Button2 = new Button("No, Click Me");

    Window()
    {
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout displ = new GridBagLayout();
        double colWeight[] = {1,3,1};
        double rowWeight[] = {1,1};
        int colWidth[] = {1,3,1};
        int rowHeight[] = {1,1};
        displ.rowHeights = rowHeight;
        displ.columnWidths = colWidth;
        displ.columnWeights = colWeight;
        displ.rowWeights = rowWeight;
        this.setBounds(20,20,800,200);
        this.setLayout(displ);
        c.anchor = GridBagConstraints.CENTER;
        c.weightx =1;
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        displ.setConstraints(Heading1, c);
        this.add(Heading1);
        c.gridx = 1;
        displ.setConstraints(Information1,c);
        this.add(Information1);
        c.gridx = 2;
        displ.setConstraints(Button1, c);
        this.add(Button1);

        c.gridx = 0;
        c.gridy = 1;
        displ.setConstraints(Heading2, c);
        this.add(Heading2);
        c.gridx = 1;
        displ.setConstraints(Information2,c);
        this.add(Information2);
        c.gridx = 2;
        displ.setConstraints(Button2, c);
        this.add(Button2);

        this.setVisible(true);
        Button1.addActionListener(this);
        Button2.addActionListener(this);
        this.addWindowListener(this);

    }

}

public void windowClosing(WindowEvent e)
{
    this.removeWindowListener(this);
    Button1.removeActionListener(this);
    Button2.removeActionListener(this);
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

public void actionPerformed(ActionEvent e)
{
    Object source = e.getSource();
    if (source ==Button1)
    {
        Information1.setText("It was Info 1");
        Information2.setText("");
    }
    if (source ==Button2)
    {
        Information2.setText("It was Info 2");
        Information1.setText("");

    }
}
public static void main(String[] args)
{
    new Window();
}
