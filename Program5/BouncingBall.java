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

public class BouncingBall extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, MouseListener, MouseMotionListener, Runnable
{
    // Frame
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    private final int WinLeft = 10;
    private final int WinTop = 10;
    private Point FrameSize = new Point(640, 400);
    private Point Screen = new Point(FrameSize.x - 1, FrameSize.y - 1);
    private Point m1 = new Point(0, 0);
    private Point m2 = new Point(0, 0);
    private Rectangle Perimeter = new Rectangle(0, 0, Screen.x, Screen.y);
    private Rectangle db = new Rectangle();
    private static final Rectangle ZERO = new Rectangle(0, 0, 0, 0);
    private Panel sheet = new Panel();
    private Panel control = new Panel();
    private List list = new List(13);
    private final int BUTTONH = 20;

    // Scrollbars
    private final int SBvisible = 10;
    private final int SBunit = 1;
    private final int SBblock = 10;
    private final int MINSpeed = 1;
    private final int MAXSpeed = 100 + SBvisible;
    private final int INITSpeed = 50;
    private final int MAXSize = 100;
    private final int MINSize = 10;
    private final int INITSize = 21;

    // Ball
    private Ballc Ball;
    private int SPEED = INITSpeed;
    private int SIZE = INITSize;

    // Compontents
    private Button Run = new Button("Run");
    private Button Pause = new Button("Pause");
    private Button Quit = new Button("Quit");
    private Scrollbar SpeedScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
    private Scrollbar SizeScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
    private Label SpeedLabel = new Label("Speed", Label.CENTER);
    private Label SizeLabel = new Label("Size", Label.CENTER);

    // Thread
    private Thread thethread;
    private double delay;
    private boolean isTimePaused;
    private boolean isStarted;
    private boolean more = true;

    private int dx = 2;
    private int dy = 2;

    // For resizing the window based on the ball's position
    private Rectangle r = new Rectangle();
    private int mr, mb;
    private final int EXPAND = 10;

    public static void main(String[] args)
    {
        new BouncingBall();
    }

