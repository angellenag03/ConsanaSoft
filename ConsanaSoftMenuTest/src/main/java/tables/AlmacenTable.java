
package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ConceptoObraDTO;
import dto.MaterialOutputDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import utils.HTTPManager;

public class AlmacenTable extends JTable {
    private final HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private Gson gson;
    
    public AlmacenTable() {
        this.gson = new Gson();
        model = new DefaultTableModel(
                new Object[] {
                    "ID",
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
    
    public void cargarDatosIniciales() {
        cargarDatos("/almacen/list");
    }
    
    public void cargarDatosNombre(String nombre) {
        cargarDatos("/almacen/list?nombre="+nombre);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            model.setRowCount(0);
            String response = http.executeRequest(endpoint);
            Type historialMateriales = new TypeToken<List<MaterialOutputDTO>>(){}.getType();
            List<MaterialOutputDTO> materiales = gson.fromJson(response, historialMateriales);
            
            for (MaterialOutputDTO m : materiales) {
                model.addRow(new Object[]{
                    m.getId(),
                    m.getNombre(),
                    m.getUnidad(),
                    m.getCantidad()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
    
    private void ajustarTabla() {
        this.getColumnModel().getColumn(0).setMaxWidth(40);
        this.getColumnModel().getColumn(2).setMaxWidth(50);
        this.getColumnModel().getColumn(3).setMaxWidth(60);
    }
    
    public Long getId() {
        int selectedRow = this.getSelectedRow();
        if (selectedRow >= 0) {
            return (Long) this.getValueAt(selectedRow, 0); // Columna 1 es el nombre
        }
        return null;
    }
}
