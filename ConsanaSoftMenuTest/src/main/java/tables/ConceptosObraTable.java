
package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ConceptoObraDTO;
import utils.HTTPManager;
import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ConceptosObraTable extends JTable {
    private final HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private final String obraId;
    private Gson gson;
    
    public ConceptosObraTable(String obraId) {
        this.gson = new Gson();
        this.obraId = obraId;
        model = new DefaultTableModel(
                new Object[] {
                    "Partida",
                    "Nombre",
                    "Unidad",
                    "Cantidad"
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
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        
        if (column == 1) { // Solo para la columna "nombre"
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
        cargarDatos("/concepto-obra/list?id="+obraId);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            model.setRowCount(0);
            String response = http.executeRequest(endpoint);
            Type obraListType = new TypeToken<List<ConceptoObraDTO>>(){}.getType();
            List<ConceptoObraDTO> conceptosObra = gson.fromJson(response, obraListType);
            
            for (ConceptoObraDTO co : conceptosObra) {
                model.addRow(new Object[]{
                    co.getPartida(),
                    co.getNombre(),
                    co.getUnidad(),
                    co.getCantidad()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
    
    private void ajustarTabla() {
        this.getColumnModel().getColumn(0).setPreferredWidth(30);
        this.getColumnModel().getColumn(1).setPreferredWidth(1000);
        this.getColumnModel().getColumn(2).setPreferredWidth(20);
        this.getColumnModel().getColumn(2).setPreferredWidth(20);
    }
}
