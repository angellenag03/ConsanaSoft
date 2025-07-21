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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tables.HistorialMaterialTable;

public class ObraPanel extends JPanel {
    
    private JButton aniadirButton;
    private JButton suministrarButton;
    private ConceptosObraTable conceptosTable;
    private MaterialObraTable materialesTable;
    private HistorialMaterialTable historialTable;
    private JFrame parentFrame;
    
    private final HTTPManager http = HTTPManager.getInstance();
    private final ObraDTO obra;
    
    public ObraPanel(JFrame parentFrame, ObraDTO obra) {
        this.obra = obra;
        this.parentFrame = parentFrame;
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        aniadirButton = new JButton("Añadir");
        suministrarButton = new JButton("Suministrar");
        conceptosTable = new ConceptosObraTable(obra.getId());
        materialesTable = new MaterialObraTable(obra.getId());
        historialTable = new HistorialMaterialTable();
        
        // Configurar tamaño preferido de las tablas (ajustado para disposición horizontal)
        conceptosTable.setPreferredScrollableViewportSize(new Dimension(800, 300));
        materialesTable.setPreferredScrollableViewportSize(new Dimension(400, 300)); // Mitad del ancho
        historialTable.setPreferredScrollableViewportSize(new Dimension(400, 300));  // Mitad del ancho
        
        // Listener para la selección de materiales
        materialesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String materialNombre = materialesTable.getNombre();
                    if (materialNombre != null) {
                        historialTable.cargarDatosNombre(materialNombre);
                    }
                }
            }
        });
        
        // ActionListeners
        aniadirButton.addActionListener(this::addConcepto);
        suministrarButton.addActionListener(this::suministrarMaterial);
    }
    
    private void setupUI() {
        this.setLayout(new BorderLayout());
        
        // Panel superior con botones principales
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(aniadirButton);
        
        // Panel de botones para insumos
        JPanel insumosButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insumosButtonPanel.add(suministrarButton);
        
        // Panel dividido HORIZONTAL (Materiales a la izquierda, Historial a la derecha)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(materialesTable));
        splitPane.setRightComponent(new JScrollPane(historialTable));
        splitPane.setDividerLocation(0.5); // División al 50%
        splitPane.setResizeWeight(0.5);    // Ambas partes se redimensionan igual
        splitPane.setContinuousLayout(true); // Redibujo continuo al mover
        
        // Panel contenedor para la pestaña de insumos
        JPanel insumosPanel = new JPanel(new BorderLayout());
        insumosPanel.add(insumosButtonPanel, BorderLayout.NORTH);
        insumosPanel.add(splitPane, BorderLayout.CENTER);
        
        // Panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de conceptos
        JScrollPane conceptosScrollPane = new JScrollPane(conceptosTable);
        tabbedPane.addTab("Conceptos", conceptosScrollPane);
        
        // Pestaña de insumos (con división horizontal)
        tabbedPane.addTab("Insumos", insumosPanel);
        
        // Agregar componentes al panel principal
        this.add(topPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
    }
    
    // Métodos de acción (sin cambios)
    private void salir(ActionEvent e) {
        System.out.println("Volver al menú principal");
    }
    
    private void addConcepto(ActionEvent e) {
        AddConceptoAObraDialog d = new AddConceptoAObraDialog(parentFrame, obra.getId(), this);
        d.setVisible(true);
    }
    
    private void suministrarMaterial(ActionEvent e) {
        SuministrarMaterialObraDialog d = new SuministrarMaterialObraDialog(parentFrame, obra.getId(), materialesTable.getId());
        d.setVisible(true);
    }
    
    public void actualizarTablaConceptos() {
        conceptosTable.cargarDatosIniciales();
        materialesTable.cargarDatosIniciales();
    }
    
}