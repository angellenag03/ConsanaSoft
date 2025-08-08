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
            "ID",
            "Clave",
            "Nombre",
            "Unidad",
            "Requerido",
            "Sumininstrado",
            "Por Suministrar",
            "Instalar",
            "Existente"
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
                    m.getClave(),
                    m.getNombre(),
                    m.getUnidad(),
                    m.getCantidadRequerida(),
                    m.getCantidadSuministrada(),
                    m.getCantidadPendiente(),
                    m.getCantidadInstalada(),
                    m.getCantidadExistente()
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
    
    public String getClave() {
        Object value = getSelectedValue(1);
        return value != null ? value.toString() : null;
    }
    
    public String getNombre() {
        Object value = getSelectedValue(2);
        return value != null ? value.toString() : null;
    }
    
    @Override
    protected void ajustarTabla() {
        // Configurar anchos máximos y mínimos
        setColumnMinWidth(0, 0);  // ID
        setColumnMaxWidth(0, 0);  // ID
                
        setColumnMaxWidth(1, 100);   // Clave
        setColumnMaxWidth(3, 50);   // Unidad
        setColumnMaxWidth(4, 50);   // Requerido
        setColumnMaxWidth(5, 50);   // Suministrado
        setColumnMaxWidth(6, 65);   // Pendiente
        setColumnMaxWidth(7, 45);   // Instalado
        setColumnMaxWidth(8, 45);   // Existente
        
        setColumnMinWidth(1, 100);   // Clave
        setColumnMinWidth(3, 50);   // Unidad
        setColumnMinWidth(4, 50);   // Requerido
        setColumnMinWidth(5, 50);   // Suministrado
        setColumnMinWidth(6, 65);   // Pendiente
        setColumnMinWidth(7, 45);   // Instalado
        setColumnMinWidth(8, 45);   // Existente
    }
}                                                                                                          