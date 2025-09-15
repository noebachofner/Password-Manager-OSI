import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Hilfklasse zum Erstellen und Setzen des App-Icons
 */
public class IconCreator {

    /**
     * Erstellt das grüne App-Icon programmatisch
     */
    public static ImageIcon createAppIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Anti-Aliasing für glatte Kanten
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Grüner Kreis-Hintergrund
        g2d.setColor(new Color(34, 139, 34)); // Waldgrün
        g2d.fillOval(2, 2, size-4, size-4);

        // Weißes Pfeil/Chevron-Symbol
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(size/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int centerX = size / 2;
        int centerY = size / 2;
        int symbolSize = size / 3;

        // Oberer Pfeil/Chevron
        int[] xPoints1 = {
                centerX - symbolSize/2,
                centerX,
                centerX + symbolSize/2
        };
        int[] yPoints1 = {
                centerY - symbolSize/4,
                centerY - symbolSize/2,
                centerY - symbolSize/4
        };

        // Unterer Pfeil/Chevron (verschoben)
        int[] xPoints2 = {
                centerX - symbolSize/3,
                centerX + symbolSize/4,
                centerX + symbolSize/2
        };
        int[] yPoints2 = {
                centerY + symbolSize/4,
                centerY,
                centerY + symbolSize/2
        };

        // Zeichne die Pfeil-Linien
        for (int i = 0; i < xPoints1.length - 1; i++) {
            g2d.drawLine(xPoints1[i], yPoints1[i], xPoints1[i+1], yPoints1[i+1]);
        }

        for (int i = 0; i < xPoints2.length - 1; i++) {
            g2d.drawLine(xPoints2[i], yPoints2[i], xPoints2[i+1], yPoints2[i+1]);
        }

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * Setzt das Icon für ein JFrame
     */
    public static void setAppIcon(JFrame frame) {
        try {
            // Erstelle Icons in verschiedenen Größen für verschiedene Kontexte
            java.util.List<Image> icons = new java.util.ArrayList<>();
            icons.add(createAppIcon(16).getImage());  // Taskleiste
            icons.add(createAppIcon(24).getImage());  // Kleine Icons
            icons.add(createAppIcon(32).getImage());  // Standard
            icons.add(createAppIcon(48).getImage());  // Desktop
            icons.add(createAppIcon(64).getImage());  // Große Icons
            icons.add(createAppIcon(128).getImage()); // Sehr große Icons

            frame.setIconImages(icons);

        } catch (Exception e) {
            System.err.println("Konnte App-Icon nicht setzen: " + e.getMessage());
            // Fallback: Verwende Standard-Icon
        }
    }

    /**
     * Erstellt ein einfaches Text-basiertes Icon als Fallback
     */
    public static ImageIcon createSimpleIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Grüner Hintergrund
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRoundRect(0, 0, size, size, size/8, size/8);

        // Weißer Text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size/3));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "🔐";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);

        g2d.dispose();
        return new ImageIcon(image);
    }
}