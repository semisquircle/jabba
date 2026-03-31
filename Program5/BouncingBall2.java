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
import javax.swing.SwingUtilities;

public class BouncingBall2 extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, MouseListener, MouseMotionListener, Runnable
{
    // Window constants
    private final int WIDTH = 640;
    private final int HEIGHT = 400;

    // Button constants
    private final int BUTTONH = 20;
    private final int BUTTONHS = 20;

    // Scrollbar constants
    private final int SBvisible = 10;
    private final int SBunit = 1;
    private final int SBblock = 10;
    private final int MINSpeed = 1;
    private final int MAXSpeed = 100 + SBvisible;
    private final int INITSpeed = 50;
    private final int MAXSize = 100;
    private final int MINSize = 10;
    private final int INITSize = 21;
    private final int SCROLLBARH = BUTTONH;

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

    private int SPEED = INITSpeed;
    private int SIZE = INITSize;
    private int ScrollBarW;

    private Insets i;

    // Compontents
    private Ballc Ball;
    private Button Run, Pause, Quit;
    private Scrollbar SpeedScrollBar;
    private Scrollbar SizeScrollBar;
    private Label SpeedLabel = new Label("Speed", Label.CENTER);
    private Label SizeLabel = new Label("Size", Label.CENTER);

    // Frame
    private Point FrameSize = new Point(640, 400);
    private Point Screen = new Point(FrameSize.x - 1, FrameSize.y - 1);
    private Point m1 = new Point(0, 0);
    private Point m2 = new Point(0, 0);
    private Rectangle Perimeter = new Rectangle(0, 0, Screen.x, Screen.y);
    private Rectangle db = new Rectangle();
    private static final Rectangle ZERO = new Rectangle(0, 0, 0, 0);
    Panel sheet = new Panel();
    List list = new List(13);

    // Thread variables
    private double delay;
    private Thread thethread;
    private boolean isTimePaused;
    private boolean isStarted;

    private int dx = 2;
    private int dy = 2;

    // Dragging variables
    private Vector<Rectangle> Walls = new Vector<Rectangle>();

    private Point dragStart = null;
    private Point dragEnd = null;
    private boolean isDragging = false;

    public static void main(String[] args)
    {
        new BouncingBall2();
    }

    public BouncingBall2()
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
                for (Rectangle rect : Walls)
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
                    if (!Walls.contains(newRect))
                    {
                        Walls.add(newRect);
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
                    Walls.removeIf(rect -> rect.contains(p));
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
                Ball.reSize(ScreenWidth, ScreenHeight, INITSize);
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
                    for (Rectangle rect : Walls)
                    {
                        if (rect.intersects(new Rectangle(Ball.x - INITSize / 2, Ball.y - INITSize / 2, INITSize, INITSize)))
                        {
                            if (rect.contains(Ball.x - INITSize / 2, Ball.y) || rect.contains(Ball.x + INITSize / 2, Ball.y))
                            {
                                dx = -dx;
                            }
                            if (rect.contains(Ball.x, Ball.y - INITSize / 2) || rect.contains(Ball.x, Ball.y + INITSize / 2))
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

        Run.setEnabled(true);
        Pause.setEnabled(false);

        // Scrollbars
        SpeedScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
        SizeScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);

        SpeedScrollBar.setMaximum(MAXSpeed);
        SpeedScrollBar.setMinimum(MINSpeed);
        SpeedScrollBar.setUnitIncrement(SBunit);
        SpeedScrollBar.setBlockIncrement(SBblock);
        SpeedScrollBar.setValue(INITSpeed);
        SpeedScrollBar.setVisibleAmount(SBvisible);
        SpeedScrollBar.setBackground(Color.gray);

        SizeScrollBar.setMaximum(MAXSize);
        SizeScrollBar.setMinimum(MINSize);
        SizeScrollBar.setUnitIncrement(SBunit);
        SizeScrollBar.setBlockIncrement(SBblock);
        SizeScrollBar.setValue(INITSize);
        SizeScrollBar.setVisibleAmount(SBvisible);
        SizeScrollBar.setBackground(Color.gray);

        add(SpeedScrollBar);
        add(SizeScrollBar);
        add(SpeedLabel);
        add(SizeLabel);

        SpeedScrollBar.addAdjustmentListener(this);
        SizeScrollBar.addAdjustmentListener(this);

        m1.setLocation(0, 0);
        m2.setLocation(0, 0);

        Perimeter.setBounds(0, 0, Screen.x, Screen.y);
        Perimeter.grow(-1, -1);

        Ball = new Ballc(INITSize, ScreenWidth, ScreenHeight);
        Ball.setBackground(Color.white);

        add(Ball);

        addComponentListener(this);
        addWindowListener(this);

        setBounds(WinLeft, WinTop, FrameSize.x, FrameSize.y);
        setBackground(Color.lightGray);
        setVisible(true);

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
                Ball.setPrevious(Ball.x, Ball.y);

                Ball.x += dx;
                Ball.y += dy;

                int half = (INITSize - 1) / 2;

                // Prevent object from exceeding boundaries
                if (Ball.x - half <= 1)
                {
                    Ball.x = half + 1;
                    dx = -dx;
                }
                else if (Ball.x + half >= ScreenWidth - 2)
                {
                    Ball.x = ScreenWidth - half - 2;
                    dx = -dx;
                }

                if (Ball.y - half <= 1)
                {
                    Ball.y = half + 1;
                    dy = -dy;
                }
                else if (Ball.y + half >= ScreenHeight - 2)
                {
                    Ball.y = ScreenHeight - half - 2;
                    dy = -dy;
                }

                Ball.repaint();
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
        SizeScrollBar.setLocation(WinWidth - ScrollBarW - i.right - BUTTONS, ScreenHeight + BUTTONHS + i.top);

        SpeedLabel.setLocation(i.left + BUTTONS, ScreenHeight + BUTTONHS + BUTTONH + i.top);
        SizeLabel.setLocation(WinWidth - ScrollBarW - BUTTONS - i.right, ScreenHeight + BUTTONHS + BUTTONH + i.top);

        SpeedScrollBar.setSize(ScrollBarW, SCROLLBARH);
        SizeScrollBar.setSize(ScrollBarW, SCROLLBARH);

        SpeedLabel.setSize(ScrollBarW, BUTTONH);
        SizeLabel.setSize(ScrollBarW, BUTTONH);

        Ball.setBounds(i.left, i.top, ScreenWidth, ScreenHeight);
    }

    // Window listeners
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

    // Component listeners
    public void componentResized(ComponentEvent e)
    {
        r.setBounds(Ball.getOne(0));
        mr = r.x + r.width;
        mb = r.y + r.height;
        for (int i; i < ; i++)
        {
            r.setBounds(Ball.getOne(i));
            mr = Math.max((r.x + r.width), mr);
            mb = Math.max((r.y + r.height), mb);
        }
        r.setBounds(Ball.getBall());
        mr = Math.max((r.x + r.width), mr);
        mb = Math.max((r.y + r.height), mb);
        if (mr > sw || mb > sh)
        {
            setSize(Math.max((mr + EXPAND), sw) + lw, Math.max((mb + EXPAND), sh) + lh + 2 * BUTTONH);
        }
        setExtendedState(ICONIFIED);
        setExtendedState(NORMAL);
        Screen.setLocation(sheet.getWidth() - 1, sheet.getHeight() - 1);
        Perimeter.setBounds(getX(), getY(), Screen.x, Screen.y);
        Perimeter.grow(-1, -1);
        Ball.reSize(Screen);
        Ball.repaint();
    }
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    // Action listeners
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
        if (sb == SizeScrollBar)
        {
            TS = e.getValue();
            TS = (TS / 2) * 2 + 1;

            int maxSize = Math.min(ScreenWidth, ScreenHeight) - 2;
            if (TS > maxSize)
            {
                TS = (maxSize / 2) * 2 + 1;
                SizeScrollBar.setValue(TS);
            }

            Ball.setPrevious(Ball.x, Ball.y);
            SIZE = TS;
            Ball.updateSize(SIZE);
        }
        else
        {
            delay = 1000.0 / SpeedScrollBar.getValue();
        }

        Ball.repaint();
    }

