package ui;

import tables.MaterialesTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SeleccionarMaterialDialog extends JDialog {
    private MaterialesTable materialesTable;
    private JTextField buscarField;
    private JButton buscarButton;
    private JButton seleccionarButton;
    private JButton cancelarButton;
    
    private Long idSeleccionado;
    private String claveSeleccionada;
    private String nombreSeleccionado;
    private String unidadSeleccionada;

    public SeleccionarMaterialDialog(JFrame parentFrame) {
        super(parentFrame, "Seleccionar Material Existente", true);
        initComponents();
        materialesTable = new MaterialesTable();
        setupLayout();
        configurarComportamiento();
        
        this.setSize(500, 400);
        this.setLocationRelativeTo(parentFrame);
    }

    // abre directamente buscando coincidencias
    public SeleccionarMaterialDialog(JFrame parentFrame, String query) {
        super(parentFrame, "Seleccionar Material Existente", true);
        initComponents();
        materialesTable = new MaterialesTable(query);
        setupLayout();
        configurarComportamiento();
        
        this.setSize(500, 400);
        this.setLocationRelativeTo(parentFrame);
    }
    
    private void initComponents() {
        buscarField = new JTextField(20);
        buscarButton = new JButton("Buscar");
        seleccionarButton = new JButton("Seleccionar");
        cancelarButton = new JButton("Cancelar");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Nombre del material:"));
        searchPanel.add(buscarField);
        searchPanel.add(buscarButton);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(seleccionarButton);
        
        // Tabla con scroll
        JScrollPane scrollPane = new JScrollPane(materialesTable);
        
        // Ensamblar componentes
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    private void configurarComportamiento() {
        // Buscar materiales por nombre
        buscarButton.addActionListener(this::buscar);
        buscarField.addActionListener(this::buscar);
        
        // Seleccionar material
        seleccionarButton.addActionListener(e -> {
            int filaSeleccionada = materialesTable.getSelectedRow();
            if (filaSeleccionada >= 0) {
                idSeleccionado = (Long) materialesTable.getValueAt(filaSeleccionada, 0);
                claveSeleccionada = (String) materialesTable.getValueAt(filaSeleccionada, 1);
                nombreSeleccionado = (String) materialesTable.getValueAt(filaSeleccionada, 2); // Columna "Nombre"
                unidadSeleccionada = (String) materialesTable.getValueAt(filaSeleccionada, 3); // Columna "Unidad"
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Selecciona un material de la tabla", 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Cancelar
        cancelarButton.addActionListener(e -> dispose());
        
        // Doble click para seleccionar
        materialesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    seleccionarButton.doClick();
                }
            }
        });
    }
    
    private void buscar(ActionEvent e) {
        String nombre = buscarField.getText().trim();
        if (!nombre.isEmpty()) {
            materialesTable.cargarDatosPorNombre(nombre);
        } else {
            materialesTable.cargarDatosIniciales();
        }
    }
    
    public Long getIdSeleccionado() {
        return idSeleccionado;
    }
    
    public String getClaveSeleccionada() {
        return claveSeleccionada;
    }
    // Getters para obtener los datos seleccionados
    public String getNombreSeleccionado() {
        return nombreSeleccionado;
    }
    
    public String getUnidadSeleccionada() {
        return unidadSeleccionada;
    }
}
