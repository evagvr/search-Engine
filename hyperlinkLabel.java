import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class hyperlinkLabel extends JLabel {
    private String url;

    public hyperlinkLabel(String title, String url) {
        this.url = url;
        
        setText("<html><a href=\"" + url + "\">" + title + "</a></html>");

        setForeground(Color.BLUE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText(url); 
        addMouseListener(new LinkMouseListener());
    }

    private class LinkMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    JOptionPane.showMessageDialog(hyperlinkLabel.this,
                        "Browser cannot be accessed at the moment copy it manuallyS: " + url,
                        "Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setForeground(new Color(133, 39, 54));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setForeground(Color.BLUE);
        }
    }
}
