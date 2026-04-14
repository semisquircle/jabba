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
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Vector;

public class CannonVSBall extends Frame implements ActionListener, AdjustmentListener, WindowListener, ItemListener, MouseListener
{
    // Frame and layout
    private int sw = 650, sh = 480;
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    private Frame Sheet = new Frame();
    private Panel ControlPanel = new Panel();

    // Menu bar and items
    private MenuBar MMB;
    private Menu ControlMenu, ParamMenu, EnvMenu;
    private Menu SizeMenu, SpeedMenu;
    private MenuItem RunItem, PauseItem, RestartItem, QuitItem;
    private CheckboxMenuItem Size1Item, Size2Item, Size3Item, Size4Item, Size5Item;
    private CheckboxMenuItem Speed1Item, Speed2Item, Speed3Item, Speed4Item, Speed5Item;
    private CheckboxMenuItem MercuryItem, VenusItem, EarthItem, MoonItem, MarsItem,
                             JupiterItem, SaturnItem, UranusItem, NeptuneItem, PlutoItem;

    // Scrollbar constants and controls
    private final int SBVisible    = 10;
    private final int SBUnit       = 1;
    private final int SBBlock      = 10;
    private final int MINVelocity  = 100;
    private final int MAXVelocity  = 1200;
    private final int INITVelocity = 650;
    private final int MINAngle     = 0;
    private final int MAXAngle     = 90;
    private final int INITAngle    = 45;

    private Scrollbar VelocityScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
    private Scrollbar AngleScrollBar    = new Scrollbar(Scrollbar.HORIZONTAL);

    // Score, time, and status labels
    private Label VelocityLabel = new Label("Velocity: " + INITVelocity + " ft/s", Label.CENTER);
    private Label AngleLabel    = new Label("Angle: "    + INITAngle    + "\u00b0", Label.CENTER);
    private Label TimeLabel     = new Label("Time: 0.0s",   Label.CENTER);
    private Label BallLabel     = new Label("Ball: 0",      Label.CENTER);
    private Label PlayerLabel   = new Label("Player: 0",    Label.CENTER);
    private Label StatusLabel   = new Label("",             Label.CENTER);

    // Game state
    private Ballc Ball;
    private int Velocity = INITVelocity;
    private int Angle    = INITAngle;

    private int    playerScore = 0;
    private int    ballScore   = 0;
    private double elapsedTime = 0.0;

    private volatile boolean running = false;
    private Thread animThread;

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

        Ball = new Ballc(sw, sh, INITVelocity, INITAngle);
        Ball.setBackground(Color.white);
        Ball.addMouseListener(this);
        Sheet.add("Center", Ball);

        ControlPanel.setLayout(gbl);
        ControlPanel.setBackground(Color.lightGray);
        ControlPanel.setVisible(true);
        Sheet.add("South", ControlPanel);

        Sheet.setMenuBar(MMB);
        Sheet.addWindowListener(this);
        Sheet.setSize(sw, sh);
        Sheet.setResizable(true);
        Sheet.setVisible(true);
        Sheet.validate();

        try { initControls(); }
        catch (Exception e) { e.printStackTrace(); }

