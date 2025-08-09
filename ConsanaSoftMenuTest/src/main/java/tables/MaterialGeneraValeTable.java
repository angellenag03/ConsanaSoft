
package tables;

import com.google.gson.reflect.TypeToken;
import dto.MaterialGeneraValeDTO;
import dto.MaterialObraDTO;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class MaterialGeneraValeTable extends BaseTable {
    private String id;
    
    public MaterialGeneraValeTable(String id) {
        super();
        this.id = id;
        
        initializeModel(new String[]{
            "ID",
            "Clave",
            "Nombre",
            "Unidad",
            "Existente",
            "A Instalar"
        });
        
        cargarDatosIniciales();
        ajustarTabla();
        configurarEditorCantidad();
    }

    @Override
    public void cargarDatosIniciales() {
        cargarDatos("/materiales-obra/?obraId="+id);
    }

    private void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest(endpoint);
            Type materialesObra = new TypeToken<List<MaterialObraDTO>>(){}.getType();
            List<MaterialObraDTO> materiales = gson.fromJson(response, materialesObra);
            for (MaterialObraDTO m : materiales) {
                if(!m.getCantidadExistente().equals("0.0")) {
                    model.addRow(new Object[]{
                        m.getId(),
                        m.getClave(),
                        m.getNombre(),
                        m.getUnidad(),
                        m.getCantidadExistente(),
                        0.0 // Cantidad a instalar en 0 por defecto
                    });
                }
            }
        } catch (Exception e) {
            handleError(e, "cargarDatos MaterialGeneraValeTable");
        }
    }
    
    // método para hacer editable solo la columna de cantidad
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 5; // Solo editable la columna "Por Instalar" (índice 5)
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

        // Asigna el editor solo a la columna de Por Instalar
        getColumnModel().getColumn(5).setCellEditor(editor);

        // Esto evita que otras columnas sean editables
        setDefaultEditor(Object.class, null);
    }

    @Override
    protected void ajustarTabla() {
        setColumnMinWidth(0, 0);  // ID
        setColumnMaxWidth(0, 0);  // ID
        
        setColumnMaxWidth(1, 100);   // Clave
        setColumnMaxWidth(3, 60);   // Unidad
        setColumnMaxWidth(4, 100);   // Existente
        setColumnMaxWidth(5, 100);   // Por Instalar
    }

    public String getId() {
        return id;
    }
    
    public List<Map<String, Object>> getMateriales() {
        List<Map<String, Object>> materiales = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            Double cantidad = (Double) model.getValueAt(i, 5);
            if (cantidad > 0.0) {  
                Map<String, Object> material = new HashMap<>();
                material.put("materialId", model.getValueAt(i, 0));
                material.put("cantidad", cantidad);
                materiales.add(material);
            }
        }

        return materiales;
    }
    
}
