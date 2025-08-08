package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialOutputDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AlmacenTable extends BaseTable {
    
    public AlmacenTable() {
        super(); // Llamar al constructor de BaseTable
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[] {
            "ID",
            "Clave",
            "Nombre", 
            "Unidad",
            "Cantidad"
        });
        
        ajustarTabla();
        cargarDatosIniciales();
    }
    
    @Override
    public void cargarDatosIniciales() {
        cargarDatos("/almacen/list");
    }
    
    public void cargarDatosNombre(String nombre) {
        nombre = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
        cargarDatos("/almacen/list?nombre="+nombre);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable(); // Usar método de la clase base
            String response = http.executeRequest(endpoint);
            Type historialMateriales = new TypeToken<List<MaterialOutputDTO>>(){}.getType();
            List<MaterialOutputDTO> materiales = gson.fromJson(response, historialMateriales);
            
            for (MaterialOutputDTO m : materiales) {
                model.addRow(new Object[]{
                    m.getId(),
                    m.getClave(),
                    m.getNombre(),
                    m.getUnidad(),
                    m.getCantidad()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos AlmacenTable");
        }
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnMinWidth(0, 0);  // ID
        setColumnMaxWidth(0, 0);  // ID

        setColumnMaxWidth(1, 100); // Clave
        setColumnMaxWidth(3, 60);  // Unidad
        setColumnMaxWidth(4, 70);  // Cantidad
        
//        setColumnMinWidth(1, 100); // Clave
//        setColumnMinWidth(3, 60);  // Unidad
//        setColumnMinWidth(4, 70);  // Cantidad
    }
    
    public Long getId() {
        Object value = getSelectedValue(0);
        return value != null ? Long.valueOf(value.toString()) : null;
    }
    
    public String getClave() {
        Object value = getSelectedValue(1);
        return value != null ? String.valueOf(value.toString()) : null;
    }
    
    public String getNombre() {
        Object value = getSelectedValue(2);
        return value != null ? String.valueOf(value.toString()) : null;
    }
    
    public String getUnidad() {
        Object value = getSelectedValue(3);
        return value != null ? String.valueOf(value.toString()) : null;
    }
}