    // Mouse listeners
    public void mousePressed(MouseEvent e)
    {
        String button = "";
        if (e.getButton() == MouseEvent.BUTTON1) button = "Left";
        if (e.getButton() == MouseEvent.BUTTON2) button = "Center";
        if (e.getButton() == MouseEvent.BUTTON3) button = "Right";
        list.add(button + " mouse button " + e.getButton() + " pressed");
    }
    public void mouseReleased(MouseEvent e)
    {
        list.add("Mouse button " + e.getButton() + " released");
    }
    public void mouseClicked(MouseEvent e)
    {
        list.add("Mouse clicked " + e.getClickCount() + " clicks");
    }
    public void mouseMoved(MouseEvent e)
    {
        list.add("Mouse moved");
    }
    public void mouseDragged(MouseEvent e)
    {
        list.add("Mouse dragged");
    }
    public void mouseEntered(MouseEvent e)
    {
        list.add("Mouse entered");
    }
    public void mouseExited(MouseEvent e)
    {
        list.add("Mouse exited");
    }

    // Walls
    public void addOne(Rectangle r)
    {
        Walls.addElement(new Rectangle(r));
    }
    public void removeOne(int i)
    {
        Walls.removeElementAt(i);
    }
    public Rectangle getOne(int i)
    {
        return Walls.elementAt(i);
    }
    public int getWallSize()
    {
        return Walls.size();
    }
}

class Ballc extends Canvas
{
    Image buffer;
    Graphics g;

    private int width;
    private int height;
    private int size;

    public int x, y;
    private int prevX, prevY;
    private int prevSize;

    public int randomPosition(int min, int max)
    {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public Ballc(int SB, int w, int h)
    {
        ScreenWidth = w;
        ScreenHeight = h;
        size = SB;
        x = randomPosition(SB, ScreenWidth - SB);
        y = randomPosition(SB, ScreenWidth - SB);
    }

    public void updateSize(int NS)
    {
        size = NS;
    }

    public void setPrevious(int px, int py)
    {
        prevX = px;
        prevY = py;
        prevSize = size;
    }

    public void reSize(int w, int h, int currentINITSize)
    {
        ScreenWidth = w;
        ScreenHeight = h;
        size = currentINITSize;

        x = Math.min(x, ScreenWidth - size - 1);
        y = Math.min(y, ScreenHeight - size - 1);
        x = Math.max(x, 1);
        y = Math.max(y, 1);
    }

    // Drawing the canvas (overridden)
    public void paint(Graphics cg)
    {
        buffer = createImage(width, height);
        if (g != null) g.dispose();
        g = buffer.getGraphics();
        g.setColor(Color.red);
        g.fillOval((int) ball.getX(), (int) ball.getY(), (int) ball.getWidth(), (int) ball.getHeight());
        g.setColor(Color.black);
        g.drawOval((int) ball.getX(), (int) ball.getY(), (int) ball.getWidth(), (int) ball.getHeight());
        cg.drawImage(buffer, 0, 0, null);
    }

    public void update(Graphics g)
    {
    }
}
