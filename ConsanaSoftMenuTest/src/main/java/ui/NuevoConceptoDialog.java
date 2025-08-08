package ui;

import dto.MaterialOutputDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import utils.HTTPManager;
import tables.MaterialConceptoTable;

public class NuevoConceptoDialog extends JDialog {
    private JLabel claveLabel, nombreLabel, unidadLabel;
    private JTextField claveField;
    private JTextArea nombreArea;
    private JTextField unidadField;
    
    private JButton crearButton;
    private JButton cancelarButton;
    private JButton agregarMaterialButton;
    private JButton removerMaterialButton;
    
    private MaterialConceptoTable materialesTable;
    private JFrame parentFrame;
    
    private final HTTPManager http = HTTPManager.getInstance();
    
    public NuevoConceptoDialog(JFrame parentFrame) {
        super(parentFrame, "Nuevo Concepto", true);
        
        this.setSize(800, 500); 
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
        
        initComponents();
        setupLayout();
        setBehavior();
    }
    
    private void initComponents() { 
        claveLabel = new JLabel("Clave:");
        nombreLabel = new JLabel("Nombre:");
        unidadLabel = new JLabel("Unidad:");
        
        claveField = new JTextField(20);
        nombreArea = new JTextArea(3, 20);
        nombreArea.setLineWrap(true);
        nombreArea.setWrapStyleWord(true);
        nombreArea.setBackground(new Color(0x1F2B30));
        nombreArea.setForeground(Color.WHITE);
        unidadField = new JTextField(10);
        
        crearButton = new JButton("Crear");
        cancelarButton = new JButton("Cancelar");
        agregarMaterialButton = new JButton("Añadir Material");
        removerMaterialButton = new JButton("Remover Material");
        
        materialesTable = new MaterialConceptoTable();
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de detalles del concepto
        JPanel conceptPanel = new JPanel();
        conceptPanel.setLayout(new BoxLayout(conceptPanel, BoxLayout.Y_AXIS));
        conceptPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Detalles del Concepto"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Panel para clave
        JPanel clavePanel = new JPanel(new BorderLayout(5, 5));
        clavePanel.setOpaque(false);
        clavePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        clavePanel.add(claveLabel, BorderLayout.NORTH);
        clavePanel.add(claveField, BorderLayout.CENTER);

        // Panel para nombre
        JPanel nombrePanel = new JPanel(new BorderLayout(5, 5));
        nombrePanel.setOpaque(false);
        nombrePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        nombrePanel.add(nombreLabel, BorderLayout.NORTH);
        nombrePanel.add(new JScrollPane(nombreArea), BorderLayout.CENTER);

        // Panel para unidad
        JPanel unidadPanel = new JPanel(new BorderLayout(5, 5));
        unidadPanel.setOpaque(false);
        unidadPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        unidadPanel.add(unidadLabel, BorderLayout.NORTH);
        unidadPanel.add(unidadField, BorderLayout.CENTER);

        // Agregar subpaneles al panel de concepto
        conceptPanel.add(clavePanel);
        conceptPanel.add(nombrePanel);
        conceptPanel.add(unidadPanel);

        // Panel de materiales
        JPanel materialsPanel = new JPanel(new BorderLayout(10, 10));
        materialsPanel.setBorder(BorderFactory.createTitledBorder("Materiales"));

        JScrollPane tableScrollPane = new JScrollPane(materialesTable);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(agregarMaterialButton);
        buttonsPanel.add(removerMaterialButton);

        materialsPanel.add(tableScrollPane, BorderLayout.CENTER);
        materialsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Panel de botones inferiores
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(cancelarButton);
        bottomPanel.add(crearButton);

        // Panel central para alinear ambos paneles (conceptos y materiales)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.add(conceptPanel);
        centerPanel.add(materialsPanel);

        // Agregar todo al panel principal
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    
    // Resto del código (setBehavior y crear) permanece igual
    private void setBehavior() {
        crearButton.addActionListener(this::crear);
        cancelarButton.addActionListener(e -> dispose());
        
        agregarMaterialButton.addActionListener(e -> {
            SeleccionarMaterialDialog dialog = new SeleccionarMaterialDialog(parentFrame);
            dialog.setVisible(true);
            
            Long id = dialog.getIdSeleccionado();
            String clave = dialog.getClaveSeleccionada();
            String nombre = dialog.getNombreSeleccionado();
            String unidad = dialog.getUnidadSeleccionada();
            
            if (nombre != null && unidad != null) {
                MaterialOutputDTO materiales = new MaterialOutputDTO(id, clave, nombre, unidad, 0.0);
                materialesTable.setMaterial(materiales);
            }
        });
        
        removerMaterialButton.addActionListener(e -> {
            int selectedRow = materialesTable.getSelectedRow();
            if (selectedRow >= 0) {
                materialesTable.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un material para remover", 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    private void crear(ActionEvent e) {
        try {
            HashMap<String, Object> concepto = new HashMap<>();

            concepto.put("clave", claveField.getText());
            concepto.put("nombre", nombreArea.getText());
            concepto.put("unidad", unidadField.getText());

            // 2. Procesar materiales (ajusta según lo que devuelve materialesTable.getMateriales())
            List<Map<String, Object>> materiales = materialesTable.getMateriales();
            if (materiales != null && !materiales.isEmpty()) {
                concepto.put("materiales", materiales.stream()
                        .map(m -> Map.of(
                                "materialId", m.get("id"),  // Asegúrate que coincida con el DTO del backend
                                "cantidad", m.get("cantidad")
                        ))
                        .collect(Collectors.toList()));
            }

            http.executeRequest(HTTPManager.HttpMethod.POST, "/concepto", concepto);
            
            JOptionPane.showMessageDialog(this, 
                    "Concepto creado con éxito!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al crear el concepto: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}