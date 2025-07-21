package ui;

import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import utils.HTTPManager;

public class SuministrarMaterialObraDialog extends JDialog {
    
    private final String obraId;
    private final String materialId;
    
    private JTextField cantidadField;
    private JButton aceptarButton;
    
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson = new Gson();
    
    public SuministrarMaterialObraDialog(JFrame parent, String obraId, String materialId, ObraPanel obraPanel) {
        super(parent, true);
        this.obraId = obraId;
        this.materialId = materialId;
        setTitle("Ingrese cantidad");
        initComponents();
        setupUI();
        pack();
        setBehavior();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        cantidadField = new JTextField(10);
        // Filtro para solo números
        PlainDocument doc = (PlainDocument) cantidadField.getDocument();
        doc.setDocumentFilter(new NumericDocumentFilter());
        
        aceptarButton = new JButton("Aceptar");
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel();
        
        formPanel.add(new JLabel("Cantidad:"));
        formPanel.add(cantidadField);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(aceptarButton, BorderLayout.SOUTH);
        
        add(mainPanel);
        setSize(250, 120);
        setResizable(false);
    }
    
    private void setBehavior() {
        // Acción al presionar Aceptar
        aceptarButton.addActionListener(this::suministrarMaterial);
        
        // Cerrar con ESC
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void suministrarMaterial(ActionEvent e) {
        try {
            if (cantidadField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Ingrese una cantidad válida", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int cantidad = Integer.parseInt(cantidadField.getText());
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "La cantidad debe ser mayor que cero", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            HashMap<String, Object> suministro = new HashMap<>();
            suministro.put("obraId", obraId);
            suministro.put("materialId", materialId);
            suministro.put("cantidad", cantidad);

            String response = http.executeRequest(
                HTTPManager.HttpMethod.POST, 
                "/materiales-obra/suministrar", 
                suministro
            );
            
            if (response != null && response.toLowerCase().contains("éxito")) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    response, 
                    "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese solo números enteros", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error de conexión: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Filtro para solo permitir números
    private static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                throws BadLocationException {
            if (string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            if (text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