        Ball.repaint();
    }

    public void menu()
    {
        MMB = new MenuBar();

        ControlMenu = new Menu("Control");
        RunItem     = ControlMenu.add(new MenuItem("Run",     new MenuShortcut(KeyEvent.VK_R)));
        PauseItem   = ControlMenu.add(new MenuItem("Pause",   new MenuShortcut(KeyEvent.VK_P)));
        RestartItem = ControlMenu.add(new MenuItem("Restart"));
        QuitItem    = ControlMenu.add(new MenuItem("Quit"));
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
        SpeedMenu.add(Speed1Item = new CheckboxMenuItem("1"));
        SpeedMenu.add(Speed2Item = new CheckboxMenuItem("2"));
        SpeedMenu.add(Speed3Item = new CheckboxMenuItem("3"));
        SpeedMenu.add(Speed4Item = new CheckboxMenuItem("4"));
        SpeedMenu.add(Speed5Item = new CheckboxMenuItem("5"));
        Speed3Item.setState(true);
        MMB.add(ParamMenu);

        EnvMenu = new Menu("Environment");
        EnvMenu.add(MercuryItem = new CheckboxMenuItem("Mercury"));
        EnvMenu.add(VenusItem   = new CheckboxMenuItem("Venus"));
        EnvMenu.add(EarthItem   = new CheckboxMenuItem("Earth"));
        EnvMenu.add(MoonItem    = new CheckboxMenuItem("Moon"));
        EnvMenu.add(MarsItem    = new CheckboxMenuItem("Mars"));
        EnvMenu.add(JupiterItem = new CheckboxMenuItem("Jupiter"));
        EnvMenu.add(SaturnItem  = new CheckboxMenuItem("Saturn"));
        EnvMenu.add(UranusItem  = new CheckboxMenuItem("Uranus"));
        EnvMenu.add(NeptuneItem = new CheckboxMenuItem("Neptune"));
        EnvMenu.add(PlutoItem   = new CheckboxMenuItem("Pluto"));
        EarthItem.setState(true);
        MMB.add(EnvMenu);

        RunItem.addActionListener(this);
        PauseItem.addActionListener(this);
        RestartItem.addActionListener(this);
        QuitItem.addActionListener(this);

        Size1Item.addItemListener(this);  Size2Item.addItemListener(this);
        Size3Item.addItemListener(this);  Size4Item.addItemListener(this);
        Size5Item.addItemListener(this);

        Speed1Item.addItemListener(this); Speed2Item.addItemListener(this);
        Speed3Item.addItemListener(this); Speed4Item.addItemListener(this);
        Speed5Item.addItemListener(this);

        MercuryItem.addItemListener(this); VenusItem.addItemListener(this);
        EarthItem.addItemListener(this);   MoonItem.addItemListener(this);
        MarsItem.addItemListener(this);    JupiterItem.addItemListener(this);
        SaturnItem.addItemListener(this);  UranusItem.addItemListener(this);
        NeptuneItem.addItemListener(this); PlutoItem.addItemListener(this);
    }

    public void initControls() throws Exception, IOException
    {
        gbc.insets    = new Insets(2, 10, 2, 10);
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.weightx   = 1.0;
        gbc.weighty   = 0.0;
        gbc.gridwidth = 1;

        VelocityScrollBar.setMaximum(MAXVelocity + SBVisible);
        VelocityScrollBar.setMinimum(MINVelocity);
        VelocityScrollBar.setUnitIncrement(SBUnit);
        VelocityScrollBar.setBlockIncrement(SBBlock);
        VelocityScrollBar.setValue(INITVelocity);
        VelocityScrollBar.setVisibleAmount(SBVisible);
        VelocityScrollBar.setBackground(Color.gray);
        VelocityScrollBar.addAdjustmentListener(this);
        gbc.gridx = 0; gbc.gridy = 0;
        ControlPanel.add(VelocityScrollBar, gbc);

        AngleScrollBar.setMaximum(MAXAngle + SBVisible);
        AngleScrollBar.setMinimum(MINAngle);
        AngleScrollBar.setUnitIncrement(SBUnit);
        AngleScrollBar.setBlockIncrement(SBBlock);
        AngleScrollBar.setValue(INITAngle);
        AngleScrollBar.setVisibleAmount(SBVisible);
        AngleScrollBar.setBackground(Color.gray);
        AngleScrollBar.addAdjustmentListener(this);
        gbc.gridx = 4; gbc.gridy = 0;
        ControlPanel.add(AngleScrollBar, gbc);

        gbc.gridx = 0; gbc.gridy = 1; ControlPanel.add(VelocityLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; ControlPanel.add(TimeLabel,     gbc);
        gbc.gridx = 2; gbc.gridy = 1; ControlPanel.add(BallLabel,     gbc);
        gbc.gridx = 3; gbc.gridy = 1; ControlPanel.add(PlayerLabel,   gbc);
        gbc.gridx = 4; gbc.gridy = 1; ControlPanel.add(AngleLabel,    gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5;
        ControlPanel.add(StatusLabel, gbc);
        gbc.gridwidth = 1;

        ControlPanel.validate();
    }

    // Returns gravitational acceleration in ft/s^2 for the selected planet
    public double getGravity()
    {
        if (MercuryItem.getState()) return 12.1;
        if (VenusItem.getState())   return 29.1;
        if (MoonItem.getState())    return  5.3;
        if (MarsItem.getState())    return 12.5;
        if (JupiterItem.getState()) return 81.3;
        if (SaturnItem.getState())  return 34.4;
        if (UranusItem.getState())  return 28.5;
        if (NeptuneItem.getState()) return 36.6;
        if (PlutoItem.getState())   return  2.1;
        return 32.2;
    }

    public int getBallDiameter()
    {
        if (Size2Item.getState()) return 20;
        if (Size3Item.getState()) return 30;
        if (Size4Item.getState()) return 40;
        if (Size5Item.getState()) return 50;
        return 10;
    }

    public int getBallSpeed()
    {
        if (Speed1Item.getState()) return 1;
        if (Speed2Item.getState()) return 2;
        if (Speed4Item.getState()) return 4;
        if (Speed5Item.getState()) return 5;
        return 3;
    }

    // Starts the game loop on a background thread
    public void startAnimation()
    {
        if (running) return;
        running = true;
        animThread = new Thread(() ->
        {
            while (running)
            {
                Ball.update(getGravity());
                elapsedTime += 0.016;
                TimeLabel.setText(String.format("Time: %.1fs", elapsedTime));

                if (Ball.playerScored())
                {
                    playerScore++;
                    PlayerLabel.setText("Player: " + playerScore);
                    Ball.resetProjectile();
                }
                if (Ball.ballScored())
                {
                    ballScore++;
                    BallLabel.setText("Ball: " + ballScore);
                    Ball.clearBallScored();
                }

                StatusLabel.setText(Ball.getStatusMessage());
                Ball.repaint();

                try { Thread.sleep(16); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            }
        });
        animThread.setDaemon(true);
        animThread.start();
    }

    public void pauseAnimation() { running = false; }

    public void restartGame()
    {
        running      = false;
        playerScore  = 0;
        ballScore    = 0;
        elapsedTime  = 0.0;
        PlayerLabel.setText("Player: 0");
        BallLabel.setText("Ball: 0");
        TimeLabel.setText("Time: 0.0s");
        StatusLabel.setText("");
        Ball.resetAll(getBallDiameter(), getBallSpeed());
        startAnimation();
    }

    public void stop()
    {
        running = false;

        RunItem.removeActionListener(this);
        PauseItem.removeActionListener(this);
        RestartItem.removeActionListener(this);
        QuitItem.removeActionListener(this);

        Size1Item.removeItemListener(this);  Size2Item.removeItemListener(this);
        Size3Item.removeItemListener(this);  Size4Item.removeItemListener(this);
        Size5Item.removeItemListener(this);

        Speed1Item.removeItemListener(this); Speed2Item.removeItemListener(this);
        Speed3Item.removeItemListener(this); Speed4Item.removeItemListener(this);
        Speed5Item.removeItemListener(this);

        MercuryItem.removeItemListener(this); VenusItem.removeItemListener(this);
        EarthItem.removeItemListener(this);   MoonItem.removeItemListener(this);
        MarsItem.removeItemListener(this);    JupiterItem.removeItemListener(this);
        SaturnItem.removeItemListener(this);  UranusItem.removeItemListener(this);
        NeptuneItem.removeItemListener(this); PlutoItem.removeItemListener(this);

        Sheet.removeWindowListener(this);
        Sheet.dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == RunItem)     startAnimation();
        if (source == PauseItem)   pauseAnimation();
        if (source == RestartItem) restartGame();
        if (source == QuitItem)    stop();
    }

    public void itemStateChanged(ItemEvent e)
    {
        CheckboxMenuItem source = (CheckboxMenuItem) e.getSource();

        if (source == Size1Item || source == Size2Item || source == Size3Item ||
            source == Size4Item || source == Size5Item)
        {
            Size1Item.setState(false); Size2Item.setState(false);
            Size3Item.setState(false); Size4Item.setState(false);
            Size5Item.setState(false);
            source.setState(true);
            Ball.setBallDiameter(getBallDiameter());
        }

        if (source == Speed1Item || source == Speed2Item || source == Speed3Item ||
            source == Speed4Item || source == Speed5Item)
        {
            Speed1Item.setState(false); Speed2Item.setState(false);
            Speed3Item.setState(false); Speed4Item.setState(false);
            Speed5Item.setState(false);
            source.setState(true);
            Ball.setBallSpeed(getBallSpeed());
        }

        if (source == MercuryItem || source == VenusItem || source == EarthItem  ||
            source == MoonItem    || source == MarsItem   || source == JupiterItem ||
            source == SaturnItem  || source == UranusItem || source == NeptuneItem ||
            source == PlutoItem)
        {
            MercuryItem.setState(false); VenusItem.setState(false);
            EarthItem.setState(false);   MoonItem.setState(false);
            MarsItem.setState(false);    JupiterItem.setState(false);
            SaturnItem.setState(false);  UranusItem.setState(false);
            NeptuneItem.setState(false); PlutoItem.setState(false);
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
            VelocityLabel.setText("Velocity: " + value + " ft/s");
        }
        if (source == AngleScrollBar)
        {
            Angle = value;
            AngleLabel.setText("Angle: " + value + "\u00b0");
            Ball.setAngle(value);
        }
        Ball.repaint();
    }

    public void windowClosing(WindowEvent e)     { stop(); }
    public void windowClosed(WindowEvent e)      {}
    public void windowOpened(WindowEvent e)      {}
    public void windowActivated(WindowEvent e)   {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e)   {}
    public void windowDeiconified(WindowEvent e) {}

    public void componentResized(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e)   {}
    public void componentShown(ComponentEvent e)   {}
    public void componentHidden(ComponentEvent e)  {}

    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() == 1)
        {
            if (Ball.getCannonBounds().contains(e.getPoint()))
            {
                Ball.fireProjectile(Velocity, Angle, getGravity());
                if (!running) startAnimation();
            }
        }
        if (e.getClickCount() == 2)
        {
            Point p = e.getPoint();
            Ball.walls.removeIf(r -> r.contains(p));
            Ball.repaint();
        }
    }

    public void mousePressed(MouseEvent e)  { Ball.startDrag(e.getPoint()); }
    public void mouseReleased(MouseEvent e) { Ball.endDrag(e.getPoint()); Ball.repaint(); }
    public void mouseMoved(MouseEvent e)    {}
    public void mouseDragged(MouseEvent e)  {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}
}

