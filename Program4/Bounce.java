/*
	Program name: Program 4, Bounce Program
	Course: CMSC 3320, Technical Computing Using Java
	Group: #3
	Members:
		Shawn Gallagher - GAL82896@pennwest.edu
		Lucas Giovannelli - GIO07221@pennwest.edu
		Joshua Watson - WAT93888@pennwest.edu
*/
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Bounce extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable
{
    private static final long serialVersionUID = 10L;

    // Window constants
    private final int WIDTH = 640;
    private final int HEIGHT = 400;
    private final int BUTTONH = 20;
    private final int BUTTONHS = 20;

    // Scrollbar constants
    private final int MAXObj = 100;
    private final int MINObj = 10;
    private final int SPEED = 50;
    private final int SBvisible = 10;
    private final int SBunit = 1;
    private final int SBblock = 10;
    private final int SCROLLBARH = BUTTONH;
    private final int SOBJ = 21;

    // Window variables
    private int WinWidth = WIDTH;
    private int WinHeight = HEIGHT;
    private int ScreenWidth;
    private int ScreenHeight;

    private int WinTop = 10;
    private int WinLeft = 10;

    private int BUTTONW = 50;
    private int CENTER;
    private int BUTTONS;

    private Insets i;

    // Buttons
    private Button Start, Shape, Clear, Tail, Quit;

    // Scrollbar variables
    private int SObj = SOBJ;
    private int SpeedSBmin = 1;
    private int SpeedSBmax = 100 + SBvisible;
    private int SpeedSBinit = SPEED;
    private int ScrollBarW;

    // Objects
    private Objc Obj;

    private Label SPEEDL = new Label("Speed", Label.CENTER);
    private Label SIZEL = new Label("Size", Label.CENTER);

    private Scrollbar SpeedScrollBar;
    private Scrollbar ObjSizeScrollBar;

    // Thread variables
    private double delay;
    private Thread thethread;
    private boolean isTimePaused;
    private boolean isStarted;

    private int dx = 2;
    private int dy = 2;

    public static void main(String[] args)
    {
        new Bounce();
    }

    public Bounce()
    {
        setLayout(null);
        setVisible(true);

        makeSheet();

        try
        {
            initComponents();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        sizeScreen();
    }

    private void makeSheet()
    {
        i = getInsets();

        ScreenWidth = WinWidth - i.left - i.right;
        ScreenHeight = WinHeight - i.top - i.bottom - 2 * (BUTTONH + BUTTONHS);

        setSize(WinWidth, WinHeight);

        CENTER = ScreenWidth / 2;
        BUTTONW = ScreenWidth / 11;
        BUTTONS = BUTTONW / 4;

        ScrollBarW = 2 * BUTTONW;

        setBackground(Color.lightGray);
    }

    public void initComponents() throws Exception, IOException
    {
        Start = new Button("Run");
        Shape = new Button("Circle");
        Clear = new Button("Clear");
        Tail = new Button("No Tail");
        Quit = new Button("Quit");

        add(Start);
        add(Shape);
        add(Tail);
        add(Clear);
        add(Quit);

        Start.addActionListener(this);
        Shape.addActionListener(this);
        Tail.addActionListener(this);
        Clear.addActionListener(this);
        Quit.addActionListener(this);

        // Scrollbars
        SpeedScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
        ObjSizeScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);

        SpeedScrollBar.setMaximum(SpeedSBmax);
        SpeedScrollBar.setMinimum(SpeedSBmin);
        SpeedScrollBar.setUnitIncrement(SBunit);
        SpeedScrollBar.setBlockIncrement(SBblock);
        SpeedScrollBar.setValue(SpeedSBinit);
        SpeedScrollBar.setVisibleAmount(SBvisible);
        SpeedScrollBar.setBackground(Color.gray);

        ObjSizeScrollBar.setMaximum(MAXObj);
        ObjSizeScrollBar.setMinimum(MINObj);
        ObjSizeScrollBar.setUnitIncrement(SBunit);
        ObjSizeScrollBar.setBlockIncrement(SBblock);
        ObjSizeScrollBar.setValue(SOBJ);
        ObjSizeScrollBar.setVisibleAmount(SBvisible);
        ObjSizeScrollBar.setBackground(Color.gray);

        add(SpeedScrollBar);
        add(ObjSizeScrollBar);
        add(SPEEDL);
        add(SIZEL);

        SpeedScrollBar.addAdjustmentListener(this);
        ObjSizeScrollBar.addAdjustmentListener(this);

        Obj = new Objc(SObj, ScreenWidth, ScreenHeight);
        Obj.setBackground(Color.white);

        add(Obj);

        addComponentListener(this);
        addWindowListener(this);

        setBounds(WinLeft, WinTop, WIDTH, HEIGHT);
        validate();

        // Thread
        isTimePaused = true;
        delay = 1000.0 / SpeedScrollBar.getValue();
        start();
    }

    // Thread methods
    private void start()
    {
        isStarted = true;
        if (thethread == null)
        {
            thethread = new Thread();
            thethread.start();
        }
    }

    @Override
    public void run()
    {
        isStarted = true;
        while (!isTimePaused)
        {
            try
            {
                Thread.sleep((long) delay);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }

            Obj.x += dx;
            Obj.y += dy;

            // check for collisions with screen boundaries
            if (Obj.x <= 0 || Obj.x >= ScreenWidth - SObj) {
                dx = -dx;
            }
            if (Obj.y <= 0 || Obj.y >= ScreenHeight - SObj) {
                dy = -dy;
            }

            Obj.repaint();
        }
    }

    private void stop()
    {
        isTimePaused = true;
        thethread.interrupt();
    }

    private void sizeScreen()
    {
        Start.setLocation(CENTER - 2 * (BUTTONW + BUTTONS) - BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);
        Shape.setLocation(CENTER - BUTTONW - BUTTONS - BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);
        Tail.setLocation(CENTER - BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);
        Clear.setLocation(CENTER + BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);
        Quit.setLocation(CENTER + BUTTONW + 2 * BUTTONS + BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);

        Start.setSize(BUTTONW, BUTTONH);
        Shape.setSize(BUTTONW, BUTTONH);
        Tail.setSize(BUTTONW, BUTTONH);
        Clear.setSize(BUTTONW, BUTTONH);
        Quit.setSize(BUTTONW, BUTTONH);

        SpeedScrollBar.setLocation(i.left + BUTTONS, ScreenHeight + BUTTONHS + i.top);
        ObjSizeScrollBar.setLocation(WinWidth - ScrollBarW - i.right - BUTTONS, ScreenHeight + BUTTONHS + i.top);

        SPEEDL.setLocation(i.left + BUTTONS, ScreenHeight + BUTTONHS + BUTTONH + i.top);
        SIZEL.setLocation(WinWidth - ScrollBarW - i.right, ScreenHeight + BUTTONHS + BUTTONH + i.top);

        SpeedScrollBar.setSize(ScrollBarW, SCROLLBARH);
        ObjSizeScrollBar.setSize(ScrollBarW, SCROLLBARH);

        SPEEDL.setSize(ScrollBarW, BUTTONH);
        SIZEL.setSize(ScrollBarW, BUTTONH);

        Obj.setBounds(i.left, i.top, ScreenWidth, ScreenHeight);
    }

    public void windowClosing(WindowEvent e)
    {
        dispose();
        System.exit(0);
    }

    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}

    public void componentResized(ComponentEvent e)
    {
        WinWidth = getWidth();
        WinHeight = getHeight();

        makeSheet();
        sizeScreen();

        Obj.reSize(ScreenWidth, ScreenHeight);
    }

    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if (source == Start)
        {
            if (Start.getLabel().equals("Pause"))
            {
                Start.setLabel("Run");
                isTimePaused = true;
                stop();
            }
            else
            {
                Start.setLabel("Pause");
                isTimePaused = false;
                run();
            }
        }

        if (source == Shape)
        {
            if (Shape.getLabel().equals("Circle"))
            {
                Shape.setLabel("Square");
                Obj.rectangle(false);
            }
            else
            {
                Shape.setLabel("Circle");
                Obj.rectangle(true);
            }

            if (!isStarted) Obj.clear();
            Obj.repaint();
        }

        if (source == Tail)
        {
            if (Tail.getLabel().equals("No Tail"))
            {
                Tail.setLabel("Yes Tail");
            }
            else
            {
                Tail.setLabel("No Tail");
            }
        }

        if (source == Clear)
        {
            Obj.clear();
            Obj.repaint();
        }

        if (source == Quit)
        {
            System.exit(0);
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        int TS;
        Scrollbar sb = (Scrollbar) e.getSource();

        if (sb == ObjSizeScrollBar)
        {
            TS = e.getValue();
            TS = (TS / 2) * 2 + 1;
            Obj.updateSize(TS);
        }
        else
        {
            delay = 1000.0 / SpeedScrollBar.getValue();
        }

        Obj.repaint();
    }
}

