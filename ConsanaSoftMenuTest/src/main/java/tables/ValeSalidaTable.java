
package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialSinIdDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ValeSalidaTable extends BaseTable {
    private String obraId;
    
    public ValeSalidaTable(String obraId) {
        super();
        this.obraId = obraId;
        
        initializeModel(new String[] {
            "Nombre",
            "Unidad",
            "Cantidad"
        });
        cargarDatosIniciales();
        ajustarTabla();
    }
    
    @Override
    public void cargarDatosIniciales() {
        clearTable();
    }
    
    public void cargarDatosHoy() {
        cargarDatos(obraId);
    }
    
    public void cargarDatosSesion(String fecha) {
        cargarDatos(obraId+"&fecha="+fecha);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest("/historial/vale?obraId="+endpoint);
            Type vale = new TypeToken<List<MaterialSinIdDTO>>(){}.getType();
            List<MaterialSinIdDTO> materialesVale = gson.fromJson(response, vale);
            
            for (MaterialSinIdDTO m : materialesVale) {
                model.addRow(new Object[]{
                    m.getNombre(),
                    m.getUnidad(),
                    m.getCantidad()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos ValeSalidaTable");
        }
    }
     
    @Override
    protected void ajustarTabla() {
        setColumnMaxWidth(0, 200);  // Nombre
        setColumnMaxWidth(1, 120); // Unidad
        setColumnMaxWidth(2, 60);  // Cantidad
    }
    
}