class Ballc extends Canvas
{
    // Off-screen double buffer
    Image buffer;
    Graphics g;

    private int canvasWidth;
    private int canvasHeight;

    Polygon poly;
    private float a1, a2, c2, c1;

    // Cannon state
    private int     cannonAngle      = 45;
    private boolean cannonAlive      = true;
    private int     cannonBaseRadius = 20;
    private int     barrelLength     = 50;
    private int     barrelWidth      = 10;

    // Target ball state
    private int     ballDiam  = 20;
    private int     ballSpeed = 3;
    private double  ballX, ballY;
    private int     ballDX, ballDY;
    private boolean ballAlive = true;

    // Projectile state
    private boolean projActive    = false;
    private double  projX, projY;
    private double  projVX, projVY;
    private double  gravity       = 32.2;
    private boolean projScored    = false;
    private boolean ballHitCannon = false;
    private String  statusMsg     = "";

    // Rectangle drag state
    private Point dragStart = null;

    public Vector<Rectangle> walls = new Vector<Rectangle>();
    private Rectangle dragBox = null;

    public Ballc(int w, int h, int v, int a)
    {
        canvasWidth  = w;
        canvasHeight = h;
        cannonAngle  = a;
        resetBall();
    }

    private void resetBall()
    {
        ballX  = 60;
        ballY  = 60;
        ballDX = ballSpeed;
        ballDY = ballSpeed;
    }

