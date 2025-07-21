
package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialOutputDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import utils.HTTPManager;

public class MaterialConceptoTable extends JTable {

    private final HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private Gson gson;
    private String id; // id del concepto
    
    public MaterialConceptoTable(String id) {
        this.gson = new Gson();
        this.id = id;
        model = new DefaultTableModel(
                new Object[] {
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
        
        cargarDatosIniciales();
        ajustarTabla();
    }
    
    public void cargarDatosIniciales() {
        cargarDatos("/material-concepto/id/"+id);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            model.setRowCount(0);
            String response = http.executeRequest(endpoint);
            Type materialesConcepto = new TypeToken<List<MaterialOutputDTO>>(){}.getType();
            List<MaterialOutputDTO> materiales = gson.fromJson(response, materialesConcepto);
            
            for (MaterialOutputDTO m : materiales) {
                model.addRow(new Object[]{
                    m.getNombre(),
                    m.getUnidad(),
                    m.getCantidad()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void ajustarTabla() {
        this.getColumnModel().getColumn(1).setMaxWidth(50);
        this.getColumnModel().getColumn(2).setMaxWidth(60);
    }
}
