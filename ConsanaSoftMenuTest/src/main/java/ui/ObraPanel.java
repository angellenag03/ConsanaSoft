package ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import dto.ObraDTO;
import tables.ConceptosObraTable;
import tables.MaterialObraTable;
import tables.FechasValesTable;
import tables.ValeSalidaTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tables.HistorialConceptosTable;
import tables.HistorialMaterialTable;
import utils.ExcelGenerator;

public class ObraPanel extends JPanel {
    
    private JButton salirButtonConceptos;
    private JButton salirButtonInsumos;
    private JButton aniadirButton; // añadir concepto
    private JButton removerButton; // remover concepto
    private JButton instConceptoButton;
    private JButton uninstConceptoButton;
    private JButton suministrarButton;
    private JButton instalarMaterialButton;
    private JButton exportarButton;
    private JButton exportarValesButton;
    
    private ConceptosObraTable conceptosTable;
    private HistorialConceptosTable historialConceptosTable;
    private MaterialObraTable materialesTable;
    private HistorialMaterialTable historialTable;
    private ValeSalidaTable valeSalidaTable;
    private FechasValesTable fechasValesTable;
    
    private JFrame parentFrame;
    private final ObraDTO obra;
    private final String fecha;
    
    public ObraPanel(JFrame parentFrame, ObraDTO obra) {
        this.obra = obra;
        this.parentFrame = parentFrame;
        this.fecha = getFechaHora();
        initComponents();
        setupUI();
        this.parentFrame.setTitle("ConsanaSoft: "+obra.getId()+" "+obra.getNombre());
    }
    
