package ui;

import com.google.gson.Gson;
import dto.ObraDTO;
import utils.HTTPManager;
import java.awt.event.ActionEvent;
import java.io.IOException;
import static java.lang.System.out;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class OpcionesMenuBar extends JMenuBar {
    
    // OBRAS
    private JMenuItem nuevaObra;
    private JMenuItem abrirObra;
    private JMenuItem abrirObraReciente;
    private JMenuItem acercaDe;
   
    // MATERIALES
    private JMenuItem ingresarMaterial;
    private JMenuItem revisarAlmacen;
    
    private final JMenu menuObra;
    private final JMenu menuMaterial;
    private final JMenu menuAcerca;
    private final JFrame parentFrame;  // Referencia al frame padre
    
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson = new Gson();
    
    public OpcionesMenuBar(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        menuObra = new JMenu("Obra");
        menuMaterial = new JMenu("Almacén");
        menuAcerca = new JMenu("Acerca de");
        initComponents();
        this.add(menuObra);
        this.add(menuMaterial);
        this.add(menuAcerca);
    }
    
    private void initComponents() {
        // SETTEO
        
        // OBRAS
        nuevaObra = new JMenuItem("Nueva Obra");
        abrirObra = new JMenuItem("Abrir Obra");
        abrirObraReciente = new JMenuItem("Abrir Obra Reciente");
        
        // MATERIALES
        ingresarMaterial = new JMenuItem("Ingresar Material al Almacén");
        revisarAlmacen = new JMenuItem("Revisar Almacén");
        
        acercaDe = new JMenuItem("Acerca de");
        
        // Hotkeys
        
        // OBRAS
        nuevaObra.setAccelerator(KeyStroke.getKeyStroke("control N"));
        abrirObra.setAccelerator(KeyStroke.getKeyStroke("control O"));
        abrirObraReciente.setAccelerator(KeyStroke.getKeyStroke("control R"));
        
        // ACERCA DE
        acercaDe.setAccelerator(KeyStroke.getKeyStroke("F1"));
        
        // AñADIDOS AL MENUBAR
        // OBRAS
        menuObra.add(nuevaObra);
        menuObra.addSeparator();
        menuObra.add(abrirObra);
        menuObra.add(abrirObraReciente);
        
        // MATERIALES
        menuMaterial.add(ingresarMaterial);
        menuMaterial.add(revisarAlmacen);
        
        // ACERCA DE
        menuAcerca.add(acercaDe);
        
        nuevaObra.addActionListener(this::nuevaObraDialog);
        abrirObra.addActionListener(this::abrirObraJDialog);
        acercaDe.addActionListener(this::mostrarAcercaDe);
        abrirObraReciente.addActionListener(this::abrirObraRecienteDialog);
        ingresarMaterial.addActionListener(this::ingresarMaterial);
        revisarAlmacen.addActionListener(this::revisarAlmacen);
    }
    
    private void mostrarAcercaDe(ActionEvent e) {
        JOptionPane.showMessageDialog(parentFrame, 
            "CONSANA Soft\nVersión 1.2.31\n© Angel Sanchez 2025", 
            "Acerca de", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void nuevaObraDialog(ActionEvent e) {
        NuevaObraDialog dialog = new NuevaObraDialog(parentFrame);
        dialog.setVisible(true);
    }
    
    private void abrirObraJDialog(ActionEvent e) {
        AbrirObraDialog dialog = new AbrirObraDialog(parentFrame);
        dialog.setVisible(true);
    }
    
    private void abrirObraRecienteDialog(ActionEvent e) {
        try {
            String response = http.executeRequest("/obra/ultima");
            ObraDTO obra = gson.fromJson(response, ObraDTO.class);

            SwingUtilities.invokeLater(() -> {
                parentFrame.getContentPane().removeAll();
                parentFrame.add(new ObraPanel(parentFrame, obra));
                parentFrame.revalidate();
                parentFrame.repaint();
                parentFrame.setTitle("ConsanaSoft: "+obra.getNombre());
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                        "Error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ingresarMaterial(ActionEvent e) {
        NuevoMaterialDialog dialog = new NuevoMaterialDialog(parentFrame);
        dialog.setVisible(true);
    }
    
    private void revisarAlmacen(ActionEvent e) {
        AlmacenDialog dialog = new AlmacenDialog(parentFrame);
        dialog.setVisible(true);
    }
}
