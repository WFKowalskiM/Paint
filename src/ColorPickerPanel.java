import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorPickerPanel extends JPanel {
    private Color selectedColor = Color.BLACK;

    public ColorPickerPanel() {
        setPreferredSize(new Dimension(400, 100));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int width = getWidth();
                float hue = (float) x / width; // Map x to hue
                selectedColor = Color.getHSBColor(hue, 1.0f, 1.0f);
                repaint();
            }
        });
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Create a gradient from red to violet across the panel
        for (int i = 0; i < getWidth(); i++) {
            float hue = (float) i / getWidth();
            g.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
            g.drawLine(i, 0, i, getHeight());
        }

        // Draw a rectangle to show the selected color
        g.setColor(selectedColor);
        g.fillRect(0, getHeight() - 20, getWidth(), 20);
    }
}
