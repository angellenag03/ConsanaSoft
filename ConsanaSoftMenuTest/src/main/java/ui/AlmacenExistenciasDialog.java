package ui;

import dto.MaterialDTO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSplitPane;
import tables.AlmacenTable;
import tables.HistorialMaterialTable;
import tables.ExistenciasTable;

public class AlmacenExistenciasDialog extends JDialog {
    private JLabel tituloLabel;
    private JLabel buscarLabel;
    private AlmacenTable almacenTable;
    private ExistenciasTable existenciasTable;
    private JTextField buscarField;
    private JButton buscarButton;
    private JButton cerrarButton;
    private JButton suministrarButton;
    private JButton editarButton; 
    private JFrame parentFrame;

    public AlmacenExistenciasDialog(JFrame parentFrame) {
        super(parentFrame, "Visualización de Almacén", true);
        this.parentFrame = parentFrame;
        initComponents();
        setupLayout();
        configurarComportamiento();
        
        this.setSize(850, 600); // Increased width to accommodate both tables
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }
    
    private void initComponents() {
        tituloLabel = new JLabel("Listado de Materiales en Almacén");
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        almacenTable = new AlmacenTable();
        existenciasTable = new ExistenciasTable();
        
        buscarLabel = new JLabel("Buscar por nombre:");
        buscarField = new JTextField(25);
        buscarButton = new JButton("Buscar");
        editarButton = new  JButton("Editar");
        editarButton.setEnabled(false);
        suministrarButton = new JButton("Suministrar");
        suministrarButton.setEnabled(false);
        cerrarButton = new JButton("Cerrar");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Panel superior (título y búsqueda)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tituloLabel, BorderLayout.NORTH);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(buscarLabel);
        searchPanel.add(buscarField);
        searchPanel.add(buscarButton);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Panel central (tablas divididas)
        JScrollPane almacenScrollPane = new JScrollPane(almacenTable);
        almacenScrollPane.setPreferredSize(new Dimension(400, 400));
        
        JScrollPane existenciasScrollPane = new JScrollPane(existenciasTable);
        existenciasScrollPane.setPreferredSize(new Dimension(300, 400));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, almacenScrollPane, existenciasScrollPane);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        
        // Panel inferior (botón cerrar)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.add(editarButton);
        bottomPanel.add(suministrarButton);
        bottomPanel.add(cerrarButton);
        
        // Ensamblaje final
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    private void configurarComportamiento() {
        editarButton.addActionListener(this::editar);
        suministrarButton.addActionListener(this::suministrar);
        cerrarButton.addActionListener(e -> dispose());
        
        buscarButton.addActionListener(e -> realizarBusqueda());
        
        buscarField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarBusqueda();
                }
            }
        });
        
        almacenTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && almacenTable.getSelectedRow() != -1) {
                editarButton.setEnabled(true);
                suministrarButton.setEnabled(true);
                // Load existencias when a row is selected
                existenciasTable.cargarDatos(almacenTable.getId()+"");
            }
        });
        
        // Agregar MouseListener para el doble clic
        almacenTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = almacenTable.getSelectedRow();
                    if (row >= 0) {
                        String nombreMaterial = almacenTable.getNombre();
                        mostrarHistorialMaterial(nombreMaterial);
                    }
                }
            }
        });
    }
    
    private void mostrarHistorialMaterial(String nombreMaterial) {
        JDialog historialDialog = new JDialog(this, "Historial del Material: " + nombreMaterial, true);
        HistorialMaterialTable historialTable = new HistorialMaterialTable();
        historialTable.cargarDatosNombre(nombreMaterial);
        
        JScrollPane scrollPane = new JScrollPane(historialTable);
        historialDialog.add(scrollPane);
        
        // Add ESC key listener to close the dialog
        historialDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    historialDialog.dispose();
                }
            }
        });
        
        historialDialog.setSize(800, 400);
        historialDialog.setLocationRelativeTo(this);
        historialDialog.setVisible(true);
    }
    
    private void realizarBusqueda() {
        String textoBusqueda = buscarField.getText().trim();
        editarButton.setEnabled(false);
        suministrarButton.setEnabled(false);
        if(textoBusqueda.isEmpty()) {
            almacenTable.cargarDatosIniciales();
        } else {
            almacenTable.cargarDatosNombre(textoBusqueda);
        }
        existenciasTable.cargarDatosIniciales(); // Clear existencias table when searching
    }
    
    private void suministrar(ActionEvent e) {
        SuministrarAlmacenDialog d = new SuministrarAlmacenDialog(parentFrame, almacenTable.getId());
        d.setVisible(true);
        
        almacenTable.cargarDatosIniciales();
        existenciasTable.cargarDatosIniciales(); // Clear existencias table after supplying
    }
    
    private void editar(ActionEvent e) {
        Long id = almacenTable.getId();
        String clave = almacenTable.getClave();
        String nombre = almacenTable.getNombre();
        String unidad = almacenTable.getUnidad();
        EditarMaterialDialog dialog = new EditarMaterialDialog(
                new MaterialDTO(id, clave, nombre, unidad), parentFrame);
        dialog.setVisible(true);
    }
}