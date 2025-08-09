package ui;

import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import tables.MaterialGeneraValeTable;
import tables.ValeSalidaTable;
import utils.ExcelGenerator;
import utils.HTTPManager;

public class InstalarMaterialesDialog extends JDialog {
    private String obraId;
    private String nombreObra;
    private JFrame parentFrame;
    
    private JButton instalarButton;
    private JButton cancelarButton;
    
    private MaterialGeneraValeTable materialGeneraValeTable;
    private HTTPManager http = HTTPManager.getInstance();
    private ExcelGenerator excel = ExcelGenerator.getInstance();

    public InstalarMaterialesDialog(String obraId, String nombreObra, JFrame parentFrame) {
        super(parentFrame, "Asigna que materiales deseas instalar a " + obraId);
        this.obraId = obraId;
        this.nombreObra = nombreObra;
        this.parentFrame = parentFrame;
        
        configureDialog();
        initComponents();
        setupLayout();
        setBehavior();
    }
    
    private void configureDialog() {
        this.setSize(800, 500);
        this.setLocationRelativeTo(parentFrame);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
    }
    
    private void initComponents() {
        instalarButton = new JButton("Generar Vale");
        cancelarButton = new JButton("Cancelar");
        materialGeneraValeTable = new MaterialGeneraValeTable(obraId);
    }
    
    private void setupLayout() {
        // Panel principal con márgenes
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        // Panel para los botones alineados a la izquierda
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botonesPanel.add(instalarButton);
        botonesPanel.add(cancelarButton);
        
        JScrollPane scrollPane = new JScrollPane(materialGeneraValeTable);
        
        // Agregar componentes al panel principal
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(botonesPanel, BorderLayout.SOUTH);
        
        // Agregar panel principal al diálogo
        this.add(mainPanel);
    }
    
    private void setBehavior() {
        instalarButton.addActionListener(this::instalar);
        cancelarButton.addActionListener(e -> dispose());
    }
    
    private void instalar(ActionEvent e) {
        try {
            List<Map<String, Object>> materiales = materialGeneraValeTable.getMateriales();
            String jsonBody = new Gson().toJson(materiales); 
            System.out.println("Materiales a enviar: " + jsonBody);
            
            ValeSalidaTable valeTable = new ValeSalidaTable();
            valeTable.cargarDatosDesdeMaterialGeneraVale(materialGeneraValeTable);
            
            http.executeRequest(
                HTTPManager.HttpMethod.POST, 
                "/materiales-obra/instalar-varios?obraId=" + obraId, 
                jsonBody 
            );
            
            List<int[]> anchos = new ArrayList<>();
            anchos.add(new int[]{120,200,120,60});
            excel.exportJTablesToExcel(
                nombreObra + " (VALE)", 
                new int[]{1, 2}, 
                anchos,
                valeTable
            );
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}