    public void resetProjectile()
    {
        projActive = false;
        projScored = false;
        statusMsg  = "";
    }

    public void clearBallScored() { ballHitCannon = false; }

    public void resetAll(int diam, int speed)
    {
        ballDiam    = diam;
        ballSpeed   = speed;
        ballAlive   = true;
        cannonAlive = true;
        walls.clear();
        resetBall();
        resetProjectile();
        repaint();
    }

    public void setAngle(int a)        { cannonAngle = a; }
    public void setCannonAngle(int a)  { cannonAngle = a; }
    public void setBallDiameter(int d) { ballDiam = d; }
    public void setBallSpeed(int s)
    {
        ballSpeed = s;
        ballDX = (ballDX < 0 ? -s : s);
        ballDY = (ballDY < 0 ? -s : s);
    }

    public boolean playerScored()     { return projScored; }
    public boolean ballScored()       { return ballHitCannon; }
    public String  getStatusMessage() { return statusMsg; }

    public Rectangle getCannonBounds()
    {
        int w = getWidth();
        int h = getHeight();
        if (w > 0) canvasWidth  = w;
        if (h > 0) canvasHeight = h;
        int baseX = canvasWidth  - 60;
        int baseY = canvasHeight - 60;
        return new Rectangle(baseX - 10, baseY - 10,
                             cannonBaseRadius * 2 + barrelLength + 20,
                             cannonBaseRadius * 2 + 20);
    }

