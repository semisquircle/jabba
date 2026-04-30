/*
    Program name: Program 8, Fix It Filip Program
    Course: CMSC 3320, Technical Computing Using Java
    Group: #3
    Members:
        Shawn Gallagher - GAL82896@pennwest.edu
        Lucas Giovannelli - GIO07221@pennwest.edu
        Joshua Watson - WAT93888@pennwest.edu
*/

package spatter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import EDU.emporia.mathbeans.*;
import EDU.emporia.mathtools.*;
import java.util.*;

public class SpatterApplication extends JFrame implements WindowListener, ActionListener
{
    final double gravity = 4;
    final double wallDistance = 6;

    double inputHeight = 4;
    double inputAngle = 45;
    double inputVelocity = 1.4;

    double t = 0;
    double x1 = 0, y1 = inputHeight;
    double oldx1 = x1, oldy1 = y1;
    double x2 = 1, y2 = inputHeight + 1;
    double oldx2 = x2, oldy2 = y2;
    double spatterWidth = 0, spatterLength = 0;

    boolean dragging1 = false;
    boolean dragging2 = false;

    javax.swing.Timer animationTimer;
    boolean move = false;
    private boolean isStandalone = false;

    JPanel jPanel1 = new JPanel();

    //* Left components
    JLabel jLabel1 = new JLabel();
    JButton trackButton = new JButton();
    JButton resetButton = new JButton();
    JLabel initialHeightLabel = new JLabel();
    JLabel initialAngleLabel = new JLabel();
    JLabel initialVelocityLabel = new JLabel();
    MathTextField initialHeightField = new MathTextField();
    MathTextField initialAngleField = new MathTextField();
    MathTextField initialVelocityField = new MathTextField();

    //* Center components
    MathGrapher graph = new MathGrapher();
    MathGrapher dropShapeGraph = new MathGrapher();
    SymbolicParametricCurve bloodPath = new SymbolicParametricCurve();
    SymbolicParametricCurve directionVector = new SymbolicParametricCurve();
    SymbolicParametricCurve wall = new SymbolicParametricCurve();
    Ellipse spatterEllipse = new Ellipse();

    //* Right components
    JLabel floorOrWallLabel = new JLabel();
    JLabel widthLabel = new JLabel();
    JLabel lengthLabel = new JLabel();
    JLabel jLabel2 = new JLabel();
    MathTextField widthMathTextField = new MathTextField();
    MathTextField lengthMathTextField = new MathTextField();
    MathTextField angleMathTextField = new MathTextField();

    private String getProperty(Properties property, String key, String def)
    {
        String temp;
        try {
            temp = property.getProperty(key);
            if (temp.equals(""))
                temp = def;
        } catch (NullPointerException e) {
            temp = def;
        }
        return temp;
    }