    public BouncingBall()
    {
        makeSheet();
        try
        {
            initComponents();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        validate();
        setVisible(true);

        // Thread
        isTimePaused = true;
        isStarted = false;
        delay = 1000.0 / SpeedScrollBar.getValue();
        start();
    }

    // Window initialization
    private void makeSheet()
    {
        setLayout(new BorderLayout());
        setBounds(WinLeft, WinTop, FrameSize.x, FrameSize.y);
        setBackground(Color.lightGray);
        addComponentListener(this);
        addWindowListener(this);

        m1.setLocation(0, 0);
        m2.setLocation(0, 0);

        Perimeter.setBounds(0, 0, Screen.x, Screen.y);
        Perimeter.grow(-1, -1);

        sheet.setLayout(new BorderLayout(0, 0));
        sheet.setVisible(true);
        add("Center", sheet);

        control.setLayout(gbl);
        control.setBackground(Color.lightGray);
        control.setVisible(true);
        add("South", control);

        Ball = new Ballc(INITSize, Screen);
        Ball.setBackground(Color.white);
        Ball.addMouseMotionListener(this);
        Ball.addMouseListener(this);
        sheet.add("Center", Ball);
    }

    public void initComponents() throws Exception, IOException
    {
        gbc.insets = new Insets(0, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;

        // Buttons
        Run.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 0;
        control.add(Run, gbc);

        Pause.addActionListener(this);
        gbc.gridx = 2;
        gbc.gridy = 0;
        Pause.setEnabled(false);
        control.add(Pause, gbc);

        Quit.addActionListener(this);
        gbc.gridx = 3;
        gbc.gridy = 0;
        control.add(Quit, gbc);

        // Scrollbars
        SpeedScrollBar.setMaximum(MAXSpeed);
        SpeedScrollBar.setMinimum(MINSpeed);
        SpeedScrollBar.setUnitIncrement(SBunit);
        SpeedScrollBar.setBlockIncrement(SBblock);
        SpeedScrollBar.setValue(INITSpeed);
        SpeedScrollBar.setVisibleAmount(SBvisible);
        SpeedScrollBar.setBackground(Color.gray);
        SpeedScrollBar.addAdjustmentListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        control.add(SpeedScrollBar, gbc);

        SizeScrollBar.setMaximum(MAXSize);
        SizeScrollBar.setMinimum(MINSize);
        SizeScrollBar.setUnitIncrement(SBunit);
        SizeScrollBar.setBlockIncrement(SBblock);
        SizeScrollBar.setValue(INITSize);
        SizeScrollBar.setVisibleAmount(SBvisible);
        SizeScrollBar.setBackground(Color.gray);
        SizeScrollBar.addAdjustmentListener(this);
        gbc.gridx = 4;
        gbc.gridy = 0;
        control.add(SizeScrollBar, gbc);

        // Labels
        gbc.gridx = 0;
        gbc.gridy = 1;
        control.add(SpeedLabel, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        control.add(SizeLabel, gbc);

        control.validate();
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
        while (more)
        {
            try
            {
                Thread.sleep((long)(1000.0 / SPEED));
            }
            catch (InterruptedException e) {}

            if (!isTimePaused)
            {
                Ball.setPrevious(Ball.x, Ball.y);

                Ball.x += dx;
                Ball.y += dy;

                int half = SIZE / 2;

                // Prevent object from exceeding boundaries
                if (Ball.x - half <= 0)
                {
                    Ball.x = half + 1;
                    dx = -dx;
                }
                else if (Ball.x + half >= Ball.getCanvasWidth())
                {
                    Ball.x = Ball.getCanvasWidth() - half - 1;
                    dx = -dx;
                }

                if (Ball.y - half <= 0)
                {
                    Ball.y = half + 1;
                    dy = -dy;
                }
                else if (Ball.y + half >= Ball.getCanvasHeight())
                {
                    Ball.y = Ball.getCanvasHeight() - half - 1;
                    dy = -dy;
                }

                Rectangle ballRect = new Rectangle(Ball.x - half, Ball.y - half, SIZE, SIZE);

                for (int i = 0; i < Ball.getWallSize(); i++)
                {
                    Rectangle wall = Ball.getOne(i);

                    if (ballRect.intersects(wall))
                    {
                        int prevLeft   = Ball.prevX - half;
                        int prevRight  = Ball.prevX + half;
                        int prevTop    = Ball.prevY - half;
                        int prevBottom = Ball.prevY + half;

                        boolean fromLeft   = prevRight  <= wall.x;
                        boolean fromRight  = prevLeft   >= wall.x + wall.width;
                        boolean fromTop    = prevBottom <= wall.y;
                        boolean fromBottom = prevTop    >= wall.y + wall.height;

                        if (fromLeft || fromRight)  dx = -dx;
                        if (fromTop  || fromBottom) dy = -dy;

                        Ball.x = Ball.prevX;
                        Ball.y = Ball.prevY;
                        break;
                    }
                }

                Ball.repaint();
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

    private void stopWindow()
    {
        more = false;
        removeWindowListener(this);
        Ball.removeMouseListener(this);
        Ball.removeMouseMotionListener(this);
        dispose();
        System.exit(0);
    }

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
            stopWindow();
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        int TS = e.getValue();
        Scrollbar sb = (Scrollbar) e.getSource();

        // Size scrollbar
        if (sb == SizeScrollBar)
        {
            TS = (TS / 2) * 2 + 1;
            int maxSize = Math.min(Ball.getCanvasWidth(), Ball.getCanvasHeight()) - 2;
            if (TS > maxSize)
            {
                TS = (maxSize / 2) * 2 + 1;
                SizeScrollBar.setValue(TS);
            }
            Ball.setPrevious(Ball.x, Ball.y);
            SIZE = TS;
            Ball.updateSize(SIZE);
        }

        // Speed scrollbar
        else
        {
            delay = 1000.0 / SpeedScrollBar.getValue();
            SPEED = TS;
        }

        Ball.repaint();
    }

    // Window listeners
    public void windowClosing(WindowEvent e)
    {
        stopWindow();
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
        // Calculate wall bounds
        if (Ball.getWallSize() > 0)
        {
            r.setBounds(Ball.getOne(0));
            mr = r.x + r.width;
            mb = r.y + r.height;
            for (int i = 1; i < Ball.getWallSize(); i++)
            {
                r.setBounds(Ball.getOne(i));
                mr = Math.max((r.x + r.width), mr);
                mb = Math.max((r.y + r.height), mb);
            }
        }
        else
        {
            mr = 0;
            mb = 0;
        }

        // Calculate ball bounds
        r.setBounds(Ball.getBall());
        mr = Math.max((r.x + r.width), mr);
        mb = Math.max((r.y + r.height), mb);

        Screen.setLocation(sheet.getWidth() - 1, sheet.getHeight() - 1);
        Perimeter.setBounds(getX(), getY(), Screen.x, Screen.y);
        Perimeter.grow(-1, -1);
        Ball.reSize(Screen);
        Ball.repaint();
    }

    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    // Mouse listeners
    public void mousePressed(MouseEvent e)
    {
        m1.setLocation(e.getPoint());
    }

    public void mouseReleased(MouseEvent e)
    {
        Rectangle newRect = getDragBox(e);

        if (newRect.width <= 0 || newRect.height <= 0) return;

        if (!Perimeter.contains(newRect)) return;

        int half = SIZE / 2;
        Rectangle ballRect = new Rectangle(Ball.x - half, Ball.y - half, SIZE, SIZE);
        Rectangle expanded = new Rectangle(ballRect);
        expanded.grow(1, 1);
        if (expanded.intersects(newRect)) return;

        boolean addNew = true;
        int i = 0;
        while (i < Ball.getWallSize())
        {
            Rectangle existing = Ball.getOne(i);

            
            if (existing.contains(newRect))
            {
                addNew = false;
                break;
            }

            
            if (newRect.contains(existing))
            {
                Ball.removeOne(i);
            }
            else
            {
                i++;
            }
        }

        if (addNew)
        {
            Ball.addOne(newRect);
        }

        Ball.setDragBox(new Rectangle(ZERO));
        Ball.repaint();
    }

    public void mouseClicked(MouseEvent e)
    {
        Point p = new Point(e.getX(), e.getY());
        int i = 0;
        while (i < Ball.getWallSize())
        {
            Rectangle b = Ball.getOne(i);
            if (b.contains(p))
            {
                Ball.removeOne(i);
            }
            else
            {
                i++;
            }
        }
        Ball.repaint();
    }

    public void mouseMoved(MouseEvent e) {}

    public void mouseDragged(MouseEvent e)
    {
        db.setBounds(getDragBox(e));
        if (Perimeter.contains(db))
        {
            Ball.setDragBox(db);
            Ball.repaint();
            m2.setLocation(e.getPoint());
        }
    }

    public void mouseEntered(MouseEvent e)
    {
        Ball.repaint();
    }

    public void mouseExited(MouseEvent e) {}

    private Rectangle getDragBox(MouseEvent e)
    {
        int x1 = Math.min(m1.x, e.getX());
        int y1 = Math.min(m1.y, e.getY());
        int x2 = Math.max(m1.x, e.getX());
        int y2 = Math.max(m1.y, e.getY());
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }
}

class PanelMouse extends Frame implements WindowListener, MouseListener, MouseMotionListener
{
    final int WinLeft = 10;
    final int WinTop = 10;
    Point FrameSize = new Point(640, 400);
    Panel sheet = new Panel();
    List list = new List(13);

    PanelMouse()
    {
        setLayout(new BorderLayout());
        setBounds(WinLeft, WinTop, FrameSize.x, FrameSize.y);
        setBackground(Color.lightGray);
        sheet.setLayout(new BorderLayout(0, 0));
        sheet.setVisible(true);
        sheet.add("Center", list);
        add("Center", sheet);
        addWindowListener(this);
        list.addMouseListener(this);
        list.addMouseMotionListener(this);
        setVisible(true);
        validate();
    }

    public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void mousePressed(MouseEvent e) { list.add("Mouse pressed"); }
    public void mouseReleased(MouseEvent e) { list.add("Mouse released"); }
    public void mouseClicked(MouseEvent e) { list.add("Mouse clicked"); }
    public void mouseEntered(MouseEvent e) { list.add("Mouse entered"); }
    public void mouseExited(MouseEvent e) { list.add("Mouse exited"); }
    public void mouseMoved(MouseEvent e) { list.add("Mouse moved"); }
    public void mouseDragged(MouseEvent e) { list.add("Mouse dragged"); }
}

class Ballc extends Canvas
{
    Image buffer;
    Graphics g;

    private int canvasWidth;
    private int canvasHeight;

    private int size;
    private int prevSize;
    public int prevX, prevY;
    public int x, y;

    // Walls
    public Vector<Rectangle> walls = new Vector<Rectangle>();
    private Point dragStart = null;
    private Point dragEnd = null;
    private boolean isDragging = false;
    private Rectangle dragBox = null;

    public int randomPosition(int min, int max)
    {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public Ballc(int SB, Point screen)
    {
        size = SB;
        canvasWidth = screen.x;
        canvasHeight = screen.y;
        x = randomPosition(size, canvasWidth - size);
        y = randomPosition(size, canvasHeight - size);
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

    // Drawing the canvas (overridden)
    public void paint(Graphics cg)
    {
        if (canvasWidth <= 0 || canvasHeight <= 0) return;

        buffer = createImage(canvasWidth, canvasHeight);
        if (g != null) g.dispose();
        g = buffer.getGraphics();

        g.setColor(Color.white);
        g.fillRect(0, 0, canvasWidth, canvasHeight);

        //paint rectangles
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

        g.setColor(Color.red);
        g.fillOval(x - size / 2, y - size / 2, size, size);
        g.setColor(Color.black);
        g.drawOval(x - size / 2, y - size / 2, size, size);

        cg.drawImage(buffer, 0, 0, null);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public Rectangle getBall()
    {
        Rectangle r = new Rectangle(x - size / 2, y - size / 2, size, size);
        return r;
    }

    // Walls
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