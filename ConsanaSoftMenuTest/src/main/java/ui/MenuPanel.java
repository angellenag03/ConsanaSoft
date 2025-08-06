package ui;

import utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.time.LocalTime;

public class MenuPanel extends JPanel {
    private String nombreUsuario;
    private BufferedImage logoImage;
    private String saludo;
    private String welcomeMessage;
    private final String logoPath = "Logo sin fondo.png";

    private JFrame parentFrame;
    
    public MenuPanel(JFrame parentFrame) {
        this.setLayout(new BorderLayout());
        
        this.parentFrame = parentFrame;
        
        this.saludo = LocalTime.now().isBefore(LocalTime.NOON) ? "Buenos días, " : "Buenas tardes, ";
        this.nombreUsuario = System.getProperty("user.name");
        this.welcomeMessage = saludo+nombreUsuario;
        // Cargar logo con ImageLoader
        Image logo = ImageLoader.getImagen(logoPath);
        logoImage = convertirABufferedImage(logo);

        // Panel superior: banner con gradiente + logo + texto
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // Crear gradiente para el fondo del banner
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0x455b66), 
                        getWidth(), 0, new Color(0x304047));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                int panelHeight = getHeight();

                // Tamaño y posición del logo (125x125, un 25% más grande)
                int logoWidth = 265;
                int logoHeight = 265;
                int logoX = 20;
                int logoY = (panelHeight - logoHeight) / 2;

                if (logoImage != null) {
                    g2d.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, this);
                }

                // Texto de bienvenida
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = logoX + logoWidth + 20;
                int textY = (panelHeight - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(welcomeMessage, textX, textY);
            }
        };

        bannerPanel.setPreferredSize(new Dimension(getWidth(), 150));
        add(bannerPanel, BorderLayout.NORTH);

        // Panel de contenido (vacío por ahora)
        JPanel contentPanel = new JPanel();
        contentPanel.add(new BotonesPanel(parentFrame));
//        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
    }

    private class BotonesPanel extends JPanel {
        private JButton existenciasButton;
        private JFrame parentFrame;
        
        public BotonesPanel(JFrame parentFrame) {
            this.parentFrame = parentFrame;
            this.setLayout(new FlowLayout());
            
            initComp();
            setBehavior();
            
            this.add(existenciasButton);
        }
        
        private void initComp() {
            existenciasButton = new JButton("Revisar Existencias");
        }
        
        private void setBehavior() {
            existenciasButton.addActionListener(this::existencias);
        }
        
        private void existencias(ActionEvent e) {
            AlmacenExistenciasDialog dialog = new AlmacenExistenciasDialog(parentFrame);
            dialog.setVisible(true);
        }
    }
    
    private BufferedImage convertirABufferedImage(Image img) {
        if (img instanceof BufferedImage) return (BufferedImage) img;
        if (img == null) return null;

        BufferedImage bimage = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = bimage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return bimage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
