
package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ExistenciaDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ExistenciasTable extends BaseTable {

    private Double total;
    
    public ExistenciasTable() {
        super();
        initializeModel(new String[]{
            "Ubicación",
            "Existencias: N/A"
        });
        
        this.total = total;
        
        clearTable();
        ajustarTabla();
    }
    
    @Override
    public void cargarDatosIniciales() {
        clearTable();
    }

    public void cargarDatos(String id) {
        try {
            clearTable(); 
            total = 0.0;
            String response = http.executeRequest("/almacen/existencias?id="+id);
            Type existenciasJson = new TypeToken<List<ExistenciaDTO>>(){}.getType();
            List<ExistenciaDTO> existencias = gson.fromJson(response, existenciasJson);
            
            for(ExistenciaDTO e : existencias) {
                model.addRow(new Object[]{
                    e.getUbicacion(),
                    e.getExistente()
                });
                total = total + e.getExistente();
            }
            model.setColumnIdentifiers(new Object[]{"Ubicación","Existencias: "+total});
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos ExistenciasTable");
        }
    }
    
    @Override
    protected void ajustarTabla() {
        
    }
    
}
