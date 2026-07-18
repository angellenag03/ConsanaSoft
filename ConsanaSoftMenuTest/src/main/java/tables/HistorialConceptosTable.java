
package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.HistorialConceptoDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

public class HistorialConceptosTable extends BaseTable {

    public HistorialConceptosTable() {
        super();
        // Ej: 17/07/2026, 09:00:00 | Se {ESTADO} {CANTIDAD} a la obra
        initializeModel(new String[] {
            "Registro"
        });
    }
    
    @Override
    public void cargarDatosIniciales() {
        // Esta tabla no carga datos inicialmente
        clearTable();
    }

    public void cargarDatosConceptoObra(Long conceptoId, String obraId) {
        try {
            obraId = URLEncoder.encode(obraId, "UTF-8");
            cargarDatos("/historial/concepto/list?conceptoId="+conceptoId+"&obra="+obraId);
        } catch (Exception e) {
            handleError(e, "cargarDatosConceptoObra HistorialConceptosTable");
        }
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest(endpoint);
            Type historialJson = new TypeToken<List<HistorialConceptoDTO>>(){}.getType();
            List<HistorialConceptoDTO> historial = gson.fromJson(response, historialJson);
            
            String initTxt; 
            for(HistorialConceptoDTO h : historial) {
                
                switch(h.getEstado().charAt(0)) {
                    case 'A': initTxt = "Se añadieron "; break;
                    case 'I': initTxt = "Se instalaron "; break;
                    case 'D': initTxt = "Se desinstalaron "; break;
                    case 'R': initTxt = "Se removieron "; break;
                    default: initTxt = 
                            "De alguna forma logró salir "
                            + "este texto y eso no es normal... "; 
                };
                
                String registro = initTxt+h.getCantidadInstalada()+" en la obra";
                model.addRow(new Object[]{
                    h.getFechaRegistro()+" | "+registro
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos HistorialMaterialTable");
        }
    }
    
    public String getConceptoID() {
        Object value = getSelectedValue(6);
        return value != null ? value.toString() : null;
    }
    
    
    
    @Override
    protected void ajustarTabla() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
