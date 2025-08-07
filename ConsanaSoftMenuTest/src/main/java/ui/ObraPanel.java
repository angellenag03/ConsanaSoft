package ui;

import dto.ObraDTO;
import tables.ConceptosObraTable;
import tables.MaterialObraTable;
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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tables.HistorialMaterialTable;
import tables.ValeSalidaTable;
import utils.ExcelGenerator;

public class ObraPanel extends JPanel {
    
    private JButton salirButtonConceptos;
    private JButton salirButtonInsumos;
    private JButton aniadirButton;
    
    private JButton generarValeButton;
    private JButton suministrarButton;
    private JButton instalarButton;
    private JButton exportarButton;
    
    private ConceptosObraTable conceptosTable;
    private MaterialObraTable materialesTable;
    private HistorialMaterialTable historialTable;
    
    private ValeSalidaTable valeSalidaTable;
    
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
        salirButtonConceptos = new JButton("Salir");
        salirButtonInsumos = new JButton("Salir");
        aniadirButton = new JButton("Añadir Concepto");
        suministrarButton = new JButton("Suministrar");
        instalarButton = new JButton("Instalar");
        exportarButton = new JButton("Exportar");
        generarValeButton = new JButton("Generar Vale");
        conceptosTable = new ConceptosObraTable(obra.getId());
        materialesTable = new MaterialObraTable(obra.getId());
        valeSalidaTable = new ValeSalidaTable(obra.getId());
        historialTable = new HistorialMaterialTable();
        
        suministrarButton.setEnabled(false);
        instalarButton.setEnabled(false);
        
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
                        suministrarButton.setEnabled(true);
                        instalarButton.setEnabled(true); 
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
        instalarButton.addActionListener(this::instalarMaterial);
        exportarButton.addActionListener(this::exportarInsumos);
        generarValeButton.addActionListener(this::generarVale);
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
        insumosButtonPanel.add(instalarButton);
        insumosButtonPanel.add(exportarButton);
        insumosButtonPanel.add(generarValeButton);
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
        InstalarMaterialObraDialog dialog = new InstalarMaterialObraDialog(parentFrame, obra.getId(), materialesTable.getId(), this);
        dialog.setVisible(true);
    }
    
    public void actualizarTablas() {
        conceptosTable.cargarDatosIniciales();
        materialesTable.cargarDatosIniciales();
        suministrarButton.setEnabled(false);
        instalarButton.setEnabled(false);
    }
    
    private void generarVale(ActionEvent e) {
        String respuesta = preguntarTipoVale(parentFrame);
        if(respuesta == null) {
            return;
        }
        if(respuesta.equals("Hoy")){
            valeSalidaTable.cargarDatosHoy();
        }
        if(respuesta.equals("Sesion")){
            valeSalidaTable.cargarDatosSesion(fecha);
        } 
        
        List<int[]> anchos = List.of(new int[]{200,65,60});
        
        ExcelGenerator.getInstance().exportJTablesToExcelWithCustomWidths(
                obra.getNombre(), new int[]{0},anchos, valeSalidaTable);
    }
    
    private void exportarInsumos(ActionEvent e) {
        List<int[]> anchos = List.of(new int[]{46, 326, 80, 80, 120, 120, 80, 80});
        ExcelGenerator.getInstance().exportJTablesToExcelWithCustomWidths(
                obra.getNombre(), new int[]{1}, anchos, materialesTable);
    }
    
    private static String preguntarTipoVale(JFrame parent) {
        Object[] opciones = {"Hoy", "Sesión"};
        return (String)JOptionPane.showInputDialog(
            parent,
            "¿Deseas generar el vale de hoy o de la sesión actual?",
            "Generar Vale",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0] // Opción por defecto
        );
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