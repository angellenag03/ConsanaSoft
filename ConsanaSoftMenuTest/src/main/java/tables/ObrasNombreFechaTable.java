package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ObraDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ObrasNombreFechaTable extends BaseTable {
    
    public ObrasNombreFechaTable() {
        super(); // Llamar al constructor de BaseTable
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[]{
            "Número de Obra", 
            "Nombre", 
            "Ú. Fecha de Modificación"
        });
        
        ajustarTabla();
        cargarDatosIniciales();
        this.repaint();
    }
    
    @Override
    public void cargarDatosIniciales() {
        cargarDatos("/obra/list");
    }
    
    public void cargarDatosPorID(String id) {
        cargarDatos("/obra/list?id="+id);
    }
    
    public void cargarDatosPorNombre(String nombre) {
        cargarDatos("/obra/list?nombre="+nombre);
    }
    
    /**
     * La idea original era tener varios métodos porque habrán querys de
     * búsqueda por lo que considero que sería mejor tener un método
     * universal y solo introducir la solicitud directamente en el método
     * @param endpoint 
     */
    public void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest(endpoint);
            Type obraListType = new TypeToken<List<ObraDTO>>(){}.getType();
            List<ObraDTO> obras = gson.fromJson(response, obraListType);
            
            for (ObraDTO o : obras) {
                model.addRow(new Object[]{
                    o.getId(),
                    o.getNombre(),
                    o.getFechaModificacion()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos ObrasNombreFechaTable");
        }
    }
    
    // Método para obtener el ID de la obra seleccionada
    public String getSelectedObraId() {
        return getSelectedId(); // Usar método de la clase base
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnPreferredWidth(0, 100);  // Número de Obra
        setColumnPreferredWidth(1, 280);  // Nombre
        setColumnPreferredWidth(2, 150);  // Ú. Fecha de Modificación
    }
}