    public Rectangle getBallBounds()
    {
        return new Rectangle((int) ballX, (int) ballY, ballDiam, ballDiam);
    }

    // Calculates initial velocity components from angle and spawns projectile at muzzle tip
    public void fireProjectile(int velocity, int angle, double grav)
    {
        if (projActive) return;
        gravity = grav;
        double scale     = 0.15;
        int    baseX     = canvasWidth  - 60;
        int    baseY     = canvasHeight - 60;
        int    cx        = baseX + cannonBaseRadius;
        int    cy        = baseY + cannonBaseRadius;
        double barrelRad = Math.toRadians(270 - angle);
        projX  = cx + Math.cos(barrelRad) * barrelLength;
        projY  = cy + Math.sin(barrelRad) * barrelLength;
        projVX = Math.cos(barrelRad) * velocity * scale;
        projVY = Math.sin(barrelRad) * velocity * scale;
        projActive    = true;
        projScored    = false;
        ballHitCannon = false;
        statusMsg     = "";
    }

    // Called once per frame: moves the target ball, applies gravity to projectile, checks all collisions
    public void update(double grav)
    {
        if (ballAlive)
        {
            ballX += ballDX;
            ballY += ballDY;

            if (ballX <= 0)                      { ballX = 0;                       ballDX =  Math.abs(ballDX); }
            if (ballX + ballDiam >= canvasWidth)  { ballX = canvasWidth  - ballDiam; ballDX = -Math.abs(ballDX); }
            if (ballY <= 0)                      { ballY = 0;                       ballDY =  Math.abs(ballDY); }
            if (ballY + ballDiam >= canvasHeight) { ballY = canvasHeight - ballDiam; ballDY = -Math.abs(ballDY); }

            Rectangle br = getBallBounds();
            for (Rectangle wall : walls)
            {
                if (br.intersects(wall))
                {
                    boolean fromLeft  = (ballX + ballDiam - ballDX) <= wall.x;
                    boolean fromRight = (ballX - ballDX)             >= wall.x + wall.width;
                    if (fromLeft || fromRight) ballDX = -ballDX;
                    else                       ballDY = -ballDY;
                    break;
                }
            }

            if (cannonAlive && br.intersects(getCannonBounds()))
            {
                cannonAlive   = false;
                ballHitCannon = true;
            }
        }

        if (projActive)
        {
            // Gravity accumulates downward each frame; 0.016 = seconds per frame, 0.3 = pixel scale
            projVY += grav * 0.016 * 0.3;
            projX  += projVX;
            projY  += projVY;

            boolean outBottom = projY > canvasHeight + 200;
            boolean outSide   = projX < -200 || projX > canvasWidth + 200;

            if (outBottom || outSide)
            {
                projActive = false;
                statusMsg  = "Projectile will not return!";
            }

            if (projActive && ballAlive)
            {
                Rectangle pb = new Rectangle((int) projX - 5, (int) projY - 5, 10, 10);
                if (pb.intersects(getBallBounds()))
                {
                    ballAlive  = false;
                    projActive = false;
                    projScored = true;
                    statusMsg  = "Target destroyed!";
                }
            }

            if (projActive)
            {
                Rectangle pb = new Rectangle((int) projX - 5, (int) projY - 5, 10, 10);
                for (int i = walls.size() - 1; i >= 0; i--)
                {
                    if (pb.intersects(walls.elementAt(i)))
                    {
                        walls.removeElementAt(i);
                        projActive = false;
                        statusMsg  = "";
                        break;
                    }
                }
            }
        }
    }

