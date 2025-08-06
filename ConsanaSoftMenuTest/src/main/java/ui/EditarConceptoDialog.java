package ui;

import dto.ConceptoDTO;
import utils.HTTPManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import com.google.gson.Gson;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.util.HashMap;

public class EditarConceptoDialog extends JDialog {
    
    private final ConceptoDTO concepto;
    private final HTTPManager http = HTTPManager.getInstance();
    
    // Componentes UI
    private JTextField claveField;
    private JTextArea nombreArea;
    private JComboBox<String> unidadBox;
    private JButton aceptarButton, cancelarButton;
    
    public EditarConceptoDialog(ConceptoDTO concepto, JFrame parentFrame) {
        super(parentFrame, "Editar Concepto", true);
        this.concepto = concepto;
        initComponents();
        setupLayout();
        configurarComportamiento();
        
        this.setSize(575, 300); 
        this.setLocationRelativeTo(parentFrame);
    }
    
    private void initComponents() {
        claveField = new JTextField(concepto.getClave(), 25); // Aumenté el ancho de 20 a 25 caracteres
        
        nombreArea = new JTextArea(concepto.getNombre(), 5, 25); // Aumenté el ancho de 20 a 25 caracteres
        nombreArea.setLineWrap(true);
        nombreArea.setWrapStyleWord(true);
        
        unidadBox = new JComboBox<>(new String[]{"MT", "PZA", "LOTE", "KM"});
        unidadBox.setSelectedItem(concepto.getUnidad());
        unidadBox.setPreferredSize(new Dimension(200, 25)); // Aumenté el ancho del ComboBox
        
        aceptarButton = new JButton("Guardar");
        cancelarButton = new JButton("Cancelar");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de formulario con GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 1: Clave
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Clave:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // Permite expansión horizontal
        formPanel.add(claveField, gbc);
        
        // Fila 2: Nombre (con altura flexible)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; // Expansión vertical
        formPanel.add(new JScrollPane(nombreArea), gbc);
        
        // Fila 3: Unidad (altura fija)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0; // Restaura altura fija
        formPanel.add(new JLabel("Unidad:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(unidadBox, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(aceptarButton);
        
        // Ensamblaje final
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    private void configurarComportamiento() {
        cancelarButton.addActionListener(e -> dispose());
        
        aceptarButton.addActionListener(e -> {
            try {
                guardarCambios();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Error al guardar: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        // Cerrar con ESC
        getRootPane().registerKeyboardAction(
            ev -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void guardarCambios() throws IOException {
        try {
                String nombre = nombreArea.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (claveField.equals("")) { 
                JOptionPane.showMessageDialog(this, "La clave no puede estar vacía", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            HashMap<String, Object> nuevoConcepto = new HashMap<>();
            nuevoConcepto.put("id", concepto.getId());
            nuevoConcepto.put("clave", claveField.getText().trim());
            nuevoConcepto.put("nombre", nombre);
            nuevoConcepto.put("unidad", unidadBox.getSelectedItem());

            System.out.println(nuevoConcepto.toString());
            
            http.executeRequest(
                HTTPManager.HttpMethod.PUT,
                "/concepto/update/",
                nuevoConcepto
            );

            JOptionPane.showMessageDialog(this, "Concepto actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}