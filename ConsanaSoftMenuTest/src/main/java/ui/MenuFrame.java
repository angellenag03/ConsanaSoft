package ui;

import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import utils.ImageLoader;

public class MenuFrame extends JFrame {

    private final OpcionesMenuBar bar;
    private final MenuPanel menuPanel = new MenuPanel("Logo sin fondo.png");
    
    public MenuFrame() throws HeadlessException {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.setSize(800, 600);
            this.setLocationRelativeTo(null);
            this.setTitle("ConsanaSoft");
            this.setIconImage(ImageLoader.getImagen("Logo.png"));
            this.add(menuPanel);
            cargarTema();
            
            bar = new OpcionesMenuBar(this);
            this.setJMenuBar(bar);
            
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);
    }
    
    // Este método carga el tema de windows porque no me gusta 
    // el look and feel por defecto de java
    private void cargarTema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException 
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.err.println(ex.getMessage());
        } 
    }
    
    

}