    public static void main(String[] args)
    {
        try {
            SpatterApplication s = new SpatterApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Component initialization
    public SpatterApplication() throws Exception
    {
        animationTimer = new javax.swing.Timer(1, this);
        this.setSize(new Dimension(660, 440));
        jPanel1.setLayout(null);

        //* Left
        jLabel1.setText("Blood Spatter");
        jLabel1.setBounds(new Rectangle(0, 17, 140, 38));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);

        trackButton.setBounds(new Rectangle(23, 81, 101, 39));
        trackButton.setText("Trace path");
        trackButton.addActionListener(new SpatterApplication_trackButton_actionAdapter(this));

        resetButton.addActionListener(new SpatterApplication_resetButton_actionAdapter(this));
        resetButton.setText("reset");
        resetButton.addActionListener(new SpatterApplication_resetButton_actionAdapter(this));
        resetButton.setBounds(new Rectangle(23, 142, 101, 39));

        initialHeightLabel.setText("Initial Height (m):");
        initialHeightLabel.setBounds(new Rectangle(4, 190, 140, 25));
        initialHeightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        initialHeightField.setText("" + inputHeight);
        initialHeightField.setBounds(new Rectangle(18, 215, 110, 30));
        initialHeightField.setFont(new java.awt.Font("Dialog", 0, 14));
        initialHeightField.setHorizontalAlignment(SwingConstants.CENTER);
        initialHeightField.setEditable(false);

        initialAngleLabel.setText("Initial Angle:");
        initialAngleLabel.setBounds(new Rectangle(4, 250, 140, 25));
        initialAngleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        initialAngleField.setText("" + inputAngle);
        initialAngleField.setBounds(new Rectangle(18, 275, 110, 30));
        initialAngleField.setFont(new java.awt.Font("Dialog", 0, 14));
        initialAngleField.setHorizontalAlignment(SwingConstants.CENTER);
        initialAngleField.setEditable(false);

        initialVelocityLabel.setText("Initial Velocity (m/s):");
        initialVelocityLabel.setBounds(new Rectangle(4, 310, 140, 25));
        initialVelocityLabel.setHorizontalAlignment(SwingConstants.CENTER);

        initialVelocityField.setText("" + inputVelocity);
        initialVelocityField.setBounds(new Rectangle(18, 335, 110, 30));
        initialVelocityField.setFont(new java.awt.Font("Dialog", 0, 14));
        initialVelocityField.setHorizontalAlignment(SwingConstants.CENTER);
        initialVelocityField.setEditable(false);

        //* Center
        graph.setTraceEnabled(false);
        graph.setF(bloodPath);
        graph.setG(directionVector);
        graph.setGridLines(EDU.emporia.mathbeans.MathGrapher.GRIDOFF);
        graph.setToolTipText("Drag left hand point to adjust height, right hand point to adjust direction and velocity");
        graph.setXMax(6.5); // FIXED: wall is more visible
        graph.setXMin(0.0);
        graph.setYMax(10.0);
        graph.setYMin(0.0);
        graph.setBounds(new Rectangle(140, 5, 364, 390));
        graph.addMouseMotionListener(new SpatterApplication_graph_mouseMotionAdapter(this));
        graph.addMouseListener(new SpatterApplication_graph_mouseAdapter(this));

        dropShapeGraph.setTraceEnabled(false);
        dropShapeGraph.setAxesColor(Color.lightGray);
        dropShapeGraph.setGridColor(Color.lightGray);
        dropShapeGraph.setTitleEnabled(false);
        dropShapeGraph.setXLabel("");
        dropShapeGraph.setXMax(5.0);
        dropShapeGraph.setXMin(-5.0);
        dropShapeGraph.setYLabel("");
        dropShapeGraph.setYMax(5.0);
        dropShapeGraph.setYMin(-5.0);
        dropShapeGraph.setBounds(new Rectangle(507, 7, 131, 124));

        bloodPath.setYFormula("1");
        bloodPath.setTMax(20.0);
        bloodPath.setTMin(0.0);

        directionVector.setXFormula("0");
        directionVector.setTMax(1.0);
        directionVector.setTMin(0.0);

        wall.setXFormula("" + wallDistance);
        wall.setYFormula("t");
        wall.setTMin(0.0);

        //* Right
        floorOrWallLabel.setHorizontalAlignment(SwingConstants.CENTER);
        floorOrWallLabel.setText("Press \"Trace path\"");
        floorOrWallLabel.setBounds(new Rectangle(517, 135, 114, 28));

        widthLabel.setText("width (in mm):");
        widthLabel.setBounds(new Rectangle(520, 190, 114, 25));
        widthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        widthMathTextField.setText("");
        widthMathTextField.setBounds(new Rectangle(520, 215, 110, 30));
        widthMathTextField.setMargin(new Insets(1, 1, 1, 1));
        widthMathTextField.setFont(new java.awt.Font("Dialog", 0, 14));
        widthMathTextField.setHorizontalAlignment(SwingConstants.CENTER);
        widthMathTextField.setMaxNumberOfCharacters(8);
        widthMathTextField.setRequestFocusEnabled(true);
        widthMathTextField.setEditable(false);

        lengthLabel.setText("height (in mm):");
        lengthLabel.setBounds(new Rectangle(520, 250, 114, 25));
        lengthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        lengthMathTextField.setText("");
        lengthMathTextField.setBounds(new Rectangle(521, 275, 110, 30));
        lengthMathTextField.setMargin(new Insets(1, 1, 1, 1));
        lengthMathTextField.setFont(new java.awt.Font("Dialog", 0, 14));
        lengthMathTextField.setHorizontalAlignment(SwingConstants.CENTER);
        lengthMathTextField.setMaxNumberOfCharacters(8);
        lengthMathTextField.setRequestFocusEnabled(true);
        lengthMathTextField.setEditable(false);

        jLabel2.setText("Angle of impact:");
        jLabel2.setBounds(new Rectangle(520, 310, 114, 24));
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);

        angleMathTextField.setText("");
        angleMathTextField.setBounds(new Rectangle(521, 335, 110, 30));
        angleMathTextField.setMargin(new Insets(1, 1, 1, 1));
        angleMathTextField.setFont(new java.awt.Font("Dialog", 0, 14));
        angleMathTextField.setHorizontalAlignment(SwingConstants.CENTER);
        angleMathTextField.setMaxNumberOfCharacters(10);
        angleMathTextField.setRequestFocusEnabled(true);
        angleMathTextField.setEditable(false);

        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        this.setResizable(false);

        jPanel1.add(graph, null);
        jPanel1.add(jLabel1, null);
        jPanel1.add(trackButton, null);
        jPanel1.add(dropShapeGraph, null);
        jPanel1.add(floorOrWallLabel, null);
        jPanel1.add(widthLabel, null);
        jPanel1.add(lengthLabel, null);
        jPanel1.add(lengthMathTextField, null);
        jPanel1.add(widthMathTextField, null);
        jPanel1.add(angleMathTextField, null);
        jPanel1.add(jLabel2, null);
        jPanel1.add(resetButton, null);
        jPanel1.add(initialHeightLabel, null);
        jPanel1.add(initialHeightField, null);
        jPanel1.add(initialAngleLabel, null);
        jPanel1.add(initialAngleField, null);
        jPanel1.add(initialVelocityLabel, null);
        jPanel1.add(initialVelocityField, null);

        setVisible(true); // make it visible
        validate(); // validate the layout
        addWindowListener(this);
        setTitle("Spatter Application");

        graph.setPointRadius(4);
        graph.updateGraph();
        dropShapeGraph.removeAll();
        repaint();
    }

