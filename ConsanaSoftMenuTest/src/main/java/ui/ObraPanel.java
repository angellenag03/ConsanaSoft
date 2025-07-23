package ui;

import dto.ObraDTO;
import tables.ConceptosObraTable;
import tables.MaterialObraTable;
import utils.HTTPManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tables.HistorialMaterialTable;
import utils.ExcelGenerator;

public class ObraPanel extends JPanel {
    
    private JButton salirButtonConceptos;
    private JButton salirButtonInsumos;
    private JButton aniadirButton;
    private JButton suministrarButton;
    private JButton exportarButton;
    
    private ConceptosObraTable conceptosTable;
    private MaterialObraTable materialesTable;
    private HistorialMaterialTable historialTable;
    private JFrame parentFrame;
    
    private final ObraDTO obra;
    
    public ObraPanel(JFrame parentFrame, ObraDTO obra) {
        this.obra = obra;
        this.parentFrame = parentFrame;
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        salirButtonConceptos = new JButton("Salir");
        salirButtonInsumos = new JButton("Salir");
        aniadirButton = new JButton("Añadir Concepto");
        suministrarButton = new JButton("Suministrar");
        exportarButton = new JButton("Exportar");
        conceptosTable = new ConceptosObraTable(obra.getId());
        materialesTable = new MaterialObraTable(obra.getId());
        historialTable = new HistorialMaterialTable();
        
        // Configurar tamaño preferido de las tablas
        conceptosTable.setPreferredScrollableViewportSize(new Dimension(800, 300));
        materialesTable.setPreferredScrollableViewportSize(new Dimension(400, 300));
        historialTable.setPreferredScrollableViewportSize(new Dimension(400, 300));
        
        // Listener para la selección de materiales
        materialesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String materialNombre = materialesTable.getNombre();
                    if (materialNombre != null) {
                        historialTable.cargarDatosNombreObra(obra.getId(), materialNombre);
                    }
                }
            }
        });
        
        // ActionListeners
        salirButtonConceptos.addActionListener(this::regresarAlMenu);
        salirButtonInsumos.addActionListener(this::regresarAlMenu);
        aniadirButton.addActionListener(this::addConcepto);
        suministrarButton.addActionListener(this::suministrarMaterial);
        exportarButton.addActionListener(e -> 
                ExcelGenerator.getInstance().exportJTablesToExcel(obra.getNombre(), new int[]{1}, materialesTable));
    }
    
    private void setupUI() {
        this.setLayout(new BorderLayout());
        
        // Panel de botones para conceptos (añadir y salir)
        JPanel conceptosButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conceptosButtonPanel.add(aniadirButton);
        conceptosButtonPanel.add(salirButtonConceptos);
        
        // Panel de botones para insumos (suministrar y salir)
        JPanel insumosButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insumosButtonPanel.add(suministrarButton);
        insumosButtonPanel.add(salirButtonInsumos);
        insumosButtonPanel.add(exportarButton);
        
        // Panel dividido HORIZONTAL para insumos
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(materialesTable));
        splitPane.setRightComponent(new JScrollPane(historialTable));
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
        
        // Panel contenedor para la pestaña de insumos
        JPanel insumosPanel = new JPanel(new BorderLayout());
        insumosPanel.add(insumosButtonPanel, BorderLayout.NORTH);
        insumosPanel.add(splitPane, BorderLayout.CENTER);
        
        // Panel contenedor para la pestaña de conceptos
        JPanel conceptosPanel = new JPanel(new BorderLayout());
        conceptosPanel.add(conceptosButtonPanel, BorderLayout.NORTH);
        conceptosPanel.add(new JScrollPane(conceptosTable), BorderLayout.CENTER);
        
        // Panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de conceptos
        tabbedPane.addTab("Conceptos", conceptosPanel);
        
        // Pestaña de insumos
        tabbedPane.addTab("Insumos", insumosPanel);
        
        // Agregar componentes al panel principal
        this.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void addConcepto(ActionEvent e) {
        AddConceptoAObraDialog d = new AddConceptoAObraDialog(parentFrame, obra.getId(), this);
        d.setVisible(true);
    }
    
    private void suministrarMaterial(ActionEvent e) {
        SuministrarMaterialObraDialog d = new SuministrarMaterialObraDialog(parentFrame, obra.getId(), materialesTable.getId(), this);
        d.setVisible(true);
    }
    
    public void actualizarTablas() {
        conceptosTable.cargarDatosIniciales();
        materialesTable.cargarDatosIniciales();
    }
    
    private void regresarAlMenu(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MenuPanel());
            parentFrame.revalidate();
            parentFrame.repaint();
            parentFrame.setTitle("ConsanaSoft");
        });
    }
}