    public void startDrag(Point p) { dragStart = p; dragBox = null; }

    // Finalizes a rectangle drag. Prevents drawing outside of canvas and overlap with ball or cannon
    public void endDrag(Point end)
    {
        if (dragStart == null) return;
        int x = Math.min(dragStart.x, end.x);
        int y = Math.min(dragStart.y, end.y);
        int w = Math.abs(end.x - dragStart.x);
        int h = Math.abs(end.y - dragStart.y);
        dragStart = null;
        dragBox   = null;
        if (w < 4 || h < 4) return;

        Rectangle newRect = new Rectangle(x, y, w, h);
        Rectangle canvas  = new Rectangle(0, 0, canvasWidth, canvasHeight);
        if (!canvas.contains(newRect))             return;
        if (newRect.intersects(getBallBounds()))    return;
        if (newRect.intersects(getCannonBounds()))  return;

        walls.removeIf(existing -> newRect.contains(existing));
        walls.addElement(newRect);
    }

    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(x, y, w, h);
        canvasWidth  = w;
        canvasHeight = h;
    }

    public void reSize(int w, int h) { canvasWidth = w; canvasHeight = h; }

    public void setDragBox(Rectangle r) { dragBox = new Rectangle(r); }

    public void paint(Graphics cg)
    {
        canvasWidth  = getWidth();
        canvasHeight = getHeight();
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

        if (ballAlive)
        {
            g.setColor(Color.red);
            g.fillOval((int) ballX, (int) ballY, ballDiam, ballDiam);
            g.setColor(Color.darkGray);
            g.drawOval((int) ballX, (int) ballY, ballDiam, ballDiam);
        }

        if (projActive)
        {
            g.setColor(Color.black);
            g.fillOval((int) projX - 5, (int) projY - 5, 10, 10);
        }

        drawPolygon(poly);
        fillPolygon(poly);

        if (cannonAlive) drawCannon(g);

        cg.drawImage(buffer, 0, 0, null);
    }

    public void drawPolygon(Polygon p)
    {
        if (p == null) return;
        g.setColor(Color.black);
        g.drawPolygon(p);
    }

    public void fillPolygon(Polygon p)
    {
        if (p == null) return;
        g.setColor(Color.darkGray);
        g.fillPolygon(p);
    }

    // Draws the cannon at bottom-right using Graphics2D rotation so the barrel stays a true rectangle
    public void drawCannon(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        AffineTransform old = g2.getTransform();

        int baseX = canvasWidth  - 60;
        int baseY = canvasHeight - 60;

        g2.setColor(Color.black);
        g2.fillOval(baseX, baseY, cannonBaseRadius * 2, cannonBaseRadius * 2);

        int cx = baseX + cannonBaseRadius;
        int cy = baseY + cannonBaseRadius;

        g2.rotate(Math.toRadians(270 - cannonAngle), cx, cy);

        g2.setColor(Color.darkGray);
        g2.fillRect(cx, cy - barrelWidth / 2, barrelLength, barrelWidth);

        g2.setTransform(old);
    }

    public void addOne(Rectangle r)  { walls.addElement(new Rectangle(r)); }
    public void removeOne(int i)     { walls.removeElementAt(i); }
    public Rectangle getOne(int i)   { return walls.elementAt(i); }
    public int getWallSize()         { return walls.size(); }
    public int getCanvasWidth()      { return canvasWidth; }
    public int getCanvasHeight()     { return canvasHeight; }
}