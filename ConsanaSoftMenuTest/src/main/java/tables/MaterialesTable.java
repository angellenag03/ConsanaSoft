
package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import utils.HTTPManager;

public class MaterialesTable extends JTable {
    private final HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private Gson gson;
    
    public MaterialesTable() {
        initComponents();
        cargarDatosIniciales();
        ajustarTabla();
    }
    
    public MaterialesTable(String query) {
        initComponents();
        cargarDatosPorNombre(query);
        ajustarTabla();
    }
    
    private void initComponents() {
        this.gson = new Gson();
        model = new DefaultTableModel(
                new Object[] {
                    "ID",
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
    }
    
    public void cargarDatosPorNombre(String nombre) {
        try {
            nombre = URLEncoder.encode(nombre, "UTF-8");
            cargarDatos("/material/list?nombre="+nombre);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void cargarDatosIniciales() {
        cargarDatos("/material/list");
    }
    
    private void cargarDatos(String endpoint) {
        try {
            model.setRowCount(0);
            String response = http.executeRequest(endpoint);
            Type materialesJson = new TypeToken<List<MaterialDTO>>(){}.getType();
            List<MaterialDTO> materiales = gson.fromJson(response, materialesJson);
            
            for (MaterialDTO m : materiales) {
                model.addRow(new Object[]{
                    m.getId(),
                    m.getNombre(),
                    m.getUnidad()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void ajustarTabla() {
        this.getColumnModel().getColumn(0).setMaxWidth(40);
        this.getColumnModel().getColumn(2).setMaxWidth(50);
    }
}
