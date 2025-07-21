
package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ObraDTO;
import javax.swing.JTable;
import utils.HTTPManager;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class ObrasNombreFechaTable extends JTable{
    
    private HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private Gson gson;
    
    public ObrasNombreFechaTable() {
        this.gson = new Gson();
        model = new DefaultTableModel(
                new Object[]{
                    "Número de Obra", 
                    "Nombre", 
                    "Ú. Fecha de Modificación"
                }, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                    return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Todos los valores son String
                return String.class;
            }
        };
        this.setModel(model);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ajustarTabla();

        // este método se cargará con estos datos primero
        cargarDatosIniciales();
        this.repaint();
    }
    
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
     * la idea oiriginal era tener varios métodos porque habrán querys de
     * busqueda por lo que considero que sería mejor tener un método
     * universal y solo introducir la solicitud directamente en el método
     * @param endpoint 
    */
    public void cargarDatos(String endpoint) {
        try {
            model.setRowCount(0);
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
            e.printStackTrace();
        }
    }
    // Método para obtener el ID de la obra seleccionada
    public String getSelectedObraId() {
        int selectedRow = this.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) model.getValueAt(selectedRow, 0); // Columna 0 es el ID
        }
        return null;
    }
    
    private void ajustarTabla() {
        this.getColumnModel().getColumn(0).setPreferredWidth(100);
        this.getColumnModel().getColumn(1).setPreferredWidth(280);
        this.getColumnModel().getColumn(2).setPreferredWidth(150);
    }
    
}
