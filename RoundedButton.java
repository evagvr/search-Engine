import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Shape shape;
    private Color originalBackground;

    public RoundedButton(String label) {
        super(label);
        setBackground(new Color(151,146,203));
        setForeground(new Color(65, 75,158));
        setFont(new Font("Arial", Font.BOLD, 15));
        setBorder(null);
        setPreferredSize(new Dimension(70, 20));
        this.originalBackground = getBackground();
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40)); 

        
    
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(144, 135, 201));
                setForeground(new Color(65, 75,158)); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(originalBackground);
                setForeground(new Color(65, 75,158)); 
            }
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(new Color(140, 133, 199)); 
                setForeground(new Color(35, 45, 128));
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (contains(e.getX(), e.getY())) {
                    setBackground(new Color(130,126,153));
                    setForeground(new Color(65, 75,158)); 
                } else {
                    setBackground(originalBackground);
                    setForeground(new Color(65, 75,158)); 
                }
            }
        });
    }
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        g.setColor(getForeground());
        g.setFont(getFont());

        FontMetrics fm = g.getFontMetrics();
        String text = getText();

        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        
        g.drawString(text, x, y);
    }

    protected void paintBorder(Graphics g) {
        g.setColor(getForeground()); 
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
    }
    
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 5, 5);
        }
        return shape.contains(x, y);
    }
}