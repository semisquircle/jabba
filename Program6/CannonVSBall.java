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

public class CannonVSBall implements ActionListener, AdjustmentListener, ComponentListener, WindowListener, ItemListener, MouseListener, MouseMotionListener
{
    // Frame/layout
    private int sw = 650, sh = 480;
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    private Frame sheet = new Frame();
    private Panel controlPanel = new Panel();

    // Menu bar
    private MenuBar mmb = new MenuBar();
    private Menu controlMenu, paramMenu, envMenu;
    private Menu sizeMenu, speedMenu;
    private MenuItem runItem, pauseItem, restartItem, quitItem;
    private CheckboxMenuItem size1Item, size2Item, size3Item, size4Item, size5Item;
    private CheckboxMenuItem speed1Item, speed2Item, speed3Item, speed4Item, speed5Item;
    private CheckboxMenuItem mercuryItem, venusItem, earthItem, moonItem, marsItem,
                             jupiterItem, saturnItem, uranusItem, neptuneItem, plutoItem;

    // Game state
    private Ballc Ball;
    private int playerScore = 0;
    private int ballScore = 0;
    private double elapsedTime = 0.0;
    private volatile boolean running = false;
    private Thread animThread;

    private final int initSize = 30;
    private final int initSpeed = 3;
    private final int initAngle = 45;
    private final int initVelocity = 650;
    private final double initGravity = 32.2;

    // Scrollbars
    private Scrollbar velocityScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
    private Scrollbar angleScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
    private final int sbVisible = 10;
    private final int sbUnit = 1;
    private final int sbBlock = 10;
    private final int minVelocity = 100;
    private final int maxVelocity = 1200;
    private final int minAngle = 0;
    private final int maxAngle = 90;

    // Labels
    private Label velocityLabel = new Label("Velocity: " + initVelocity + " ft/s", Label.CENTER);
    private Label angleLabel = new Label("Angle: " + initAngle + "\u00b0", Label.CENTER);
    private Label timeLabel = new Label("Time: 0.0s", Label.CENTER);
    private Label ballLabel = new Label("Ball: 0", Label.CENTER);
    private Label playerLabel = new Label("Player: 0", Label.CENTER);
    private Label statusLabel = new Label("", Label.CENTER);

    public static void main(String[] args)
    {
        new CannonVSBall();
    }

    public CannonVSBall()
    {
        sheet.setLayout(new BorderLayout(0, 0));
        sheet.setBackground(Color.lightGray);
        sheet.setForeground(Color.black);
        initMenu();

        Ball = new Ballc(sw, sh, initSize, initSpeed, initAngle, initVelocity, initGravity);
        Ball.setBackground(Color.white);
        Ball.addMouseListener(this);
        Ball.addMouseMotionListener(this);
        sheet.add("Center", Ball);

        controlPanel.setLayout(gbl);
        controlPanel.setBackground(Color.lightGray);
        controlPanel.validate();
        controlPanel.setVisible(true);
        try { initControls(); }
        catch (Exception e) { e.printStackTrace(); }
        sheet.add("South", controlPanel);

        sheet.setMenuBar(mmb);
        sheet.addComponentListener(this);
        sheet.addWindowListener(this);
        sheet.setSize(sw, sh);
        sheet.setResizable(true);
        sheet.validate();
        sheet.setVisible(true);

        Ball.repaint();
    }

