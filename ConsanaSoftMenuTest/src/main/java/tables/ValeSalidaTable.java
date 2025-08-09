
package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialSinIdDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

public class ValeSalidaTable extends BaseTable {
    private String obraId;
    
    public ValeSalidaTable() {
        super();
        initializeModel(new String[] {
            "Clave",
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
    
    public void cargarDatosFechaHora(String obraId, String fechaHora) {
        cargarDatos(obraId, fechaHora);
    }
    
    private void cargarDatos(String endpoint1, String endpoint2) {
        try {
            clearTable();
            
            String obraIdEncoded = URLEncoder.encode(endpoint1, "UTF-8");
            String fechaHoraEncoded = URLEncoder.encode(endpoint2, "UTF-8");
            
            String response = http.executeRequest(
                    "/historial/vale?obraId="+obraIdEncoded+"&fechaHora="+fechaHoraEncoded);
            Type vale = new TypeToken<List<MaterialSinIdDTO>>(){}.getType();
            List<MaterialSinIdDTO> materialesVale = gson.fromJson(response, vale);
            
            for (MaterialSinIdDTO m : materialesVale) {
                model.addRow(new Object[]{
                    m.getClave(),
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
        setColumnMaxWidth(0, 120); // Clave
//        setColumnMaxWidth(1, 200);  // Nombre
        setColumnMaxWidth(2, 120); // Unidad
        setColumnMaxWidth(3, 60);  // Cantidad
    }
    
    // En ValeSalidaTable.java, añade este método:
    public void cargarDatosDesdeMaterialGeneraVale(MaterialGeneraValeTable sourceTable) {
        clearTable();
        for (int i = 0; i < sourceTable.getRowCount(); i++) {
            Double cantidad = (Double) sourceTable.getValueAt(i, 5); // Columna "A Instalar"
            if (cantidad > 0.0) {
                model.addRow(new Object[]{
                    sourceTable.getValueAt(i, 1), // Clave
                    sourceTable.getValueAt(i, 2), // Nombre
                    sourceTable.getValueAt(i, 3), // Unidad
                    cantidad                     // Cantidad
                });
            }
        }
    }
}
