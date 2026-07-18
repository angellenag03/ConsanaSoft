
package ui;

import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import static org.apache.http.client.methods.RequestBuilder.get;
import utils.HTTPManager;


public class DesInstalarConceptoDialog extends JDialog {
    private final String obraId;
    private final Long conceptoId;
    private final ObraPanel obraPanel;
    
    private JTextField cantidadField;
    private JButton aceptarButton;
    
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson = new Gson();
    
    private boolean isInstalar; // true Instalar | false pues no lol
    
    public DesInstalarConceptoDialog(JFrame parent, boolean isInstalar, String obraId, ObraPanel obraPanel, Long conceptoId) {
        super(parent, true);
        this.isInstalar = isInstalar;
        this.obraId = obraId;
        this.conceptoId = conceptoId;
        this.obraPanel = obraPanel;
        setTitle("Ingrese cantidad");
        initComponents();
        setBehavior();
        setupUI();
        pack();
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
        aceptarButton.addActionListener(this::removerConceptos);
        
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
    
    private void removerConceptos(ActionEvent e) {
        if (cantidadField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese una cantidad válida",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadField.getText());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                "Cantidad inválida",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this,
                "La cantidad debe ser mayor que cero",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        HashMap<String, Object> remover = new HashMap<>();
        remover.put("cantidad", cantidad);
        remover.put("conceptoId", conceptoId);
        remover.put("obraId", obraId);

        String endpoint = isInstalar ? "/concepto-obra/instalar" : "/concepto-obra/desinstalar";

        try {
            String response = http.executeRequest(HTTPManager.HttpMethod.PATCH, endpoint, remover);

            if (http.isSatusSuccess()) {
                JOptionPane.showMessageDialog(this,
                    "Operación realizada con éxito",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                obraPanel.actualizarTablas();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    response != null ? response : "No se pudo completar la operación",
                    "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al conectar con el servidor: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Filtro para solo permitir números
    private static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                throws BadLocationException {
            if (string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            if (text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