    // UI initializations
    public void initMenu()
    {
        controlMenu = new Menu("Control");
        runItem = controlMenu.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
        pauseItem = controlMenu.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
        restartItem = controlMenu.add(new MenuItem("Restart"));
        quitItem = controlMenu.add(new MenuItem("Quit"));
        mmb.add(controlMenu);

        paramMenu = new Menu("Parameters");
        paramMenu.add(sizeMenu = new Menu("Size"));
        sizeMenu.add(size1Item = new CheckboxMenuItem("x-small"));
        sizeMenu.add(size2Item = new CheckboxMenuItem("small"));
        sizeMenu.add(size3Item = new CheckboxMenuItem("medium"));
        sizeMenu.add(size4Item = new CheckboxMenuItem("large"));
        sizeMenu.add(size5Item = new CheckboxMenuItem("x-large"));
        size3Item.setState(true);

        paramMenu.add(speedMenu = new Menu("Speed"));
        speedMenu.add(speed1Item = new CheckboxMenuItem("x-slow"));
        speedMenu.add(speed2Item = new CheckboxMenuItem("slow"));
        speedMenu.add(speed3Item = new CheckboxMenuItem("medium"));
        speedMenu.add(speed4Item = new CheckboxMenuItem("fast"));
        speedMenu.add(speed5Item = new CheckboxMenuItem("x-fast"));
        speed3Item.setState(true);
        mmb.add(paramMenu);

        envMenu = new Menu("Environment");
        envMenu.add(mercuryItem = new CheckboxMenuItem("Mercury"));
        envMenu.add(venusItem = new CheckboxMenuItem("Venus"));
        envMenu.add(earthItem = new CheckboxMenuItem("Earth"));
        envMenu.add(moonItem = new CheckboxMenuItem("Moon"));
        envMenu.add(marsItem = new CheckboxMenuItem("Mars"));
        envMenu.add(jupiterItem = new CheckboxMenuItem("Jupiter"));
        envMenu.add(saturnItem = new CheckboxMenuItem("Saturn"));
        envMenu.add(uranusItem = new CheckboxMenuItem("Uranus"));
        envMenu.add(neptuneItem = new CheckboxMenuItem("Neptune"));
        envMenu.add(plutoItem = new CheckboxMenuItem("Pluto"));
        earthItem.setState(true);
        mmb.add(envMenu);

        runItem.addActionListener(this);
        pauseItem.addActionListener(this);
        restartItem.addActionListener(this);
        quitItem.addActionListener(this);

        size1Item.addItemListener(this);
        size2Item.addItemListener(this);
        size3Item.addItemListener(this);
        size4Item.addItemListener(this);
        size5Item.addItemListener(this);

        speed1Item.addItemListener(this);
        speed2Item.addItemListener(this);
        speed3Item.addItemListener(this);
        speed4Item.addItemListener(this);
        speed5Item.addItemListener(this);

        mercuryItem.addItemListener(this);
        venusItem.addItemListener(this);
        earthItem.addItemListener(this);
        moonItem.addItemListener(this);
        marsItem.addItemListener(this);
        jupiterItem.addItemListener(this);
        saturnItem.addItemListener(this);
        uranusItem.addItemListener(this);
        neptuneItem.addItemListener(this);
        plutoItem.addItemListener(this);
    }
    public void initControls() throws Exception, IOException
    {
        gbc.insets = new Insets(2, 10, 2, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;

        velocityScrollBar.setMaximum(maxVelocity + sbVisible);
        velocityScrollBar.setMinimum(minVelocity);
        velocityScrollBar.setUnitIncrement(sbUnit);
        velocityScrollBar.setBlockIncrement(sbBlock);
        velocityScrollBar.setValue(initVelocity);
        velocityScrollBar.setVisibleAmount(sbVisible);
        velocityScrollBar.setBackground(Color.gray);
        velocityScrollBar.addAdjustmentListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(velocityScrollBar, gbc);

        angleScrollBar.setMaximum(maxAngle + sbVisible);
        angleScrollBar.setMinimum(minAngle);
        angleScrollBar.setUnitIncrement(sbUnit);
        angleScrollBar.setBlockIncrement(sbBlock);
        angleScrollBar.setValue(initAngle);
        angleScrollBar.setVisibleAmount(sbVisible);
        angleScrollBar.setBackground(Color.gray);
        angleScrollBar.addAdjustmentListener(this);
        gbc.gridx = 4;
        gbc.gridy = 0;
        controlPanel.add(angleScrollBar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(velocityLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        controlPanel.add(timeLabel, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        controlPanel.add(ballLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        controlPanel.add(playerLabel, gbc);
        gbc.gridx = 4;
        gbc.gridy = 1;
        controlPanel.add(angleLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        controlPanel.add(statusLabel, gbc);
        gbc.gridwidth = 1;

        controlPanel.validate();
    }

    // Game loop
    public void run()
    {
        if (!running) {
            running = true;
            animThread = new Thread(() ->
            {
                while (running)
                {
                    Ball.update();
                    elapsedTime += 0.016;
                    timeLabel.setText(String.format("Time: %.1fs", elapsedTime));

                    if (Ball.playerScored())
                    {
                        playerScore++;
                        playerLabel.setText("Player: " + playerScore);
                        Ball.resetProjectile();
                    }
                    if (Ball.ballScored())
                    {
                        ballScore++;
                        ballLabel.setText("Ball: " + ballScore);
                        Ball.clearBallScored();
                    }

                    statusLabel.setText(Ball.getStatusMessage());
                    Ball.repaint();

                    try { Thread.sleep(16); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            });
            animThread.setDaemon(true);
            animThread.start();
        }
    }
    public void pause() { running = false; }
    public void restart()
    {
        running = false;

        // Wait for old thread to die
        if (animThread != null && animThread.isAlive())
        {
            try { animThread.join(); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        playerScore = 0;
        ballScore = 0;
        elapsedTime = 0.0;
        playerLabel.setText("Player: 0");
        ballLabel.setText("Ball: 0");
        timeLabel.setText("Time: 0.0s");
        statusLabel.setText("");

        Ball.resetAll();

        run();
    }
    public void stop()
    {
        running = false;

        runItem.removeActionListener(this);
        pauseItem.removeActionListener(this);
        restartItem.removeActionListener(this);
        quitItem.removeActionListener(this);

        size1Item.removeItemListener(this);
        size2Item.removeItemListener(this);
        size3Item.removeItemListener(this);
        size4Item.removeItemListener(this);
        size5Item.removeItemListener(this);

        speed1Item.removeItemListener(this);
        speed2Item.removeItemListener(this);
        speed3Item.removeItemListener(this);
        speed4Item.removeItemListener(this);
        speed5Item.removeItemListener(this);

        mercuryItem.removeItemListener(this);
        venusItem.removeItemListener(this);
        earthItem.removeItemListener(this);
        moonItem.removeItemListener(this);
        marsItem.removeItemListener(this);
        jupiterItem.removeItemListener(this);
        saturnItem.removeItemListener(this);
        uranusItem.removeItemListener(this);
        neptuneItem.removeItemListener(this);
        plutoItem.removeItemListener(this);

        sheet.removeWindowListener(this);
        sheet.dispose();
    }

    // Menu + control panel listeners
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == runItem) run();
        if (source == pauseItem) pause();
        if (source == restartItem) restart();
        if (source == quitItem) stop();
    }
    public void itemStateChanged(ItemEvent e)
    {
        CheckboxMenuItem source = (CheckboxMenuItem) e.getSource();

        if (source == size1Item || source == size2Item || source == size3Item ||
            source == size4Item || source == size5Item)
        {
            if      (source == size1Item) Ball.setBallSize(10);
            else if (source == size2Item) Ball.setBallSize(20);
            else if (source == size3Item) Ball.setBallSize(initSize);
            else if (source == size4Item) Ball.setBallSize(40);
            else if (source == size5Item) Ball.setBallSize(50);
            size1Item.setState(false);
            size2Item.setState(false);
            size3Item.setState(false);
            size4Item.setState(false);
            size5Item.setState(false);
            source.setState(true);
        }

        if (source == speed1Item || source == speed2Item || source == speed3Item ||
            source == speed4Item || source == speed5Item)
        {
            if      (source == speed1Item) Ball.setBallSpeed(1);
            else if (source == speed2Item) Ball.setBallSpeed(2);
            else if (source == speed3Item) Ball.setBallSpeed(initSpeed);
            else if (source == speed4Item) Ball.setBallSpeed(4);
            else if (source == speed5Item) Ball.setBallSpeed(5);
            speed1Item.setState(false);
            speed2Item.setState(false);
            speed3Item.setState(false);
            speed4Item.setState(false);
            speed5Item.setState(false);
            source.setState(true);
        }

        if (source == mercuryItem || source == venusItem || source == earthItem ||
            source == moonItem || source == marsItem || source == jupiterItem ||
            source == saturnItem  || source == uranusItem || source == neptuneItem ||
            source == plutoItem)
        {
            if      (source == mercuryItem) Ball.setGravity(12.1);
            else if (source == venusItem)   Ball.setGravity(29.1);
            else if (source == earthItem)   Ball.setGravity(initGravity);
            else if (source == moonItem)    Ball.setGravity(5.3);
            else if (source == marsItem)    Ball.setGravity(12.5);
            else if (source == jupiterItem) Ball.setGravity(81.3);
            else if (source == saturnItem)  Ball.setGravity(34.4);
            else if (source == uranusItem)  Ball.setGravity(28.5);
            else if (source == neptuneItem) Ball.setGravity(36.6);
            else if (source == plutoItem)   Ball.setGravity(2.1);
            mercuryItem.setState(false);
            venusItem.setState(false);
            earthItem.setState(false);
            moonItem.setState(false);
            marsItem.setState(false);
            jupiterItem.setState(false);
            saturnItem.setState(false);
            uranusItem.setState(false);
            neptuneItem.setState(false);
            plutoItem.setState(false);
            source.setState(true);
        }
    }
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        Scrollbar source = (Scrollbar) e.getSource();
        int value = e.getValue();

        if (source == velocityScrollBar)
        {
            Ball.setVelocity(value);
            velocityLabel.setText("Velocity: " + value + " ft/s");
        }
        if (source == angleScrollBar)
        {
            Ball.setCannonAngle(value);
            angleLabel.setText("Angle: " + value + "\u00b0");
        }

        Ball.repaint();
    }

    public Dimension getMinFrameSize()
    {
        Dimension canvasMin = Ball.getMinCanvasSize();
        Dimension controlSize = controlPanel.getPreferredSize();
        Insets insets = sheet.getInsets();
        int width = Math.max(canvasMin.width, controlSize.width) + insets.left + insets.right;
        int height = canvasMin.height + controlSize.height + insets.top + insets.bottom;
        return new Dimension(width, height);
    }

    // Window listeners
    public void windowClosing(WindowEvent e) { stop(); }
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}

    // Component listeners
    public void componentResized(ComponentEvent e)
    {
        Dimension min = getMinFrameSize();
        int w = sheet.getWidth();
        int h = sheet.getHeight();
        if (w < min.width || h < min.height)
            sheet.setSize(Math.max(w, min.width), Math.max(h, min.height));
    }
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    // Mouse listeners
    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() == 1)
        {
            if (Ball.getCannonBounds().contains(e.getPoint()))
            {
                Ball.fireProjectile();
                if (!running) run();
            }
        }
        if (e.getClickCount() == 2)
        {
            Point p = e.getPoint();
            Ball.walls.removeIf(r -> r.contains(p));
            Ball.repaint();
        }
    }
    public void mousePressed(MouseEvent e) { Ball.startDrag(e.getPoint()); }
    public void mouseReleased(MouseEvent e)
    {
        Ball.endDrag(e.getPoint());
        Ball.repaint();
    }
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e)
    {
        Ball.setDragBox(new Rectangle(
            Math.min(Ball.dragStart.x, e.getX()),
            Math.min(Ball.dragStart.y, e.getY()),
            Math.abs(e.getX() - Ball.dragStart.x),
            Math.abs(e.getY() - Ball.dragStart.y)
        ));
        Ball.repaint();
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

class Ballc extends Canvas
{
    // Double buffer
    private Image buffer;
    private Graphics g;

    private int canvasWidth;
    private int canvasHeight;

    // Cannon
    private int cannonAngle;
    private int ax1, ay1, ax2, ay2, cx2, cy2, cx1, cy1;
    Polygon poly = new Polygon();
    private boolean cannonAlive = true;
    private int cannonBaseRadius = 20;
    private int barrelWidth = 18;
    private int halfBarrelWidth = barrelWidth / 2;
    private int barrelLength = 90;

    // Target ball
    private int ballSize;
    private int ballSpeed;
    private double ballX, ballY;
    private int ballDX, ballDY;
    private boolean ballAlive = true;
    private boolean ballHitCannon = false;

    // Projectile
    private int projVelocity;
    private double projGravity;
    private double projX, projY;
    private double projVX, projVY;
    private int projSize = 20;
    private double projVelocityScale = 0.01;
    private boolean projActive = false;
    private boolean projScored = false;
    private String statusMsg = "";

    // Walls
    public Point dragStart = null;
    public Vector<Rectangle> walls = new Vector<Rectangle>();
    private Rectangle dragBox = null;

    public Ballc(int w, int h, int size, int speed, int a, int v, double g)
    {
        canvasWidth = w;
        canvasHeight = h;
        ballSize = size;
        ballSpeed = speed;
        cannonAngle = a;
        projVelocity = v;
        projGravity = g;
        resetBall();
    }

    // Canvas
    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        canvasWidth = w;
        canvasHeight = h;
        buffer = createImage(w, h);
        g = buffer.getGraphics();
    }
    public void reSize(int w, int h)
    {
        canvasWidth = w;
        canvasHeight = h;
    }

    // Getters
    public Rectangle getCannonBounds() {
        Rectangle barrelBounds = poly.getBounds();
        return new Rectangle(barrelBounds.x, barrelBounds.y,
            barrelBounds.width + (canvasWidth - ax2), barrelBounds.height + (canvasHeight - ay1));
    }
    public Rectangle getBallBounds() { return new Rectangle((int) ballX, (int) ballY, ballSize, ballSize); }
    public boolean playerScored() { return projScored; }
    public boolean ballScored() { return ballHitCannon; }
    public String getStatusMessage() { return statusMsg; }

    // Setters
    public void setBallSize(int s) { ballSize = s; }
    public void setBallSpeed(int s)
    {
        ballSpeed = s;
        ballDX = (ballDX < 0) ? -s : s;
        ballDY = (ballDY < 0) ? -s : s;
    }
    public void setCannonAngle(int a) { cannonAngle = a; }
    public void setVelocity(int v) { projVelocity = v; }
    public void setGravity(double g) { projGravity = g; }

    // Resetting objects
    private void resetBall()
    {
        ballX = 60;
        ballY = 60;
        ballDX = ballSpeed;
        ballDY = ballSpeed;
    }
    public void resetProjectile()
    {
        projActive = false;
        projScored = false;
        statusMsg = "";
    }
    public void resetAll()
    {
        ballAlive = true;
        cannonAlive = true;
        walls.clear();
        resetBall();
        resetProjectile();
        repaint();
    }
    public void clearBallScored() { ballHitCannon = false; }

    public void drawCannon(Graphics g)
    {
        int pivotX = canvasWidth - cannonBaseRadius;
        int pivotY = canvasHeight - cannonBaseRadius;

        // Convert angle to radians
        double angleRad = Math.toRadians(cannonAngle - 90);
        double cosAngle = Math.cos(angleRad);
        double sinAngle = Math.sin(angleRad);

        // Unrotated points relative to the pivot
        int unrotatedAx1 = pivotX - halfBarrelWidth;
        int unrotatedAy1 = pivotY;
        int unrotatedAx2 = pivotX + halfBarrelWidth;
        int unrotatedAy2 = pivotY;
        int unrotatedCx1 = pivotX - halfBarrelWidth;
        int unrotatedCy1 = pivotY - barrelLength;
        int unrotatedCx2 = pivotX + halfBarrelWidth;
        int unrotatedCy2 = pivotY - barrelLength;

        // Rotate points around the pivot
        ax1 = (int) (pivotX + (unrotatedAx1 - pivotX) * cosAngle - (unrotatedAy1 - pivotY) * sinAngle);
        ay1 = (int) (pivotY + (unrotatedAx1 - pivotX) * sinAngle + (unrotatedAy1 - pivotY) * cosAngle);
        ax2 = (int) (pivotX + (unrotatedAx2 - pivotX) * cosAngle - (unrotatedAy2 - pivotY) * sinAngle);
        ay2 = (int) (pivotY + (unrotatedAx2 - pivotX) * sinAngle + (unrotatedAy2 - pivotY) * cosAngle);
        cx2 = (int) (pivotX + (unrotatedCx2 - pivotX) * cosAngle - (unrotatedCy2 - pivotY) * sinAngle);
        cy2 = (int) (pivotY + (unrotatedCx2 - pivotX) * sinAngle + (unrotatedCy2 - pivotY) * cosAngle);
        cx1 = (int) (pivotX + (unrotatedCx1 - pivotX) * cosAngle - (unrotatedCy1 - pivotY) * sinAngle);
        cy1 = (int) (pivotY + (unrotatedCx1 - pivotX) * sinAngle + (unrotatedCy1 - pivotY) * cosAngle);

        // Add points to the polygon
        poly.reset();
        poly.addPoint(ax1, ay1);
        poly.addPoint(ax2, ay2);
        poly.addPoint(cx2, cy2);
        poly.addPoint(cx1, cy1);

        // Draw
        g.setColor(Color.gray);
        g.fillPolygon(poly);
        g.setColor(Color.decode("#643d14"));
        g.fillOval(pivotX - cannonBaseRadius, pivotY - cannonBaseRadius, cannonBaseRadius * 2, cannonBaseRadius * 2);
    }

    // Walls
    public void setDragBox(Rectangle r) { dragBox = new Rectangle(r); }
    public void startDrag(Point p) { dragStart = p; dragBox = null; }
    public void endDrag(Point end)
    {
        if (dragStart == null) dragBox = null;
        else
        {
            int x = Math.min(dragStart.x, end.x);
            int y = Math.min(dragStart.y, end.y);
            int w = Math.abs(end.x - dragStart.x);
            int h = Math.abs(end.y - dragStart.y);
            dragStart = null;
            dragBox = null;
            if (w >= 4 && h >= 4)
            {
                Rectangle newRect = new Rectangle(x, y, w, h);
                Rectangle canvas = new Rectangle(0, 0, canvasWidth, canvasHeight);
                if (canvas.contains(newRect) && !newRect.intersects(getBallBounds()) && !newRect.intersects(getCannonBounds()))
                {
                    walls.removeIf(existing -> newRect.contains(existing));
                    walls.addElement(newRect);
                }
            }
        }
    }
    public void addOne(Rectangle r) { walls.addElement(new Rectangle(r)); }
    public void removeOne(int i) { walls.removeElementAt(i); }
    public Rectangle getOne(int i) { return walls.elementAt(i); }
    public Rectangle getWallsBounds()
    {
        if (walls.isEmpty()) return new Rectangle(0, 0, 0, 0);
        Rectangle r = new Rectangle(walls.firstElement());
        for (Rectangle w : walls)
        {
            r = r.union(w);
        }
        return r;
    }
    public Dimension getMinCanvasSize()
    {
        Rectangle wallsBounds = getWallsBounds();
        int minWidth = Math.max(canvasWidth, wallsBounds.x + wallsBounds.width + 20);
        int minHeight = Math.max(canvasHeight, wallsBounds.y + wallsBounds.height + 20);
        return new Dimension(minWidth, minHeight);
    }

    // Calculates initial velocity components from angle and spawns projectile at barrel tip
    public void fireProjectile()
    {
        if (!projActive && cannonAlive)
        {
            double angleRad = Math.toRadians(-cannonAngle);
            projX = (cx1 + cx2) / 2;
            projY = (cy1 + cy2) / 2;
            projVX = -Math.cos(angleRad) * projVelocity * projVelocityScale;
            projVY = Math.sin(angleRad) * projVelocity * projVelocityScale;
            projActive = true;
            projScored = false;
            ballHitCannon = false;
            statusMsg = "";
        }
    }

    public void paint(Graphics cg)
    {
        if (buffer == null) {
            buffer = createImage(canvasWidth, canvasHeight);
            g = buffer.getGraphics();
        }

        g.setColor(Color.white);
        g.fillRect(0, 0, canvasWidth, canvasHeight);

        g.setColor(Color.yellow);
        for (Rectangle temp : walls) {
            g.fillRect(temp.x, temp.y, temp.width, temp.height);
        }

        if (dragBox != null && dragBox.width > 0 && dragBox.height > 0) {
            g.setColor(Color.darkGray);
            g.drawRect(dragBox.x, dragBox.y, dragBox.width, dragBox.height);
        }

        if (ballAlive) {
            g.setColor(Color.red);
            g.fillOval((int) ballX, (int) ballY, ballSize, ballSize);
        }

        if (projActive) {
            g.setColor(Color.blue);
            g.fillOval((int) projX - projSize / 2, (int) projY - projSize / 2, projSize, projSize);
        }

        if (cannonAlive) drawCannon(g);

        cg.drawImage(buffer, 0, 0, null);
    }
    
    // Moves the target ball, applies gravity to projectile, checks all collisions
    // (couldn't get this to work with the override, just leave as is for now)
    public void update()
    {
        if (ballAlive)
        {
            ballX += ballDX;
            ballY += ballDY;

            if (ballX <= 0)
            {
                ballX = 0;
                ballDX = Math.abs(ballDX);
            }
            if (ballX + ballSize >= canvasWidth)
            {
                ballX = canvasWidth - ballSize;
                ballDX = -Math.abs(ballDX);
            }
            if (ballY <= 0) {
                ballY = 0;
                ballDY = Math.abs(ballDY);
            }
            if (ballY + ballSize >= canvasHeight)
            {
                ballY = canvasHeight - ballSize;
                ballDY = -Math.abs(ballDY);
            }

            Rectangle br = getBallBounds();
            boolean collided = false;
            for (Rectangle wall : walls)
            {
                if (!collided && br.intersects(wall))
                {
                    boolean fromLeft = (ballX + ballSize - ballDX) <= wall.x;
                    boolean fromRight = (ballX - ballDX) >= wall.x + wall.width;
                    if (fromLeft || fromRight) ballDX = -ballDX;
                    else ballDY = -ballDY;
                    collided = true;
                }
            }

            if (cannonAlive && br.intersects(getCannonBounds()))
            {
                cannonAlive = false;
                ballHitCannon = true;
            }
        }

        if (projActive)
        {
            // 0.016 = seconds per frame, 0.3 = pixel scale
            projVY += projGravity * 0.016 * 0.3;
            projX += projVX;
            projY += projVY;

            boolean outBottom = projY > canvasHeight + 200;
            boolean outSide = projX < -200 || projX > canvasWidth + 200;

            if (outBottom || outSide)
            {
                projActive = false;
                statusMsg = "Projectile will not return!";
            }

            if (projActive && ballAlive)
            {
                Rectangle pb = new Rectangle((int) projX - 5, (int) projY - 5, 10, 10);
                if (pb.intersects(getBallBounds()))
                {
                    ballAlive = false;
                    projActive = false;
                    projScored = true;
                    statusMsg = "Target destroyed!";
                }
            }

            if (projActive)
            {
                Rectangle pb = new Rectangle((int) projX - projSize / 2, (int) projY - projSize / 2, projSize, projSize);
                boolean hitWall = false;
                for (int i = walls.size() - 1; i >= 0; i--)
                {
                    if (!hitWall && pb.intersects(walls.elementAt(i)))
                    {
                        walls.removeElementAt(i);
                        projActive = false;
                        statusMsg = "";
                        hitWall = true;
                    }
                }
            }
        }
    }
}
