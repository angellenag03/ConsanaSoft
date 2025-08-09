
package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class FechasValesTable extends BaseTable {
    private String obraId;
    
    public FechasValesTable(String obraId) {
        super();
        this.obraId = obraId;
        initializeModel(new String[] {
            "Fechas",
        });
        cargarDatosIniciales();
    }
    
    @Override
    public void cargarDatosIniciales() {
        cargarDatos(obraId);
    }

    private void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest(
                    "/historial/fechas?obraId="+endpoint);
            Type vale = new TypeToken<List<String>>(){}.getType();
            List<String> fechas = gson.fromJson(response, vale);
            
            for(String f : fechas) {
                model.addRow(new Object[]{f});
            }
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos FechasValesTable");
        }
    }
    
    @Override
    protected void ajustarTabla() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
