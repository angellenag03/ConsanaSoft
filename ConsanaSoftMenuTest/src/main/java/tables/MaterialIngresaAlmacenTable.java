
package tables;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class MaterialIngresaAlmacenTable extends BaseTable {

    public MaterialIngresaAlmacenTable() {
        super();
        
        initializeModel(new String[]{
            "ID",
            "Clave",
            "Nombre",
            "Unidad",
            "Cantidad"
        });
        ajustarTabla();
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

        // Asigna el editor solo a la columna de Por Instalar
        getColumnModel().getColumn(4).setCellEditor(editor);

        // Esto evita que otras columnas sean editables
        setDefaultEditor(Object.class, null);
    }
    
    // Añade un nuevo elemento pasando los datos necesarios desde la otra tabla
    public void agregarMaterial(Object id, Object clave, Object nombre, Object unidad) {
        // Verificamos si el elemento ya fue agregado previamente para evitar duplicados
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(id)) {
                JOptionPane.showMessageDialog(this, 
                    "El material ya se encuentra en la lista.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Se inserta la fila definiendo '0.0' en la columna Cantidad (índice 4)
        model.addRow(new Object[]{id, clave, nombre, unidad, 0.0});
    }

    // Remueve la fila seleccionada y la JTable reorganiza automáticamente el orden de las filas sobrantes
    public void eliminarMaterialSeleccionado() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
            // Detener la edición de celdas por si el usuario estaba editando 'Cantidad' al hacer clic en eliminar
            if (isEditing()) {
                getCellEditor().stopCellEditing();
            }
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un elemento de la tabla derecha para eliminar.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // se queda vacía ya que tiene que se le va a ingresar
    @Override
    public void cargarDatosIniciales() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getId(){
        return getSelectedId();
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnMinWidth(0, 0);  // ID
        setColumnMaxWidth(0, 0);  // ID
        
        setColumnMaxWidth(1, 100);   // Clave
        setColumnMaxWidth(3, 60);   // Unidad
        setColumnMaxWidth(4, 100);   // Cantidad
    }
    
}
