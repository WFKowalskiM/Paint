import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class Main extends JFrame {

    private int startX, startY, endX, endY;
    private String currentShape = "line";
    private Color currentColor = Color.BLACK;
    private final JTextField rField, gField, bField;
    private final JTextField cField, mField, yField, kField;
    private final JTextField x1Field, y1Field, x2Field, y2Field, widthField, heightField;
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private Shape selectedShape = null;
    private Point dragStart;
    private final ColorPickerPanel colorPickerPanel;

    public Main() {
        setTitle("Drawing Application");
        setSize(1400, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawingPanel drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        String[] shapesList = {"line", "rectangle", "circle"};
        JComboBox<String> shapeSelector = new JComboBox<>(shapesList);
        shapeSelector.addActionListener(e -> currentShape = (String) shapeSelector.getSelectedItem());
        controlPanel.add(shapeSelector);

        rField = new JTextField(3);
        gField = new JTextField(3);
        bField = new JTextField(3);
        controlPanel.add(new JLabel("R:"));
        controlPanel.add(rField);
        controlPanel.add(new JLabel("G:"));
        controlPanel.add(gField);
        controlPanel.add(new JLabel("B:"));
        controlPanel.add(bField);

        JButton setRGBButton = new JButton("Set RGB");
        setRGBButton.addActionListener(e -> updateColorFromRGB());
        controlPanel.add(setRGBButton);

        cField = new JTextField(5);
        mField = new JTextField(5);
        yField = new JTextField(5);
        kField = new JTextField(5);
        controlPanel.add(new JLabel("C:"));
        controlPanel.add(cField);
        controlPanel.add(new JLabel("M:"));
        controlPanel.add(mField);
        controlPanel.add(new JLabel("Y:"));
        controlPanel.add(yField);
        controlPanel.add(new JLabel("K:"));
        controlPanel.add(kField);

        JButton setCMYKButton = new JButton("Set CMYK");
        setCMYKButton.addActionListener(e -> updateColorFromCMYK());
        controlPanel.add(setCMYKButton);

        x1Field = new JTextField(5);
        y1Field = new JTextField(5);
        x2Field = new JTextField(5);
        y2Field = new JTextField(5);
        widthField = new JTextField(5);
        heightField = new JTextField(5);
        controlPanel.add(new JLabel("X1:"));
        controlPanel.add(x1Field);
        controlPanel.add(new JLabel("Y1:"));
        controlPanel.add(y1Field);
        controlPanel.add(new JLabel("X2:"));
        controlPanel.add(x2Field);
        controlPanel.add(new JLabel("Y2:"));
        controlPanel.add(y2Field);
        controlPanel.add(new JLabel("Width:"));
        controlPanel.add(widthField);
        controlPanel.add(new JLabel("Height:"));
        controlPanel.add(heightField);

        JButton setShapeParamsButton = new JButton("Update Shape");
        setShapeParamsButton.addActionListener(e -> updateShapeParams());
        controlPanel.add(setShapeParamsButton);

        add(controlPanel, BorderLayout.NORTH);

        colorPickerPanel = new ColorPickerPanel();
        add(colorPickerPanel, BorderLayout.SOUTH);
        JButton applyColorButton = new JButton("Apply Selected Color");
        applyColorButton.addActionListener(e -> {
            currentColor = colorPickerPanel.getSelectedColor();
            updateTextFieldsColor(currentColor);
        });
        controlPanel.add(applyColorButton);
        add(controlPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    class DrawingPanel extends JPanel {

        public DrawingPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startX = e.getX();
                    startY = e.getY();

                    for (Shape shape : shapes) {
                        if (shape.contains(startX, startY)) {
                            selectedShape = shape;
                            dragStart = e.getPoint();
                            updateTextFieldsPos(shape);
                            return;
                        }
                    }
                    selectedShape = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (selectedShape == null) {
                        endX = e.getX();
                        endY = e.getY();
                        Shape newShape = createShape(startX, startY, endX, endY, currentShape, currentColor);
                        shapes.add(newShape);
                    }
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (selectedShape != null && dragStart != null) {
                        if (isResizing(e.getPoint())) {
                            resizeShape(e.getX(), e.getY());
                        } else {
                            int dx = e.getX() - dragStart.x;
                            int dy = e.getY() - dragStart.y;
                            selectedShape.translate(dx, dy);
                            dragStart = e.getPoint();
                        }
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Shape shape : shapes) {
                shape.draw(g);
            }
        }

        private Shape createShape(int x1, int y1, int x2, int y2, String type, Color color) {
            return switch (type) {
                case "line" -> new Line(x1, y1, x2, y2, color);
                case "rectangle" -> new Rectangle(x1, y1, x2, y2, color);
                case "circle" -> new Circle(x1, y1, x2, y2, color);
                default -> null;
            };
        }
    }

    private void updateShapeParams() {
        if (selectedShape != null) {
            int x1 = Integer.parseInt(x1Field.getText());
            int y1 = Integer.parseInt(y1Field.getText());
            int x2 = Integer.parseInt(x2Field.getText());
            int y2 = Integer.parseInt(y2Field.getText());
            selectedShape.updateParams(x1, y1, x2, y2);
            repaint();
        }
    }

    private boolean isResizing(Point p) {
        return selectedShape != null && selectedShape.isInResizeArea(p);
    }

    private void resizeShape(int x, int y) {
        selectedShape.resize(x, y);
    }
    private void updateTextFieldsPos(Shape shape){
        x1Field.setText(String.valueOf(shape.getX1()));
        y1Field.setText(String.valueOf(shape.getY1()));
        x2Field.setText(String.valueOf(shape.getX2()));
        y2Field.setText(String.valueOf(shape.getY2()));
        widthField.setText(String.valueOf(shape.getWidth()));
        heightField.setText(String.valueOf(shape.getHeight()));

    }
    private void updateTextFieldsColor(Color color) {
        // Update position and size fields

        // Get RGB values from the selected shape's color
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        // Update RGB fields
        rField.setText(String.valueOf(r));
        gField.setText(String.valueOf(g));
        bField.setText(String.valueOf(b));

        // Calculate CMYK values from RGB
        double rPercent = r / 255.0;
        double gPercent = g / 255.0;
        double bPercent = b / 255.0;

        double k = 1 - Math.max(rPercent, Math.max(gPercent, bPercent));
        double c = (1 - rPercent - k) / (1 - k);
        double m = (1 - gPercent - k) / (1 - k);
        double y = (1 - bPercent - k) / (1 - k);

        // Handle edge case when the color is black (k=1)
        if (r == 0 && g == 0 && b == 0) {
            c = 0;
            m = 0;
            y = 0;
            k = 1;
        }

        // Update CMYK fields
        cField.setText(String.format("%.0f", c * 100));
        mField.setText(String.format("%.0f", m * 100));
        yField.setText(String.format("%.0f", y * 100));
        kField.setText(String.format("%.0f", k * 100));
    }

    abstract static class Shape {
        Color color;

        Shape(Color color) {
            this.color = color;
        }

        abstract void draw(Graphics g);

        abstract void translate(int dx, int dy);

        abstract boolean contains(int x, int y);

        abstract void resize(int x, int y);

        abstract boolean isInResizeArea(Point p);

        abstract void updateParams(int x1, int y1, int x2, int y2);

        abstract int getX1();
        abstract int getY1();
        abstract int getX2();
        abstract int getY2();
        abstract int getWidth();
        abstract int getHeight();
    }

    static class Line extends Shape {
        private int x1, y1, x2, y2;
        public Line(int x1, int y1, int x2, int y2, Color color) {
            super(color);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.drawLine(x1, y1, x2, y2);
        }
        @Override
        public void translate(int dx, int dy) {
            this.x1 += dx;
            this.y1 += dy;
            this.x2 += dx;
            this.y2 += dy;
        }
        @Override
        public boolean contains(int x, int y) {
            return (Math.abs((x2 - x1) * (y - y1) - (x - x1) * (y2 - y1)) < 1000);
        }
        @Override
        public void resize(int x, int y) {
            this.x2 = x;
            this.y2 = y;
        }
        @Override
        public boolean isInResizeArea(Point p) {
            return (Math.abs(p.x - x2) < 5 && Math.abs(p.y - y2) < 5);
        }
        @Override
        public void updateParams(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        @Override
        public int getX1() { return x1; }
        @Override
        public int getY1() { return y1; }
        @Override
        public int getX2() { return x2; }
        @Override
        public int getY2() { return y2; }
        @Override
        public int getWidth() { return Math.abs(x2 - x1); }
        @Override
        public int getHeight() { return Math.abs(y2 - y1); }
    }
    static class Rectangle extends Shape {
        private int x1, x2, y1, y2, width, height;
        public Rectangle(int x1, int y1, int x2, int y2, Color color) {
            super(color);
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.width = Math.abs(x2 - x1);
            this.height = Math.abs(y2 - y1);
        }
        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.drawRect(Math.min(x2, x1), Math.min(y2, y1), width, height);
        }
        @Override
        public void translate(int dx, int dy) {
            this.x1 += dx;
            this.y1 += dy;
        }
        @Override
        public boolean contains(int x, int y) {
            return x >= x1 && x <= (x1 + width) && y >= y1 && y <= (y1 + height);
        }
        @Override
        public void resize(int x, int y) {
            this.width = Math.abs(x - x1);
            this.height = Math.abs(y - y1);
        }
        @Override
        public boolean isInResizeArea(Point p) {
            return (Math.abs(p.x - (x1 + width)) < 5 && Math.abs(p.y - (y1 + height)) < 5);
        }
        @Override
        public void updateParams(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.width = Math.abs(x2 - x1);
            this.height = Math.abs(y2 - y1);
        }
        @Override
        public int getX1() { return x1; }
        @Override
        public int getY1() { return y1; }
        @Override
        public int getX2() { return x1 + width; }
        @Override
        public int getY2() { return y1 + height; }
        @Override
        public int getWidth() { return width; }
        @Override
        public int getHeight() { return height; }
    }
    static class Circle extends Shape {
        private int x1, y1, radius;
        public Circle(int x1, int y1, int x2, int y2, Color color) {
            super(color);
            this.x1 = x1;
            this.y1 = y1;
            this.radius = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        }
        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.drawOval(x1 - radius, y1 - radius, radius * 2, radius * 2);
        }
        @Override
        public void translate(int dx, int dy) {
            this.x1 += dx;
            this.y1 += dy;
        }
        @Override
        public boolean contains(int x, int y) {
            int dist = (int) Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
            return dist <= radius;
        }
        @Override
        public void resize(int x, int y) {
            this.radius = (int) Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
        }
        @Override
        public boolean isInResizeArea(Point p) {
            int dist = (int) Math.sqrt(Math.pow(p.x - x1, 2) + Math.pow(p.y - y1, 2));
            return Math.abs(dist - radius) < 5;
        }
        @Override
        public void updateParams(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.radius = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        }
        @Override
        public int getX1() { return x1 - radius; }
        @Override
        public int getY1() { return y1 - radius; }
        @Override
        public int getX2() { return x1 + radius; }
        @Override
        public int getY2() { return y1 + radius; }
        @Override
        public int getWidth() { return radius * 2; }
        @Override
        public int getHeight() { return radius * 2; }
    }


    private void updateColorFromRGB() {
        try {
            int r = Integer.parseInt(rField.getText());
            int g = Integer.parseInt(gField.getText());
            int b = Integer.parseInt(bField.getText());
            currentColor = new Color(r, g, b);
            updateTextFieldsColor(currentColor);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid RGB values.");
        }
    }

    private void updateColorFromCMYK() {
        try {
            double c = Double.parseDouble(cField.getText()) / 100;
            double m = Double.parseDouble(mField.getText()) / 100;
            double y = Double.parseDouble(yField.getText()) / 100;
            double k = Double.parseDouble(kField.getText()) / 100;
            int r = (int) ((1 - c) * (1 - k) * 255);
            int g = (int) ((1 - m) * (1 - k) * 255);
            int b = (int) ((1 - y) * (1 - k) * 255);
            currentColor = new Color(r, g, b);
            updateTextFieldsColor(currentColor);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid CMYK values.");
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
