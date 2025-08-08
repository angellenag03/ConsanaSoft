
package ui;

import dto.MaterialDTO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import utils.HTTPManager;

public class EditarMaterialDialog extends JDialog {
    private final MaterialDTO material;
    
    private JLabel claveLabel, nombreLabel, unidadLabel;
    private JTextField claveField, nombreField, unidadField;
    private JButton cancelarButton, guardarButton;
    
    private final HTTPManager http = HTTPManager.getInstance();
    
    public EditarMaterialDialog(MaterialDTO material, JFrame parentFrame) {
        super(parentFrame, "Editar Material", true);
        this.material = material;
        
        initComponents();
        setupLayout();
        setupBehavior();
        
        this.setSize(400, 350);
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }
    
    private void initComponents() {
        claveLabel = new JLabel("Clave");
        nombreLabel = new JLabel("Nombre:");
        unidadLabel = new JLabel("Unidad");
        
        claveField = new JTextField(material.getClave(), 25);
        nombreField = new JTextField(material.getNombre(), 25);
        unidadField = new JTextField(material.getUnidad(), 25);
        
        cancelarButton = new JButton("Cancelar");
        guardarButton = new JButton("Guardar");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new  BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel datosPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        datosPanel.add(claveLabel);
        datosPanel.add(claveField);
        datosPanel.add(nombreLabel);
        datosPanel.add(nombreField);
        datosPanel.add(unidadLabel);
        datosPanel.add(unidadField);
        
        centerPanel.add(datosPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(guardarButton);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    private void setupBehavior() {
        cancelarButton.addActionListener(e -> {dispose();});
        guardarButton.addActionListener(this::guardarCambios);
    }
    
    private void guardarCambios(ActionEvent e) {
        try {
            if(!validarCampos()) {
                dispose();
                return;
            } else {
                HashMap<String, Object> nuevoMaterial = new HashMap<>();
                nuevoMaterial.put("id", material.getId());
                nuevoMaterial.put("clave", claveField.getText().trim());
                nuevoMaterial.put("nombre", nombreField.getText().trim());
                nuevoMaterial.put("unidad", unidadField.getText().trim());

                http.executeRequest(HTTPManager.HttpMethod.PUT,
                        "/material/update/", nuevoMaterial);

                JOptionPane.showMessageDialog(this, "Material actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private boolean validarCampos() {
        if(nombreField.getText().isEmpty() || unidadField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar: Favor de Llenar los campos", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            
            return false;
        }
        
        if(nombreField.getText().equals(material.getNombre())
                && unidadField.getText().equals(material.getUnidad())) {
            JOptionPane.showMessageDialog(this, 
                "No se ha hecho ningún cambio", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            
            return false;
        }                
        
        return true;
    }
}
