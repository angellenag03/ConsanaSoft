
package ui;

/**
 *
 * @author pausa
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuPanel extends JPanel {
    private BufferedImage bannerImage;
    private BufferedImage logoImage;
    private String welcomeMessage = "Bienvenido a ConsanaSoft";
    
    public MenuPanel(Image banner, Image logo) {
        // Configuración inicial del panel
        setLayout(new BorderLayout());
        
        // Obtener las imágenes (asumo que tienes una clase ImageLoader con el método getImage)
        bannerImage = (BufferedImage) banner;
        logoImage = (BufferedImage) logo;
        
        // Configurar el banner en la parte superior
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bannerImage != null) {
                    g.drawImage(bannerImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        // El banner ocupará 1/3 de la pantalla
        bannerPanel.setPreferredSize(new Dimension(getWidth(), getHeight()/3));
        add(bannerPanel, BorderLayout.NORTH);
        
        // Panel para el contenido debajo del banner
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar el logo en la esquina superior izquierda
                if (logoImage != null) {
                    int logoWidth = 100; // Ajusta según necesites
                    int logoHeight = 100; // Ajusta según necesites
                    g.drawImage(logoImage, 20, 20, logoWidth, logoHeight, this);
                }
                
                // Dibujar el texto de bienvenida
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.setColor(Color.BLACK);
                FontMetrics fm = g.getFontMetrics();
                int textX = 130; // Ajusta según la posición del logo
                int textY = 50 + fm.getAscent(); // Ajusta para alinear verticalmente con el logo
                g.drawString(welcomeMessage, textX, textY);
            }
        };
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fondo del panel (opcional)
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}