    // Get Application information
    public String getApplicationInfo()
    {
        return "Application Information";
    }

    // Get parameter info
    public String[][] getParameterInfo()
    {
        return null;
    }

    public void stop()
    {
        this.removeWindowListener(this);
    }

    public void windowClosing(WindowEvent e)
    {
        stop();
        dispose();
        System.exit(0);
    }
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}

    public void actionPerformed(ActionEvent e)
    {
        Point2D p = bloodPath.getPoint(t);
        graph.plotPoint(p.getX(), p.getY());
        t += 0.02;
        graph.updateGraph();
        if (t > wallDistance / (x2 - x1)) {
            animationTimer.stop();
            floorOrWallLabel.setText("Wall spatter shape");
            lengthLabel.setText("Height (in mm):");
            spatterEllipse.setXRadius((1 + Math.random()) / 2);
            spatterEllipse.setYRadius(spatterEllipse.getXRadius() / Math.abs(Math.cos(angle(t))));
            dropShapeGraph.addGraph(spatterEllipse, Color.RED);
            widthMathTextField.setMathValue(places1(10 * spatterEllipse.getXRadius()));
            lengthMathTextField.setMathValue(places1(10 * spatterEllipse.getYRadius()));
            angleMathTextField.setMathValue(places1(Math.abs(90 - angle(t) * 180 / Math.PI)));
        }
        if (t > (((y2 - y1) + Math.sqrt((y1 - y2) * (y1 - y2) + 4 * gravity * y1))) / (2 * gravity)) {
            animationTimer.stop();
            floorOrWallLabel.setText("Floor spatter shape");
            lengthLabel.setText("Length (in mm):");
            spatterEllipse.setXRadius((1 + Math.random()) / 2);
            spatterEllipse.setYRadius(spatterEllipse.getXRadius() / Math.abs(Math.sin(angle(t))));
            dropShapeGraph.addGraph(spatterEllipse, Color.RED);
            widthMathTextField.setMathValue(places1(10 * spatterEllipse.getXRadius()));
            lengthMathTextField.setMathValue(places1(10 * spatterEllipse.getYRadius()));
            angleMathTextField.setMathValue(places1(Math.abs(angle(t) * 180 / Math.PI)));
        }
    }

    public double places1(double x)
    {
        x = 10 * x;
        x = Math.round(x);
        x = (double) x / 10;
        return x;
    }

    public double places2(double x)
    {
        x = 100 * x;
        x = Math.round(x);
        x = (double) x / 100;
        return x;
    }

    public double x(double t)
    {
        return ((x2 - x1) * t);
    }

    public double y(double t)
    {
        return (y1 + (y2 - y1) * t - 0.5 * gravity * t * t); // FIXED: added 1/2 factor
    }

    // FIXED: angle calculation was made velocity-based
    public double angle(double t)
    {
        double vx = (x2 - x1);
        double vy = (y2 - y1) - gravity * t; // fixed physics
        return Math.atan2(vy, vx);
    }

    public void repaint()
    {
        graph.removeAllPoints();
        graph.removeAll();
        graph.addPoint(x1, y1, Color.magenta);
        graph.addPoint(x2, y2, Color.magenta);
        try {
            directionVector.setXFormula(x2 + "*t");
            directionVector.setYFormula(y1 + "+" + "(" + y2 + "-" + y1 + ")*" + "t");
        } catch (Graphable_error e) {
        }
        if (x1 == x2) {
        } else {
        }
        try {
            bloodPath.setXFormula("(" + x2 + "-" + x1 + ")*t");
            bloodPath.setYFormula(y1 + "+(" + y2 + "-" + y1 + ")*t-" + gravity + "*t*t");
        } catch (Graphable_error e) {
        }
        graph.addGraph(directionVector, Color.MAGENTA);
        graph.addGraph(bloodPath, Color.RED);
        graph.addGraph(wall, Color.BLUE);
        graph.updateGraph();
    }

    void updateInitialValues()
    {
        inputHeight = y1;
        double vx = (x2 - x1);
        double vy = (y2 - y1);
        inputVelocity = Math.sqrt(vx * vx + vy * vy);
        inputAngle = Math.toDegrees(Math.atan2(vy, vx));
        initialHeightField.setText("" + places1(inputHeight));
        initialVelocityField.setText("" + places1(inputVelocity));
        initialAngleField.setText("" + places1(inputAngle));
    }

    //* Button listeners
    void trackButton_actionPerformed(ActionEvent e)
    {
        dropShapeGraph.removeGraph(spatterEllipse);
        t = 0;
        animationTimer.start();
    }
    void resetButton_actionPerformed(ActionEvent e)
    {
        animationTimer = null;
        animationTimer = new javax.swing.Timer(1, this);
        t = 0;
        x1 = 0;
        y1 = 4;
        oldx1 = x1;
        oldy1 = y1;
        x2 = 1;
        y2 = 5;
        oldx2 = x2;
        oldy2 = y2;
        spatterWidth = 0;
        spatterLength = 0;
        try {
            bloodPath.setYFormula("1");
            directionVector.setXFormula("0");
            wall.setXFormula("" + wallDistance);
            wall.setYFormula("t");
        } catch (Graphable_error r) {
        }
        floorOrWallLabel.setText("Press \"Trace path\"");
        lengthLabel.setText("height (in mm):");
        widthMathTextField.setText("");
        lengthMathTextField.setText("");
        angleMathTextField.setText("");

        graph.setPointRadius(4);
        graph.updateGraph();
        dropShapeGraph.removeGraph(spatterEllipse);
        repaint();
        updateInitialValues();
    }

    //* Graph listeners
    void graph_mousePressed(MouseEvent e)
    {
        int xMouse = e.getX();
        int yMouse = e.getY();
        int distance1Squared = (xMouse - graph.xMathToPixel(x1)) * (xMouse - graph.xMathToPixel(x1)) + (yMouse - graph.yMathToPixel(y1)) * (yMouse - graph.yMathToPixel(y1));
        if (distance1Squared < 9)
            dragging1 = true;
        int distance2Squared = (xMouse - graph.xMathToPixel(x2)) * (xMouse - graph.xMathToPixel(x2)) + (yMouse - graph.yMathToPixel(y2)) * (yMouse - graph.yMathToPixel(y2));
        if (distance2Squared < 9 && distance1Squared >= 9)
            dragging2 = true;
    }
    void graph_mouseReleased(MouseEvent e)
    {
        dragging1 = false;
        dragging2 = false;
    }
    void graph_mouseDragged(MouseEvent e)
    {
        if (dragging1) {
            oldy1 = y1;
            oldy2 = y2;
            y1 = graph.yPixelToMath(e.getY());
            y2 = oldy2 - oldy1 + y1;
            repaint();
            updateInitialValues();
        }
        if (dragging2) {
            oldx2 = x2;
            oldy2 = y2;
            x2 = graph.xPixelToMath(e.getX());
            y2 = graph.yPixelToMath(e.getY());
            repaint();
            updateInitialValues();
        }
    }
}

