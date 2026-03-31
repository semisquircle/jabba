/*
	Program name: Program 5, Bouncing Ball Program
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
import java.util.Random;
import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.SwingUtilities;

public class BouncingBalljunk extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable
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
    private int minWidth = 300;
    private int minHeight = 300;
    private int CENTER;
    private int BUTTONW;
    private int BUTTONS;

    private Insets i;

    // Buttons
    private Button Run, Pause, Quit;

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

    // Dragging variables
    private Vector<Rectangle> rectangles = new Vector<>();

    private Point dragStart = null;
    private Point dragEnd = null;
    private boolean isDragging = false;

    public static void main(String[] args)
    {
        new BouncingBalljunk();
    }

    public BouncingBalljunk()
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
        BUTTONS = ScreenWidth / 18;
        ScrollBarW = ScreenWidth / 6;
        BUTTONW = (ScreenWidth - (4 * BUTTONS) - (2 * ScrollBarW)) / 3;

        setBackground(Color.lightGray);
    }

    public void initComponents() throws Exception, IOException
    {
        // mouse listeners for dragging
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                Point p = e.getPoint();
                for (Rectangle rect : rectangles)
                {
                    if (rect.contains(p))
                    {
                        dragStart = p;
                        isDragging = true;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                isDragging = false;
                dragStart = null;
                dragEnd = null;
            }
        });

        // rectangle creation on mouse drag 
        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                if (isDragging)
                {
                    dragEnd = e.getPoint();
                    int x = Math.min(dragStart.x, dragEnd.x);
                    int y = Math.min(dragStart.y, dragEnd.y);
                    int width = Math.abs(dragStart.x - dragEnd.x);
                    int height = Math.abs(dragStart.y - dragEnd.y);
                    Rectangle newRect = new Rectangle(x, y, width, height);
                    if (!rectangles.contains(newRect))
                    {
                        rectangles.add(newRect);
                        repaint();
                    }
                }
            }
        });

        // remove rectangles on right-click
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (SwingUtilities.isRightMouseButton(e))
                {
                    Point p = e.getPoint();
                    rectangles.removeIf(rect -> rect.contains(p));
                    repaint();
                }
            }
        });

        // screen boundry check 
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                ScreenWidth = getWidth() - i.left - i.right;
                ScreenHeight = getHeight() - i.top - i.bottom - 2 * (BUTTONH + BUTTONHS);
                Obj.reSize(ScreenWidth, ScreenHeight, SObj);
                sizeScreen();
            }
        });

        // ball-rectangle collision detection
        new Thread(() ->
        {
            while (true)
            {
                if (!isTimePaused)
                {
                    for (Rectangle rect : rectangles)
                    {
                        if (rect.intersects(new Rectangle(Obj.x - SObj / 2, Obj.y - SObj / 2, SObj, SObj)))
                        {
                            if (rect.contains(Obj.x - SObj / 2, Obj.y) || rect.contains(Obj.x + SObj / 2, Obj.y))
                            {
                                dx = -dx;
                            }
                            if (rect.contains(Obj.x, Obj.y - SObj / 2) || rect.contains(Obj.x, Obj.y + SObj / 2))
                            {
                                dy = -dy;
                            }
                        }
                    }
                }

                try
                {
                    Thread.sleep((long) delay);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
        
        // Buttons
        Run = new Button("Run");
        Pause = new Button("Pause");
        Quit = new Button("Quit");

        add(Run);
        add(Pause);
        add(Quit);

        Run.addActionListener(this);
        Pause.addActionListener(this);
        Quit.addActionListener(this);

        Pause.setEnabled(false);

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
        isStarted = false;
        delay = 1000.0 / SpeedScrollBar.getValue();
        start();
    }

    // Thread stuff
    private void start()
    {
        if (thethread == null)
        {
            thethread = new Thread(this);
            thethread.start();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (!isTimePaused)
            {
                Obj.setPrevious(Obj.x, Obj.y);

                Obj.x += dx;
                Obj.y += dy;

                int half = (SObj - 1) / 2;

                // Prevent object from exceeding boundaries
                if (Obj.x - half <= 1)
                {
                    Obj.x = half + 1;
                    dx = -dx;
                }
                else if (Obj.x + half >= ScreenWidth - 2)
                {
                    Obj.x = ScreenWidth - half - 2;
                    dx = -dx;
                }

                if (Obj.y - half <= 1)
                {
                    Obj.y = half + 1;
                    dy = -dy;
                }
                else if (Obj.y + half >= ScreenHeight - 2)
                {
                    Obj.y = ScreenHeight - half - 2;
                    dy = -dy;
                }

                Obj.repaint();
            }

            // Thread animation delay
            try
            {
                Thread.sleep((long) delay);
            }
            catch (InterruptedException e)
            {
                thethread.interrupt();
                return;
            }
        }
    }

    private void stop()
    {
        isTimePaused = true;
        if (thethread != null)
        {
            thethread.interrupt();
            thethread = null;
        }
    }

    private void sizeScreen()
    {
        Run.setLocation(CENTER - BUTTONW - BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);
        Pause.setLocation(CENTER - BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);
        Quit.setLocation(CENTER + BUTTONW / 2, ScreenHeight + BUTTONHS + i.top);

        Run.setSize(BUTTONW, BUTTONH);
        Pause.setSize(BUTTONW, BUTTONH);
        Quit.setSize(BUTTONW, BUTTONH);

        SpeedScrollBar.setLocation(i.left + BUTTONS, ScreenHeight + BUTTONHS + i.top);
        ObjSizeScrollBar.setLocation(WinWidth - ScrollBarW - i.right - BUTTONS, ScreenHeight + BUTTONHS + i.top);

        SPEEDL.setLocation(i.left + BUTTONS, ScreenHeight + BUTTONHS + BUTTONH + i.top);
        SIZEL.setLocation(WinWidth - ScrollBarW - BUTTONS - i.right, ScreenHeight + BUTTONHS + BUTTONH + i.top);

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

        // Prevent resizing too small
        if (WinWidth < minWidth)
        {
            WinWidth = minWidth;
            setSize(WinWidth, WinHeight);
        }

        if (WinHeight < minHeight)
        {
            WinHeight = minHeight;
            setSize(WinWidth, WinHeight);
        }

        makeSheet();
        sizeScreen();

        int maxSize = Math.min(ScreenWidth, ScreenHeight) - 2;
        if (SObj > maxSize)
        {
            SObj = (maxSize / 2) * 2 + 1;
            Obj.updateSize(SObj);
            ObjSizeScrollBar.setValue(SObj);
        }

        Obj.reSize(ScreenWidth, ScreenHeight, SObj);
    }
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        // Run button
        if (source == Run)
        {
            Run.setEnabled(false);
            Pause.setEnabled(true);
            isTimePaused = false;
            isStarted = true;
            start();
        }

        // Pause button
        if (source == Pause)
        {
            Run.setEnabled(true);
            Pause.setEnabled(false);
            isTimePaused = true;
            isStarted = false;
            stop();
        }

        // Quit button
        if (source == Quit)
        {
            System.exit(0);
        }
    }
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        int TS;
        Scrollbar sb = (Scrollbar) e.getSource();

        // Size/speed scrollbars
        if (sb == ObjSizeScrollBar)
        {
            TS = e.getValue();
            TS = (TS / 2) * 2 + 1;

            int maxSize = Math.min(ScreenWidth, ScreenHeight) - 2;
            if (TS > maxSize)
            {
                TS = (maxSize / 2) * 2 + 1;
                ObjSizeScrollBar.setValue(TS);
            }

            Obj.setPrevious(Obj.x, Obj.y);
            SObj = TS;
            Obj.updateSize(SObj);
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
    private int prevX, prevY;
    private int prevSObj;

    public int randomPosition(int min, int max)
    {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public Objc(int SB, int w, int h)
    {
        ScreenWidth = w;
        ScreenHeight = h;
        SObj = SB;
        x = randomPosition(SB, ScreenWidth - SB);
        y = randomPosition(SB, ScreenWidth - SB);
    }

    public void updateSize(int NS)
    {
        SObj = NS;
    }

    public void setPrevious(int px, int py)
    {
        prevX = px;
        prevY = py;
        prevSObj = SObj;
    }

    public void reSize(int w, int h, int currentSObj)
    {
        ScreenWidth = w;
        ScreenHeight = h;
        SObj = currentSObj;

        x = Math.min(x, ScreenWidth - SObj - 1);
        y = Math.min(y, ScreenHeight - SObj - 1);
        x = Math.max(x, 1);
        y = Math.max(y, 1);
    }

    // Drawing the canvas (overridden)
    public void paint(Graphics g)
    {
        g.setColor(Color.blue);
        g.drawRect(0, 0, ScreenWidth - 1, ScreenHeight - 1);
        update(g);
    }

    // Drawing the object (overridden)
    public void update(Graphics g)
    {
        g.setColor(Color.white);
        g.fillOval(prevX - (prevSObj - 1) / 2, prevY - (prevSObj - 1) / 2, prevSObj, prevSObj);

        g.setColor(Color.red);
        g.fillOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj);
        g.setColor(Color.black);
        g.drawOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj - 1, SObj - 1);

        g.setColor(Color.blue);
        g.drawRect(1, 1, ScreenWidth - 1, ScreenHeight - 1);

        g.drawRect(0, 0, ScreenWidth - 1, ScreenHeight - 1);

    }
}
