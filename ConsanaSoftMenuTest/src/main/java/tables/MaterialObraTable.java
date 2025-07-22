package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialObraDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MaterialObraTable extends BaseTable {
    private String id;
    
    public MaterialObraTable(String id) {
        super(); // Llamar al constructor de BaseTable
        this.id = id; // número de obra
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[] {
            "Clave",
            "Nombre",
            "Unidad",
            "Requerido",
            "Suministrado",
            "Pendiente"
        });
        
        ajustarTabla();
        cargarDatosIniciales();
    }
    
    @Override
    public void cargarDatosIniciales() {
        cargarDatos("/materiales-obra/?obraId="+id);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable();
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
            handleError(e, "cargarDatos MaterialObraTable");
        }
    }
    
    public String getId() {
        Object value = getSelectedValue(0);
        return value != null ? value.toString() : null;
    }
    
    public String getNombre() {
        Object value = getSelectedValue(1);
        return value != null ? value.toString() : null;
    }
    
    @Override
    protected void ajustarTabla() {
        // Configurar anchos máximos y mínimos
        setColumnMaxWidth(0, 45);   // Clave
        setColumnMaxWidth(2, 50);   // Unidad
        setColumnMaxWidth(3, 78);   // Requerido
        setColumnMaxWidth(4, 90);   // Suministrado
        setColumnMaxWidth(5, 70);   // Pendiente
        
        setColumnMinWidth(0, 45);   // Clave
        setColumnMinWidth(2, 50);   // Unidad
        setColumnMinWidth(3, 78);   // Requerido
        setColumnMinWidth(4, 90);   // Suministrado
        setColumnMinWidth(5, 70);   // Pendiente
    }
}