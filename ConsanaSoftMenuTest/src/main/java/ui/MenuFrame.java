package ui;
import com.formdev.flatlaf.*;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import javax.swing.*;
import utils.DebugConsole;
import utils.ImageLoader;

import java.awt.Color;
import java.awt.Insets;

public class MenuFrame extends JFrame {
    private final OpcionesMenuBar bar;
    private final MenuPanel menuPanel;
    
    public MenuFrame() throws HeadlessException {
        super("ConsanaSoft");
        
        // Configuración inicial del frame
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setIconImage(ImageLoader.getImagen("Logo.png"));
        // Inicializar consola ANTES de cargar tema
        configurarConsolaDebug();
        cargarTema();
        
        this.menuPanel = new MenuPanel(this);
        this.add(menuPanel);
        
        bar = new OpcionesMenuBar(this);
        this.setJMenuBar(bar);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        // Log de inicio después de que todo esté configurado
        DebugConsole.log("MenuFrame inicializado correctamente");
        DebugConsole.log("Presiona F10 para alternar la consola de depuración");
    }
    
    private void configurarConsolaDebug() {
        // Inicializar la consola
        DebugConsole.init(this);
        
        // Configurar atajo F10 con mayor prioridad
        KeyStroke f10Key = KeyStroke.getKeyStroke("f10");
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();
        
        inputMap.put(f10Key, "toggleDebugConsole");
        actionMap.put("toggleDebugConsole", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (DebugConsole.isVisible()) {
                        DebugConsole.log("→ Consola ocultada por F10");
                        DebugConsole.hideConsole();
                    } else {
                        DebugConsole.showConsole();
                        DebugConsole.log("→ Consola mostrada por F10");
                    }
                } catch (Exception ex) {
                    System.err.println("Error al alternar consola: " + ex.getMessage());
                }
            }
        });
        
        // Opcional: También agregar Ctrl+Shift+D como atajo alternativo
        KeyStroke ctrlShiftD = KeyStroke.getKeyStroke("ctrl shift D");
        inputMap.put(ctrlShiftD, "toggleDebugConsole");
        
        DebugConsole.log("Atajos de consola configurados: F10, Ctrl+Shift+D");
    }
    
    private void cargarTema() {
        try {
//            UIManager.setLookAndFeel(new FlatMacLightLaf());
            UIManager.put("Table.background", new Color(240,240,250));
            UIManager.put("Component.arrowType", "triangle");
            // SCROLLBAR
            UIManager.put( "ScrollBar.trackArc", 999 );
            UIManager.put( "ScrollBar.thumbArc", 999 );
            UIManager.put( "ScrollBar.trackInsets", new Insets( 2, 4, 2, 4 ) );
            UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
            // TEXTCOMPONENT (JTextField)
            UIManager.put("TextComponent.arc", 12);

            FlatLightLaf.setup();
            SwingUtilities.updateComponentTreeUI(this);
            DebugConsole.log("✓ Tema del sistema cargado correctamente");
        } catch (Exception ex) {
            DebugConsole.logError("✗ Error al cargar el tema: " + ex.getMessage());
            DebugConsole.logException(ex);
        } 
    }
    
    @Override
    public void dispose() {
        // Limpiar recursos de la consola al cerrar
        DebugConsole.log("Cerrando aplicación...");
        DebugConsole.cleanup();
        super.dispose();
    }
}
