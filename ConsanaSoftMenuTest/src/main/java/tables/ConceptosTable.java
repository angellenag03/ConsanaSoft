package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ConceptoDTO;
import utils.HTTPManager;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ConceptosTable extends JTable {
    private final HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private final Gson gson = new Gson();
    
    public ConceptosTable() {
        model = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Clave",
                    "Nombre",
                    "Unidad"
                }, 0)
        {
                @Override
                public boolean isCellEditable(int row, int column) {
                        return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                        return String.class;
                }
        };
        this.setModel(model);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ajustarTabla();
        
        cargarDatosIniciales();
    }
    
    // Renderer para manejar el texto con saltos de línea
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        
        if (column == 2) { // Solo para la columna "nombre"
            JTextArea textArea = new JTextArea();
            textArea.setText((String) getValueAt(row, column));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(new EmptyBorder(4, 4, 4, 4));
            textArea.setFont(getFont());
            textArea.setSize(getColumnModel().getColumn(column).getWidth(), getRowHeight(row));
            
            // Ajustar altura de la fila según el contenido
            int preferredHeight = textArea.getPreferredSize().height;
            if (getRowHeight(row) != preferredHeight) {
                setRowHeight(row, preferredHeight);
            }
            
            return textArea;
        }
        
        return component;
    }
    
    public void cargarDatosIniciales() {
        cargarDatos("/concepto/list");
    }
    
    public void cargarDatosPorId(String id) {
        try {
            model.setRowCount(0);
            String conceptoJson = http.executeRequest("/concepto/"+id);
            ConceptoDTO concepto = gson.fromJson(conceptoJson, ConceptoDTO.class);
            
            model.setRowCount(0);
            model.addRow(new Object[]{
                concepto.getId(),
                concepto.getNombre(),
                concepto.getClave(),
                concepto.getUnidad()
            });
        } catch (JsonSyntaxException | IOException e) {
            SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, 
                            "Error al cargar el concepto: \n" + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
        }
    }
    
    public void cargarDatosPorClave(String clave) {
        cargarDatos("/concepto/list?clave="+clave);
    }
    
    public void cargarDatosPorNombre(String nombre) {
        cargarDatos("/concepto/list?nombre="+nombre);
    }
    
    private void cargarDatos(String endpoint) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                model.setRowCount(0);
                try {
                    List<Map<String, String>> conceptos = gson.fromJson(
                        http.executeRequest(endpoint), 
                        new TypeToken<List<Map<String, String>>>(){}.getType());
                    model.setRowCount(0);
                    
                    for (Map<String, String> concepto : conceptos) {
                        model.addRow(new Object[]{
                            concepto.get("id"),
                            concepto.get("clave"),
                            concepto.get("nombre"),
                            concepto.get("unidad")
                        });
                    }
                } catch (JsonSyntaxException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, 
                            "Error al cargar los conceptos: \n" + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    public String getSelectedConceptoId() {
        int selectedRow = this.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) model.getValueAt(selectedRow, 0);
        }
        return null;
    }
    
    private void ajustarTabla() {
        this.getColumnModel().getColumn(0).setMaxWidth(40);
        this.getColumnModel().getColumn(1).setMinWidth(120);
        this.getColumnModel().getColumn(1).setMaxWidth(120);
//        this.getColumnModel().getColumn(2).setMaxWidth(420); // Más ancho para nombres largos
        this.getColumnModel().getColumn(3).setMaxWidth(60);
        
        // Altura inicial basada en la fuente
        this.setRowHeight(this.getFontMetrics(this.getFont()).getHeight() + 4);
    }
}