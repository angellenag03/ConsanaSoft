package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.MaterialOutputDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class MaterialConceptoTable extends BaseTable {
    private String id; // id del concepto
    
    public MaterialConceptoTable(String id) {
        super(); // Llamar al constructor de BaseTable
        this.id = id;
        
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
    
    public MaterialConceptoTable() {
        super(); 
        this.id = null;
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[] {
            "ID",
            "Clave",
            "Nombre",
            "Unidad",
            "Cantidad"
        });
        
        ajustarTabla();
        // Configurar editor para la columna Cantidad (solo valores Double)
        configurarEditorCantidad();
    }
    
    // método para hacer editable solo la columna de cantidad
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 4; // Solo editable la columna "Cantidad" (índice 4)
    }

    private void configurarEditorCantidad() {
        JTextField textField = new JTextField();
        textField.setHorizontalAlignment(JTextField.RIGHT); // Alineación derecha para números

        DefaultCellEditor editor = new DefaultCellEditor(textField) {
            @Override
            public boolean stopCellEditing() {
                try {
                    Double.parseDouble(textField.getText());
                    return super.stopCellEditing();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, 
                        "Ingrese un valor numérico válido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            @Override
            public Object getCellEditorValue() {
                try {
                    return Double.parseDouble(textField.getText());
                } catch (NumberFormatException e) {
                    return 0.0; // Valor por defecto si hay error
                }
            }
        };

        // Asigna el editor solo a la columna de Cantidad
        getColumnModel().getColumn(4).setCellEditor(editor);

        // Esto evita que otras columnas sean editables
        setDefaultEditor(Object.class, null);
    }
        
    /**
     * Inserta un nuevo material como fila en la tabla
     * @param dto MaterialOutputDTO a insertar
     */
    public void setMaterial(MaterialOutputDTO dto) {
        if (dto != null) {
            model.addRow(new Object[]{
                dto.getId(),
                dto.getClave(),
                dto.getNombre(),
                dto.getUnidad(),
                dto.getCantidad()
            });
        }
    }
    
    /**
    * 
    * @param row the index of the row to remove
    */
    public void removeRow(int row) {
        if (row >= 0 && row < model.getRowCount()) {
            model.removeRow(row);
        }
    }
    
    /**
     * Obtiene una lista con ID y cantidad de todos los materiales en la tabla
     * @return List<Map<String, Object>> donde cada Map contiene "id" y "cantidad"
     */
    public List<Map<String, Object>> getMateriales() {
        List<Map<String, Object>> materiales = new ArrayList<>();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Map<String, Object> material = new HashMap<>();
            material.put("id", model.getValueAt(i, 0)); // ID está en columna 0
            material.put("cantidad", model.getValueAt(i, 4)); // Cantidad está en columna 3
            materiales.add(material);
        }
        
        return materiales;
    }
    
    @Override
    public void cargarDatosIniciales() {
        if (id != null) {
            cargarDatos("/material-concepto/id/" + id);
        }
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest(endpoint);
            Type materialesConcepto = new TypeToken<List<MaterialOutputDTO>>(){}.getType();
            List<MaterialOutputDTO> materiales = gson.fromJson(response, materialesConcepto);
            
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
            handleError(e, "cargarDatos MaterialConceptoTable");
        }
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnMinWidth(0, 0);  // ID
        setColumnMaxWidth(0, 0);  // ID
        
        setColumnMaxWidth(1, 100); // Clave
        setColumnMaxWidth(2, 50);  // Unidad
        setColumnMaxWidth(3, 60);  // Cantidad
    }
}