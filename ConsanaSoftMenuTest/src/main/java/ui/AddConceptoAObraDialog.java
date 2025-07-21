package ui;

import com.google.gson.Gson;
import dto.ConceptoDTO;
import utils.HTTPManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.AbstractAction;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import tables.ConceptosTable;

public class AddConceptoAObraDialog extends JDialog {
    private JLabel tituloLabel;
    private JLabel buscarLabel;
    private ConceptosTable conceptosTable;
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson;
    private final String obraId;
    
    private JPanel infoPanel;
    private JLabel idLabel, claveLabel, nombreLabel, cantidadLabel, unidadLabel; 
    private JButton addButton, editarButton, cancelarButton, buscarButton;
    private JTextField buscarField, cantidadField;
    private JComboBox<String> tipoBusquedaCombo;
    private final ObraPanel obraPanel;
    private JFrame parentFrame;
    
    public AddConceptoAObraDialog(JFrame parentFrame, String clave, ObraPanel obraPanel) {
        this.obraPanel = obraPanel;
        this.obraId = clave;
        gson = new Gson();
        super(parentFrame, "Añadir Concepto", true);
        initComponents();
        setupLayout();
        configurarComportamiento();
        
        this.setSize(800, 600);
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
        
        cantidadLabel = new JLabel("Cantidad: ");
        
        // Componentes de búsqueda
        buscarLabel = new JLabel("Buscar por:");
        buscarField = new JTextField(25);
        buscarButton = new JButton("Buscar");
        tipoBusquedaCombo = new JComboBox<>(new String[]{"ID", "Clave", "Nombre"});
        cantidadField = new JTextField(6);
        
        infoPanel.add(idLabel);
        infoPanel.add(claveLabel);
        infoPanel.add(nombreLabel);
        infoPanel.add(cantidadLabel);
        
        // Botones
        addButton = new JButton("Añadir"); 
        addButton.setEnabled(false);
        editarButton = new JButton("Editar");
        editarButton.setEnabled(false);
        cancelarButton = new JButton("Cancelar");
        buscarButton = new JButton("Buscar");
        
        addButton.setPreferredSize(new Dimension(85, 25));
        editarButton.setPreferredSize(new Dimension(85, 25));
        cancelarButton.setPreferredSize(new Dimension(85, 25));
        buscarButton.setPreferredSize(new Dimension(85, 25));
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
        
        // Panel de cantidad
        JPanel cantidadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        cantidadPanel.add(cantidadLabel);
        cantidadPanel.add(cantidadField);
        searchPanel.add(cantidadPanel);
        
        // Panel central (tabla e información)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        JScrollPane scrollPane = new JScrollPane(conceptosTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(addButton);
        
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
        
        addButton.addActionListener(this::addConcepto);
        editarButton.addActionListener(this::editarConcepto);
        buscarButton.addActionListener(e -> realizarBusqueda());
        
        buscarField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarBusqueda();
                }
            }
        });
        
        getRootPane().setDefaultButton(addButton);
        
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
        addButton.setEnabled(false);
        editarButton.setEnabled(false);
        String textoBusqueda = buscarField.getText().trim();
        if(textoBusqueda.isEmpty()) {
            conceptosTable.cargarDatosIniciales();
            return;
        }
        
        int tipoBusqueda = tipoBusquedaCombo.getSelectedIndex();
        switch (tipoBusqueda) {
            case 0 -> conceptosTable.cargarDatosPorId(textoBusqueda);
            case 1 -> conceptosTable.cargarDatosPorClave(textoBusqueda);
            case 2 -> conceptosTable.cargarDatosPorNombre(textoBusqueda);
            default -> throw new AssertionError();
        }
    }
    
    private void upInfoConcepto() {
        addButton.setEnabled(true);
        editarButton.setEnabled(true);
        
        String id = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 0);
        String claveConcepto = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 1);
        String nombre = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 2);
        
        idLabel.setText("ID: "+id);
        claveLabel.setText("Clave: "+(claveConcepto != null ? claveConcepto : "N/A"));
        nombreLabel.setText("Nombre: "+nombre);
    }

    private void addConcepto(ActionEvent e) {
        try {
            String cantidadText = cantidadField.getText().trim();
            if (cantidadText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese una cantidad", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int cantidad = Integer.parseInt(cantidadText);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que cero", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String conceptoId = conceptosTable.getSelectedConceptoId();
            if (conceptoId == null) {
                JOptionPane.showMessageDialog(this, "Por favor seleccione un concepto", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            HashMap<String, Object> conceptoObra = new HashMap<>();
            conceptoObra.put("cantidad", cantidad);
            conceptoObra.put("conceptoId", conceptoId);
            conceptoObra.put("obraId", obraId);

            System.out.println(conceptoObra+"\n");
            
            http.executeRequest(
                    HTTPManager.HttpMethod.PATCH,
                    "/concepto-obra/upsert", conceptoObra);
            
            JOptionPane.showMessageDialog(this, "Concepto añadido correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            obraPanel.actualizarTablaConceptos();
            dispose();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al añadir el concepto: \n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese una cantidad válida", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } 
    }
    
    private void editarConcepto(ActionEvent e) {
        EditarConceptoDialog ecd = new EditarConceptoDialog(getConcepto(), parentFrame);
        ecd.setVisible(true);
    }
    
    private ConceptoDTO getConcepto() {
        try {
            String id = (String) conceptosTable.getValueAt(conceptosTable.getSelectedRow(), 0);
            String conceptoJson = http.executeRequest(
                    HTTPManager.HttpMethod.GET, "/concepto/", id);
            ConceptoDTO concepto = gson.fromJson(conceptoJson, ConceptoDTO.class);
            
            return concepto;
        } catch (Exception e) { return null; }
    }
}