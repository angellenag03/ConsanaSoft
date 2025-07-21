
package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialObraDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import utils.HTTPManager;

public class MaterialObraTable extends JTable {
    private final HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private Gson gson;
    private String id;
    
    public MaterialObraTable(String id) {
        this.gson = new Gson();
        this.id = id; // número de obra
        model = new DefaultTableModel(
                new Object[] {
                    "Clave",
                    "Nombre",
                    "Unidad",
                    "Requerido",
                    "Suministrado",
                    "Pendiente"
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
        
        cargarDatosIniciales();
        ajustarTabla();
    }
    
    public void cargarDatosIniciales() {
        cargarDatos("/materiales-obra/?obraId="+id);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            model.setRowCount(0);
            String response = http.executeRequest(endpoint);
            Type materialesObra = new TypeToken<List<MaterialObraDTO>>(){}.getType();
            List<MaterialObraDTO> materiales = gson.fromJson(response, materialesObra);
            
            for (MaterialObraDTO m : materiales) {
                model.addRow(new Object[]{
                    m.getId(),
                    m.getNombre(),
                    m.getUnidad(),
                    m.getCantidadRequerida(),
                    m.getCantidadSuministrada(),
                    m.getCantidadPendiente()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public String getId() {
        int selectedRow = this.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) this.getValueAt(selectedRow, 0); // Columna 1 es el nombre
        }
        return null;
    }
    
    public String getNombre() {
        int selectedRow = this.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) this.getValueAt(selectedRow, 1); // Columna 1 es el nombre
        }
        return null;
    }
    
    private void ajustarTabla() {
        this.getColumnModel().getColumn(0).setMaxWidth(45);
        this.getColumnModel().getColumn(2).setMaxWidth(50);
        this.getColumnModel().getColumn(3).setMaxWidth(78);
        this.getColumnModel().getColumn(4).setMaxWidth(90);
        this.getColumnModel().getColumn(5).setMaxWidth(70);
        
        this.getColumnModel().getColumn(0).setMinWidth(45);
        this.getColumnModel().getColumn(2).setMinWidth(50);
        this.getColumnModel().getColumn(3).setMinWidth(78);
        this.getColumnModel().getColumn(4).setMinWidth(90);
        this.getColumnModel().getColumn(5).setMinWidth(70);
    }
}