class Objc extends Canvas
{
    private static final long serialVersionUID = 11L;

    private int ScreenWidth;
    private int ScreenHeight;
    private int SObj;

    public int x, y;

    private boolean rect = true;
    private boolean clear = false;

    public Objc(int SB, int w, int h)
    {
        ScreenWidth = w;
        ScreenHeight = h;
        SObj = SB;
        x = ScreenWidth / 2;
        y = ScreenHeight / 2;
    }

    public void rectangle(boolean r)
    {
        rect = r;
    }

    public void updateSize(int NS)
    {
        SObj = NS;
    }

    public void reSize(int w, int h)
    {
        ScreenWidth = w;
        ScreenHeight = h;
        x = ScreenWidth / 2;
        y = ScreenHeight / 2;
    }

    public void clear()
    {
        clear = true;
    }

    public void paint(Graphics g)
    {
        g.setColor(Color.red);
        g.drawRect(0, 0, ScreenWidth - 1, ScreenHeight - 1);
        update(g);
    }

    public void update(Graphics g)
    {
        if (clear)
        {
            super.paint(g);
            clear = false;
            g.setColor(Color.red);
            g.drawRect(0, 0, ScreenWidth - 1, ScreenHeight - 1);
        }

        if (rect)
        {
            g.setColor(Color.lightGray);
            g.fillRect(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj);
            g.setColor(Color.black);
            g.drawRect(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj - 1, SObj - 1);

        }
        else
        {
            g.setColor(Color.lightGray);
            g.fillOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj);
            g.setColor(Color.black);
            g.drawOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj - 1, SObj - 1);
        }
    }
}
