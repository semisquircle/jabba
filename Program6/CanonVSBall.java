/*
	Program name: Program 6, Canon vs Ball Program
	Course: CMSC 3320, Technical Computing Using Java
	Group: #3
	Members:
		Shawn Gallagher - GAL82896@pennwest.edu
		Lucas Giovannelli - GIO07221@pennwest.edu
		Joshua Watson - WAT93888@pennwest.edu
*/
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class CanonVSBall implements ActionListener, WindowListener, ItemListener
{
	private int sw = 650, sh = 480;
	private Frame EditorFrame;
	private TextArea EditArea;
	private MenuBar MMB;
	private Menu FILE, TEXT;
	private Menu NEW, SIZE, FONT;
	private MenuItem FOLDER, DOCUMENT;
	private MenuItem QUIT;
	private CheckboxMenuItem S10, S14, S18;
	private CheckboxMenuItem TNR, CO;
	private int FontType = Font.PLAIN;
	private String FontStyle = "TimesNewRoman";
	private int FontSize = 14;

	public static void main(String[] args)
	{
		new CanonVSBall();
	}

	public CanonVSBall()
	{
		EditArea = new TextArea("", sw - 10, sh - 10, TextArea.SCROLLBARS_BOTH);
		EditorFrame = new Frame("Editor");
		EditorFrame.setLayout(new BorderLayout(0, 0));
		EditorFrame.setBackground(Color.lightGray);
		EditorFrame.setForeground(Color.black);
		EditorFrame.add("Center", EditArea);
		MMB = new MenuBar();
		FILE = new Menu("FILE");
		NEW = new Menu("New");
		FOLDER = NEW.add(new MenuItem("Folder", new MenuShortcut(KeyEvent.VK_F)));
		DOCUMENT = NEW.add(new MenuItem("Document", new MenuShortcut(KeyEvent.VK_D)));
		FILE.add(NEW);
		FILE.addSeparator();
		QUIT = FILE.add(new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q)));
		TEXT = new Menu("TEXT");
		SIZE = new Menu("Size");
		FONT = new Menu("Font");
		SIZE.add(S10 = new CheckboxMenuItem("10"));
		SIZE.add(S14 = new CheckboxMenuItem("14"));
		SIZE.add(S18 = new CheckboxMenuItem("18"));
		S14.setState(true);
		TEXT.add(SIZE);
		FONT.add(TNR = new CheckboxMenuItem("TimesNewRoman"));
		FONT.add(CO = new CheckboxMenuItem("Courier"));
		TNR.setState(true);
		TEXT.add(FONT);
		MMB.add(FILE);
		MMB.add(TEXT);
		DOCUMENT.addActionListener(this);
		FOLDER.addActionListener(this);
		QUIT.addActionListener(this);
		S10.addItemListener(this);
		S14.addItemListener(this);
		S18.addItemListener(this);
		TNR.addItemListener(this);
		CO.addItemListener(this);
		EditorFrame.setMenuBar(MMB);
		EditorFrame.addWindowListener(this);
		EditorFrame.setSize(sw, sh);
		EditorFrame.setResizable(true);
		EditorFrame.setVisible(true);
		EditorFrame.validate();
		setTheFont();
	}

	public void setTheFont()
	{
		FontSize = 10;
		if (S10.getState() == true) FontSize = 10;
		if (S14.getState() == true) FontSize = 14;
		if (S18.getState() == true) FontSize = 18;
		FontStyle = "TimesNewRoman";
		if (TNR.getState() == true) FontStyle = "TimesNewRoman";
		if (CO.getState() == true) FontStyle = "Courier";
		FontType = Font.PLAIN;
		EditArea.setFont(new Font(FontStyle, FontType, FontSize));
	}

	public void stop()
	{
		DOCUMENT.removeActionListener(this);
		FOLDER.removeActionListener(this);
		QUIT.removeActionListener(this);
		S10.removeItemListener(this);
		S14.removeItemListener(this);
		S18.removeItemListener(this);
		TNR.removeItemListener(this);
		CO.removeItemListener(this);
		EditorFrame.removeWindowListener(this);
		EditorFrame.dispose();
	}

	public void itemStateChanged(ItemEvent e)
	{
		CheckboxMenuItem checkbox = (CheckboxMenuItem) e.getSource();
		if (checkbox == S10 || checkbox == S14 || checkbox == S18)
		{
			S10.setState(false);
			S14.setState(false);
			S18.setState(false);
			checkbox.setState(true);
		}
		if (checkbox == TNR || checkbox == CO)
		{
			TNR.setState(false);
			CO.setState(false);
			checkbox.setState(true);
		}
		setTheFont();
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == FOLDER) EditArea.append("\nFolder\n");
		if (source == DOCUMENT) EditArea.append("\nDOCUMENT\n");
		if (source == QUIT) stop();
	}

	public void windowClosing(WindowEvent e) {
		stop();
	}
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
}

class Ballc extends Canvas
{
    Image buffer;
    Graphics g;

    private int canvasWidth;
    private int canvasHeight;

	Polygon poly;
	private float a1, a2, c2, c1;

    public Vector<Rectangle> walls = new Vector<Rectangle>();
    private Rectangle dragBox = null;

    public Ballc(Point screen)
    {
        canvasWidth = screen.x;
        canvasHeight = screen.y;
    }

    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(x, y, w, h);
        canvasWidth = w;
        canvasHeight = h;
    }

    public void reSize(Point screen)
    {
        canvasWidth = screen.x;
        canvasHeight = screen.y;
    }

    public void setDragBox(Rectangle r)
    {
        dragBox = new Rectangle(r);
    }

    public void paint(Graphics cg)
    {
        if (canvasWidth <= 0 || canvasHeight <= 0) return;

        buffer = createImage(canvasWidth, canvasHeight);
        if (g != null) g.dispose();
        g = buffer.getGraphics();

        g.setColor(Color.white);
        g.fillRect(0, 0, canvasWidth, canvasHeight);

        g.setColor(Color.blue);
        for (int i = 0; i < walls.size(); i++)
        {
            Rectangle temp = walls.elementAt(i);
            g.fillRect(temp.x, temp.y, temp.width, temp.height);
        }

        if (dragBox != null && dragBox.width > 0 && dragBox.height > 0)
        {
            g.setColor(Color.darkGray);
            g.drawRect(dragBox.x, dragBox.y, dragBox.width, dragBox.height);
        }

        drawPolygon(poly);
        fillPolygon(poly);

        cg.drawImage(buffer, 0, 0, null);
    }

	public void drawPolygon(Polygon p)
	{
	}

	public void fillPolygon(Polygon p)
	{
	}

    public void addOne(Rectangle r)
    {
        walls.addElement(new Rectangle(r));
    }
    public void removeOne(int i)
    {
        walls.removeElementAt(i);
    }
    public Rectangle getOne(int i)
    {
        return walls.elementAt(i);
    }
    public int getWallSize()
    {
        return walls.size();
    }

    public int getCanvasWidth()  { return canvasWidth; }
    public int getCanvasHeight() { return canvasHeight; }
}
