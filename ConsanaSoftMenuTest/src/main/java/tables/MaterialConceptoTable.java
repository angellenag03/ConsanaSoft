package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialOutputDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MaterialConceptoTable extends BaseTable {
    private String id; // id del concepto
    
    public MaterialConceptoTable(String id) {
        super(); // Llamar al constructor de BaseTable
        this.id = id;
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[] {
            "Nombre",
            "Unidad",
            "Cantidad"
        });
        
        ajustarTabla();
        cargarDatosIniciales();
    }
    
    @Override
    public void cargarDatosIniciales() {
        cargarDatos("/material-concepto/id/"+id);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable();
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
            handleError(e, "cargarDatos MaterialConceptoTable");
        }
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnMaxWidth(1, 50);  // Unidad
        setColumnMaxWidth(2, 60);  // Cantidad
    }
}