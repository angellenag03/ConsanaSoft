package ui;

import com.google.gson.Gson;
import dto.ConceptoDTO;
import tables.ConceptosTable;
import utils.HTTPManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.SwingConstants;
import tables.MaterialConceptoTable;

public class GestorConceptosDialog extends JDialog {

    private JLabel tituloLabel;
    private JLabel buscarLabel;
    private ConceptosTable conceptosTable;
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson;
    
    private JPanel infoPanel;
    private JLabel idLabel, claveLabel, nombreLabel, unidadLabel; 
    private JButton editarButton, crearButton, consultarButton, cancelarButton, buscarButton;
    private JTextField buscarField;
    private JComboBox<String> tipoBusquedaCombo;
    private JFrame parentFrame;
    
    public GestorConceptosDialog(JFrame parentFrame) {
        super(parentFrame, "Añadir Concepto", true);
        this.gson = new Gson();
        initComponents();
        setupLayout();
        configurarComportamiento();
        
        this.setSize(820, 600);
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(true);
    }
    
    private void initComponents() {
        tituloLabel = new JLabel("Selecciona el concepto a añadir:");
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        conceptosTable = new ConceptosTable();
        
        //Panel de info
        infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Concepto"));
        
        idLabel = new JLabel("ID: ");
        claveLabel = new JLabel("Clave: ");
        nombreLabel = new JLabel("Nombre: ");
        unidadLabel = new JLabel("Unidad: ");
        
        // Componentes de búsqueda
        buscarLabel = new JLabel("Buscar por:");
        buscarField = new JTextField(25);
        buscarButton = new JButton("Buscar");
        tipoBusquedaCombo = new JComboBox<>(new String[]{"Nombre", "Clave", "ID"});
        crearButton = new JButton("Crear Nuevo");
        
        infoPanel.add(idLabel);
        infoPanel.add(claveLabel);
        infoPanel.add(nombreLabel);
        
        // Botones
        
        consultarButton = new JButton("Consultar Materiales");
        consultarButton.setEnabled(false);
        editarButton = new JButton("Editar");
        editarButton.setEnabled(false);
        cancelarButton = new JButton("Cancelar");
        buscarButton = new JButton("Buscar");
        
//        addButton.setPreferredSize(new Dimension(85, 25));
//        editarButton.setPreferredSize(new Dimension(85, 25));
//        crearButton.setPreferredSize(new Dimension(120, 25));
//        cancelarButton.setPreferredSize(new Dimension(85, 25));
//        buscarButton.setPreferredSize(new Dimension(85, 25));
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior (búsqueda)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(buscarLabel);
        searchPanel.add(tipoBusquedaCombo);
        searchPanel.add(buscarField);
        searchPanel.add(buscarButton);
        searchPanel.add(crearButton);
        
        // Panel central (tabla e información)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        JScrollPane scrollPane = new JScrollPane(conceptosTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(consultarButton);
        buttonPanel.add(editarButton);
        
        // Ensamblaje final
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    // ... (resto de los métodos se mantienen igual que en la versión anterior)
    private void configurarComportamiento() {
        cancelarButton.addActionListener(e -> dispose());
        
        conceptosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && conceptosTable.getSelectedRow() != -1) {
                upInfoConcepto();
            }
        });
        
        consultarButton.addActionListener(this::consultarMaterial);
        editarButton.addActionListener(this::editarConcepto);
        crearButton.addActionListener(this::crearConcepto);
        buscarButton.addActionListener(e -> realizarBusqueda());
        
        buscarField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarBusqueda();
                }
            }
        });
        
        KeyStroke escapeKey = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarButton.doClick();
            }
        });
    }
    
    private void realizarBusqueda() {
        editarButton.setEnabled(false);
        String txtBusqueda = buscarField.getText().trim();
        if(txtBusqueda.isEmpty()) {
            conceptosTable.cargarDatosIniciales();
            return;
        }
        
        
        int tipoBusqueda = tipoBusquedaCombo.getSelectedIndex();
        
        // CODIFICAR TEXTO EN CASO DE NOMBRE
        if(tipoBusqueda == 0)
            txtBusqueda = URLEncoder.encode(txtBusqueda, StandardCharsets.UTF_8);
        
        switch (tipoBusqueda) {
            case 2 -> conceptosTable.cargarDatosPorId(txtBusqueda);
            case 1 -> conceptosTable.cargarDatosPorClave(txtBusqueda);
            case 0 -> conceptosTable.cargarDatosPorNombre(txtBusqueda);
            default -> throw new AssertionError();
        }
    }
    
    private void upInfoConcepto() {
        consultarButton.setEnabled(true);
        editarButton.setEnabled(true);
        
        String id = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 0);
        String claveConcepto = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 1);
        String nombre = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 2);
        
        idLabel.setText("ID: "+id);
        claveLabel.setText("Clave: "+(claveConcepto != null ? claveConcepto : "N/A"));
        nombreLabel.setText("Nombre: "+nombre);
    }
    
    private void consultarMaterial(ActionEvent e) {
        String id = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 0);
        
        JDialog mcDialog = new JDialog(this, "Materiales del concepto", true);
        MaterialConceptoTable materialesConceptoTable = new MaterialConceptoTable(id);
        
        JScrollPane scrollPane = new JScrollPane(materialesConceptoTable);
        mcDialog.add(scrollPane);
        
        mcDialog.setSize(500, 400);
        mcDialog.setLocationRelativeTo(this);
        mcDialog.setVisible(true);
    }
    
    private void editarConcepto(ActionEvent e) {
        EditarConceptoDialog dialog = new EditarConceptoDialog(getConcepto(), parentFrame);
        dialog.setVisible(true);
    }
    
    private ConceptoDTO getConcepto() {
        try {
            String id = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 0);
            String conceptoJson = http.executeRequest("/concepto/"+id);
            ConceptoDTO concepto = gson.fromJson(conceptoJson, ConceptoDTO.class);
            
            return concepto;
        } catch (Exception e) { return null; }
    }
    
    private void crearConcepto(ActionEvent e) {
        NuevoConceptoDialog dialog = new NuevoConceptoDialog(parentFrame);
        dialog.setVisible(true);
        conceptosTable.cargarDatosIniciales();
    }
}