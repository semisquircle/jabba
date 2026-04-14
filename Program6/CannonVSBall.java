/*
    Program name: Program 6, Cannon vs Ball Program
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
import java.util.Vector;
import java.awt.geom.AffineTransform;

public class CannonVSBall extends Frame implements ActionListener, AdjustmentListener, WindowListener, ItemListener, MouseListener
{
    // Frame
    private int sw = 650, sh = 480;
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    private Frame Sheet = new Frame();
    private Panel ControlPanel = new Panel();

    // Menu
    private MenuBar MMB;
    private Menu ControlMenu, ParamMenu, EnvMenu;
    private Menu SizeMenu, SpeedMenu;
    private MenuItem RunItem, PauseItem, RestartItem, QuitItem;
    private CheckboxMenuItem Size1Item, Size2Item, Size3Item, Size4Item, Size5Item;
    private CheckboxMenuItem Speed1Item, Speed2Item, Speed3Item, Speed4Item, Speed5Item;
    private CheckboxMenuItem MercuryItem, VenusItem, EarthItem, MarsItem, JupiterItem, SaturnItem, UranusItem, NeptuneItem, PlutoItem;

    // Scrollbars
    private final int SBVisible = 10;
    private final int SBUnit = 1;
    private final int SBBlock = 10;
    private final int MINVelocity = 100;
    private final int MAXVelocity = 1200;
    private final int INITVelocity = 650;
    private final int MINAngle = 0;
    private final int MAXAngle = 90;
    private final int INITAngle = 45;
    private Scrollbar VelocityScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
    private Scrollbar AngleScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
    private Label VelocityLabel = new Label("Initial Velocity", Label.CENTER);
    private Label AngleLabel = new Label("Angle", Label.CENTER);

    // Labels
    private Label TimeLabel = new Label("Time:", Label.CENTER);
    private Label BallLabel = new Label("Ball:", Label.CENTER);
    private Label PlayerLabel = new Label("Player:", Label.CENTER);

    // Canvas
    private Ballc Ball;
    private int Velocity = INITVelocity;
    private int Angle = INITAngle;

    public static void main(String[] args)
    {
        new CannonVSBall();
    }

    public CannonVSBall()
    {
        Sheet.setLayout(new BorderLayout(0, 0));
        Sheet.setBackground(Color.lightGray);
        Sheet.setForeground(Color.black);

        menu();

        Sheet.setMenuBar(MMB);
        Sheet.addWindowListener(this);
        Sheet.setSize(sw, sh);
        Sheet.setResizable(true);
        Sheet.setVisible(true);
        Sheet.validate();

        Ball = new Ballc(sw, sh, INITVelocity, INITAngle);
        Ball.setBackground(Color.white);
        Ball.addMouseListener(this);
        Sheet.add("Center", Ball);

        ControlPanel.setLayout(gbl);
        ControlPanel.setBackground(Color.lightGray);
        ControlPanel.setVisible(true);
        Sheet.add("South", ControlPanel);

        try
        {
            initControls();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void menu()
    {
        MMB = new MenuBar();

        ControlMenu = new Menu("Control");
        RunItem = ControlMenu.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
        PauseItem = ControlMenu.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
        RestartItem = ControlMenu.add(new MenuItem("Restart"));
        QuitItem = ControlMenu.add(new MenuItem("Quit"));
        MMB.add(ControlMenu);

        ParamMenu = new Menu("Parameters");
        ParamMenu.add(SizeMenu = new Menu("Size"));
        SizeMenu.add(Size1Item = new CheckboxMenuItem("10"));
        SizeMenu.add(Size2Item = new CheckboxMenuItem("20"));
        SizeMenu.add(Size3Item = new CheckboxMenuItem("30"));
        SizeMenu.add(Size4Item = new CheckboxMenuItem("40"));
        SizeMenu.add(Size5Item = new CheckboxMenuItem("50"));
        Size1Item.setState(true);
        ParamMenu.add(SpeedMenu = new Menu("Speed"));
        SpeedMenu.add(Speed1Item = new CheckboxMenuItem("10"));
        SpeedMenu.add(Speed2Item = new CheckboxMenuItem("20"));
        SpeedMenu.add(Speed3Item = new CheckboxMenuItem("30"));
        SpeedMenu.add(Speed4Item = new CheckboxMenuItem("40"));
        SpeedMenu.add(Speed5Item = new CheckboxMenuItem("50"));
        Speed1Item.setState(true);
        MMB.add(ParamMenu);

        EnvMenu = new Menu("Environment");
        EnvMenu.add(MercuryItem = new CheckboxMenuItem("Mercury"));
        EnvMenu.add(VenusItem = new CheckboxMenuItem("Venus"));
        EnvMenu.add(EarthItem = new CheckboxMenuItem("Earth"));
        EnvMenu.add(MarsItem = new CheckboxMenuItem("Mars"));
        EnvMenu.add(JupiterItem = new CheckboxMenuItem("Jupiter"));
        EnvMenu.add(SaturnItem = new CheckboxMenuItem("Saturn"));
        EnvMenu.add(UranusItem = new CheckboxMenuItem("Uranus"));
        EnvMenu.add(NeptuneItem = new CheckboxMenuItem("Neptune"));
        EnvMenu.add(PlutoItem = new CheckboxMenuItem("Pluto"));
        MMB.add(EnvMenu);

        RunItem.addActionListener(this);
        PauseItem.addActionListener(this);
        RestartItem.addActionListener(this);
        QuitItem.addActionListener(this);
        Size1Item.addItemListener(this);
        Size2Item.addItemListener(this);
        Size3Item.addItemListener(this);
        Size4Item.addItemListener(this);
        Size5Item.addItemListener(this);
        Speed1Item.addItemListener(this);
        Speed2Item.addItemListener(this);
        Speed3Item.addItemListener(this);
        Speed4Item.addItemListener(this);
        Speed5Item.addItemListener(this);
        MercuryItem.addItemListener(this);
        VenusItem.addItemListener(this);
        EarthItem.addItemListener(this);
        MarsItem.addItemListener(this);
        SaturnItem.addItemListener(this);
        JupiterItem.addItemListener(this);
        UranusItem.addItemListener(this);
        NeptuneItem.addItemListener(this);
        PlutoItem.addItemListener(this);
    }

    public void initControls() throws Exception, IOException
    {
        gbc.insets = new Insets(0, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;

        // Scrollbars
        VelocityScrollBar.setMaximum(MAXVelocity);
        VelocityScrollBar.setMinimum(MINVelocity);
        VelocityScrollBar.setUnitIncrement(SBUnit);
        VelocityScrollBar.setBlockIncrement(SBBlock);
        VelocityScrollBar.setValue(INITVelocity);
        VelocityScrollBar.setVisibleAmount(SBVisible);
        VelocityScrollBar.setBackground(Color.gray);
        VelocityScrollBar.addAdjustmentListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        ControlPanel.add(VelocityScrollBar, gbc);

        AngleScrollBar.setMaximum(MAXAngle);
        AngleScrollBar.setMinimum(MINAngle);
        AngleScrollBar.setUnitIncrement(SBUnit);
        AngleScrollBar.setBlockIncrement(SBBlock);
        AngleScrollBar.setValue(INITAngle);
        AngleScrollBar.setVisibleAmount(SBVisible);
        AngleScrollBar.setBackground(Color.gray);
        AngleScrollBar.addAdjustmentListener(this);
        gbc.gridx = 4;
        gbc.gridy = 0;
        ControlPanel.add(AngleScrollBar, gbc);

        // Labels
        gbc.gridx = 0;
        gbc.gridy = 1;
        ControlPanel.add(VelocityLabel, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        ControlPanel.add(AngleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        ControlPanel.add(TimeLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        ControlPanel.add(BallLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        ControlPanel.add(PlayerLabel, gbc);

        ControlPanel.validate();
    }

    public void stop()
    {
        RunItem.removeActionListener(this);
        PauseItem.removeActionListener(this);
        RestartItem.removeActionListener(this);
        QuitItem.removeActionListener(this);
        Size1Item.removeItemListener(this);
        Size2Item.removeItemListener(this);
        Size3Item.removeItemListener(this);
        Size4Item.removeItemListener(this);
        Size5Item.removeItemListener(this);
        Speed1Item.removeItemListener(this);
        Speed2Item.removeItemListener(this);
        Speed3Item.removeItemListener(this);
        Speed4Item.removeItemListener(this);
        Speed5Item.removeItemListener(this);
        MercuryItem.removeItemListener(this);
        VenusItem.removeItemListener(this);
        EarthItem.removeItemListener(this);
        MarsItem.removeItemListener(this);
        SaturnItem.removeItemListener(this);
        SaturnItem.removeItemListener(this);
        UranusItem.removeItemListener(this);
        NeptuneItem.removeItemListener(this);
        PlutoItem.removeItemListener(this);
        Sheet.removeWindowListener(this);
        Sheet.dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == RunItem) {}
        if (source == PauseItem) {}
        if (source == RestartItem) {}
        if (source == QuitItem) stop();
    }

    public void itemStateChanged(ItemEvent e)
    {
        CheckboxMenuItem source = (CheckboxMenuItem) e.getSource();
        if (
            source == Size1Item || source == Size2Item || source == Size3Item || source == Size4Item || source == Size5Item)
        {
            Size1Item.setState(false);
            Size2Item.setState(false);
            Size3Item.setState(false);
            Size4Item.setState(false);
            Size5Item.setState(false);
            source.setState(true);
        }
        if (source == Speed1Item || source == Speed2Item || source == Speed3Item || source == Speed4Item || source == Speed5Item)
        {
            Speed1Item.setState(false);
            Speed2Item.setState(false);
            Speed3Item.setState(false);
            Speed4Item.setState(false);
            Speed5Item.setState(false);
            source.setState(true);
        }
        if (source == MercuryItem || source == VenusItem || source == EarthItem || source == MarsItem || source == JupiterItem || source == SaturnItem || source == UranusItem || source == NeptuneItem || source == PlutoItem)
        {
            MercuryItem.setState(false);
            VenusItem.setState(false);
            EarthItem.setState(false);
            MarsItem.setState(false);
            JupiterItem.setState(false);
            SaturnItem.setState(false);
            UranusItem.setState(false);
            NeptuneItem.setState(false);
            PlutoItem.setState(false);
            source.setState(true);
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        Scrollbar source = (Scrollbar) e.getSource();
        int value = e.getValue();

        if (source == VelocityScrollBar)
        {
            Velocity = value;
        }
        if (source == AngleScrollBar)
        {
            Angle = value;
            Ball.setAngle(Angle);
        }

        Ball.repaint();
    }

    // Window listeners
    public void windowClosing(WindowEvent e) {
        stop();
    }
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}

    // Component listeners
    public void componentResized(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    // Mouse listeners
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

class Ballc extends Canvas
{
    Image buffer;
    Graphics g;

    private int canvasWidth;
    private int canvasHeight;
    private int angle;

    private int cannonBaseRadius = 20;
    private int barrelLength = 50;
    private int barrelWidth = 10;

    Polygon poly;
    private float a1, a2, c2, c1;

    public Vector<Rectangle> walls = new Vector<Rectangle>();
    private Rectangle dragBox = null;

    public Ballc(int w, int h, int v, int a)
    {
        canvasWidth = w;
        canvasHeight = h;
        angle = a;
    }

    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(x, y, w, h);
        canvasWidth = w;
        canvasHeight = h;
    }

    public void reSize(int w, int h)
    {
        canvasWidth = w;
        canvasHeight = h;
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

        drawCannon(g);

        cg.drawImage(buffer, 0, 0, null);
    }

    public void setAngle(int a)
    {
        this.angle = a;
    }
    public void drawCannon(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        // Save transform
        AffineTransform old = g2.getTransform();

        // Bottom-right position
        int baseX = canvasWidth - 60;
        int baseY = canvasHeight - 60;

        // Draw base
        g2.setColor(Color.black);
        g2.fillOval(baseX, baseY, cannonBaseRadius * 2, cannonBaseRadius * 2);

        // Center of rotation
        int cx = baseX + cannonBaseRadius;
        int cy = baseY + cannonBaseRadius;

        // Rotate
        g2.rotate(Math.toRadians(270 -angle), cx, cy);

        // Draw barrel (RECTANGLE stays rectangle ✅)
        g2.setColor(Color.darkGray);
        g2.fillRect(cx, cy - barrelWidth / 2, barrelLength, barrelWidth);

        // Restore
        g2.setTransform(old);
    }
    public void drawPolygon(Polygon p)
    {
        // ???
    }

    public void fillPolygon(Polygon p)
    {
        // ???
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