//* Button adapters
class SpatterApplication_trackButton_actionAdapter implements java.awt.event.ActionListener
{
    SpatterApplication adaptee;
    SpatterApplication_trackButton_actionAdapter(SpatterApplication adaptee)
    {
        this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e)
    {
        adaptee.trackButton_actionPerformed(e);
    }
}
class SpatterApplication_resetButton_actionAdapter implements java.awt.event.ActionListener
{
    SpatterApplication adaptee;
    SpatterApplication_resetButton_actionAdapter(SpatterApplication adaptee)
    {
        this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e)
    {
        adaptee.resetButton_actionPerformed(e);
    }
}

//* Graph adapters
class SpatterApplication_graph_mouseMotionAdapter extends java.awt.event.MouseMotionAdapter
{
    SpatterApplication adaptee;
    SpatterApplication_graph_mouseMotionAdapter(SpatterApplication adaptee)
    {
        this.adaptee = adaptee;
    }
    public void mouseDragged(MouseEvent e)
    {
        adaptee.graph_mouseDragged(e);
    }
}
class SpatterApplication_graph_mouseAdapter extends java.awt.event.MouseAdapter
{
    SpatterApplication adaptee;
    SpatterApplication_graph_mouseAdapter(SpatterApplication adaptee)
    {
        this.adaptee = adaptee;
    }
    public void mousePressed(MouseEvent e)
    {
        adaptee.graph_mousePressed(e);
    }
    public void mouseReleased(MouseEvent e)
    {
        adaptee.graph_mouseReleased(e);
    }
}