    private void initComponents() {
        salirButtonConceptos = new JButton("Salir", new FlatSVGIcon("icons/door-exit.svg"));
        salirButtonInsumos = new JButton("Salir", new FlatSVGIcon("icons/door-exit.svg"));
        aniadirButton = new JButton("Añadir Concepto", new FlatSVGIcon("icons/chevrons-up.svg"));
        removerButton = new JButton("Remover Concepto", new FlatSVGIcon("icons/chevrons-down.svg"));
        instConceptoButton = new JButton("Instalar", new FlatSVGIcon("icons/square-chevrons-up.svg"));
        uninstConceptoButton = new JButton("Desinstalar", new FlatSVGIcon("icons/square-chevrons-down.svg"));
        suministrarButton = new JButton("Suministrar", new FlatSVGIcon("icons/basket-plus.svg"));
        instalarMaterialButton = new JButton("Generar Vale", new FlatSVGIcon("icons/square-chevrons-up.svg"));
        exportarButton = new JButton("Exportar", new FlatSVGIcon("icons/file-spreadsheet.svg"));
        exportarValesButton = new JButton("Exportar Vale", new FlatSVGIcon("icons/file-spreadsheet.svg"));
        
        conceptosTable = new ConceptosObraTable(obra.getId());
        historialConceptosTable = new HistorialConceptosTable();
        materialesTable = new MaterialObraTable(obra.getId());
        historialTable = new HistorialMaterialTable();
        valeSalidaTable = new ValeSalidaTable();
        fechasValesTable = new FechasValesTable(obra.getId());
        
        instConceptoButton.setEnabled(false);
        uninstConceptoButton.setEnabled(false);
        
        suministrarButton.setEnabled(false);
        exportarValesButton.setEnabled(false);
        
        
        // Configurar tamaño preferido de las tablas
        conceptosTable.setPreferredScrollableViewportSize(new Dimension(600, 300));
        historialConceptosTable.setPreferredScrollableViewportSize(new Dimension(400,300));
        
        materialesTable.setPreferredScrollableViewportSize(new Dimension(400, 300));
        historialTable.setPreferredScrollableViewportSize(new Dimension(400, 300));
        valeSalidaTable.setPreferredScrollableViewportSize(new Dimension(600, 300));
        fechasValesTable.setPreferredScrollableViewportSize(new Dimension(200, 300));
        
        // Listener para la selección de materiales
        materialesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String materialNombre = materialesTable.getNombre();
                    if (materialNombre != null) {
                        suministrarButton.setEnabled(true);
                        exportarValesButton.setEnabled(true);
                        historialTable.cargarDatosNombreObra(obra.getId(), materialNombre);
                    }
                }
            }
        });
        
        // Listener para la seleccion de conceptos
        conceptosTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // por motivos prácticos cada que cambias de columna se desactivan los botones
                instConceptoButton.setEnabled(false);
                uninstConceptoButton.setEnabled(false);
                if (!e.getValueIsAdjusting()) {
                    Long conceptoId = conceptosTable.getConceptoId();
                    if (conceptoId != null) {
                        if(!conceptosTable.isFullInstalado()) {
                            instConceptoButton.setEnabled(true);
                        }
                        if(conceptosTable.getInstalado()!= 0) {
                            uninstConceptoButton.setEnabled(true);
                        }
                        removerButton.setEnabled(true);
                        historialConceptosTable.cargarDatosConceptoObra(conceptoId, obra.getId());
                    }
                }
            }
        });
        
        // Listener para la selección de fechas de vales
        fechasValesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = fechasValesTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String fechaHora = (String) fechasValesTable.getValueAt(selectedRow, 0);
                        valeSalidaTable.cargarDatosFechaHora(obra.getId(), fechaHora);
                    }
                }
            }
        });
        
        // ActionListeners
        salirButtonConceptos.addActionListener(this::regresarAlMenu);
        salirButtonInsumos.addActionListener(this::regresarAlMenu);
        aniadirButton.addActionListener(this::addConcepto);
        removerButton.addActionListener(this::removerConcepto);
        instConceptoButton.addActionListener(this::instalarConcepto);
        uninstConceptoButton.addActionListener(this::desinstalarConcepto);
        suministrarButton.addActionListener(this::suministrarMaterial);
        instalarMaterialButton.addActionListener(this::instalarMaterial);
        exportarButton.addActionListener(this::exportarInsumos);
        exportarValesButton.addActionListener(this::exportarVale);
    }
    
    private void setupUI() {
        this.setLayout(new BorderLayout());
        
        // Panel de botones para conceptos (añadir y salir)
        JPanel conceptosButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conceptosButtonPanel.add(aniadirButton);
        conceptosButtonPanel.add(removerButton);
        conceptosButtonPanel.add(instConceptoButton);
        conceptosButtonPanel.add(uninstConceptoButton);
        conceptosButtonPanel.add(salirButtonConceptos);
        
        // Panel de botones para insumos (suministrar y salir)
        JPanel insumosButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insumosButtonPanel.add(suministrarButton);
        insumosButtonPanel.add(instalarMaterialButton);
        insumosButtonPanel.add(exportarButton);
        insumosButtonPanel.add(salirButtonInsumos);
        
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
        conceptosPanel.add(new JScrollPane(historialConceptosTable), BorderLayout.EAST);
        
        // Panel para la pestaña de Vales Generados
        JPanel valesPanel = new JPanel(new BorderLayout());
        
        // Panel de botones para vales
        JPanel valesButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        valesButtonPanel.add(exportarValesButton);
        
        // Split pane para las tablas de vales (20% fechas, 80% detalles)
        JSplitPane valesSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        valesSplitPane.setLeftComponent(new JScrollPane(fechasValesTable));
        valesSplitPane.setRightComponent(new JScrollPane(valeSalidaTable));
        valesSplitPane.setDividerLocation(0.2);
        valesSplitPane.setResizeWeight(0.2);
        valesSplitPane.setContinuousLayout(true);
        
        valesPanel.add(valesButtonPanel, BorderLayout.NORTH);
        valesPanel.add(valesSplitPane, BorderLayout.CENTER);
        
        // Panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de conceptos
        tabbedPane.addTab("Conceptos", conceptosPanel);
        
        // Pestaña de insumos
        tabbedPane.addTab("Insumos", insumosPanel);
        
        // Nueva pestaña para Vales Generados
        tabbedPane.addTab("Vales Generados", valesPanel);
        
        // Agregar componentes al panel principal
        this.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void addConcepto(ActionEvent e) {
        AddConceptoAObraDialog d = new AddConceptoAObraDialog(parentFrame, 
                obra.getId(), this);
        d.setVisible(true);
    }
    
    private void removerConcepto(ActionEvent e) {
        RemoverConceptosDialog d = new RemoverConceptosDialog(parentFrame, 
                obra.getId(), this, conceptosTable.getConceptoId());
        d.setVisible(true);
    }
    
    private void instalarConcepto(ActionEvent e) {
        DesInstalarConceptoDialog d = new DesInstalarConceptoDialog(parentFrame, true, 
                obra.getId(), this, conceptosTable.getConceptoId());
        d.setVisible(true);
    }
    
    private void desinstalarConcepto(ActionEvent e) {
        DesInstalarConceptoDialog d = new DesInstalarConceptoDialog(parentFrame, false, 
                obra.getId(), this, conceptosTable.getConceptoId());
        d.setVisible(true);
    }
    
    private void suministrarMaterial(ActionEvent e) {
        String materialId = materialesTable.getId();
        System.out.println(materialId);
        if (materialId==null) {
            JOptionPane.showMessageDialog(this, 
                        "Favor de seleccionar un material", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            
        } else {
            SuministrarMaterialObraDialog dialog = new SuministrarMaterialObraDialog(parentFrame, obra.getId(), materialId, this);
            dialog.setVisible(true);
        }
    }
    
    private void instalarMaterial(ActionEvent e) {
        InstalarMaterialesDialog dialog = new InstalarMaterialesDialog(obra.getId(), obra.getNombre(), parentFrame);
        dialog.setVisible(true);
        materialesTable.cargarDatosIniciales();
    }
    
    public void actualizarTablas() {
        conceptosTable.cargarDatosIniciales();
        historialConceptosTable.cargarDatosIniciales();
        materialesTable.cargarDatosIniciales();
        fechasValesTable.cargarDatosIniciales();
        valeSalidaTable.cargarDatosIniciales();
        suministrarButton.setEnabled(false);
        exportarValesButton.setEnabled(false);
        removerButton.setEnabled(false);
        instConceptoButton.setEnabled(false);
        uninstConceptoButton.setEnabled(false);
    }
    
    private void exportarInsumos(ActionEvent e) {
        List<int[]> anchos = List.of(new int[]{46, 326, 80, 80, 120, 120, 80, 80});
        ExcelGenerator.getInstance().exportJTablesToExcel(
                obra.getNombre(), new int[]{1}, anchos, materialesTable);
    }
    
    private void exportarVale(ActionEvent e) {
        List<int[]> anchos = List.of(new int[]{120, 200, 120, 60});
        ExcelGenerator.getInstance().exportJTablesToExcel(
                obra.getNombre(), new int[]{1}, anchos, valeSalidaTable);
    }
    
    private void regresarAlMenu(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MenuPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
            parentFrame.setTitle("ConsanaSoft");
        });
    }
    
    private static String getFechaHora() {
        return LocalDateTime.now()
               .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}