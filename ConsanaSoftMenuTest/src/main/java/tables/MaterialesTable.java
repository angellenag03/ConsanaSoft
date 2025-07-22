package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

public class MaterialesTable extends BaseTable {
    
    public MaterialesTable() {
        super(); // Llamar al constructor de BaseTable
        initComponents();
        cargarDatosIniciales();
        ajustarTabla();
    }
    
    public MaterialesTable(String query) {
        super(); // Llamar al constructor de BaseTable
        initComponents();
        cargarDatosPorNombre(query);
        ajustarTabla();
    }
    
    private void initComponents() {
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[] {
            "ID",
            "Nombre",
            "Unidad"
        });
    }
    
    public void cargarDatosPorNombre(String nombre) {
        try {
            nombre = URLEncoder.encode(nombre, "UTF-8");
            cargarDatos("/material/list?nombre="+nombre);
        } catch (Exception e) {
            handleError(e, "cargarDatosPorNombre MaterialesTable");
        }
    }
    
    @Override
    public void cargarDatosIniciales() {
        cargarDatos("/material/list");
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable();
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
            handleError(e, "cargarDatos MaterialesTable");
        }
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnMaxWidth(0, 40);  // ID
        setColumnMaxWidth(2, 50);  // Unidad
    }
}