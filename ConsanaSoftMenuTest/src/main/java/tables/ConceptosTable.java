package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ConceptoDTO;
import java.awt.Component;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class ConceptosTable extends BaseTable {
    
    public ConceptosTable() {
        super(); // Llamar al constructor de BaseTable
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[]{
            "ID",
            "Clave",
            "Nombre",
            "Unidad"
        });
        
        ajustarTabla();
        cargarDatosIniciales();
    }
    
    // Override del renderer para manejar el texto con saltos de línea en la columna "nombre"
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        // Primero obtener el componente base con colores alternados
        Component component = super.prepareRenderer(renderer, row, column);
        
        if (column == 2) { // Solo para la columna "nombre"
            JTextArea textArea = new JTextArea();
            textArea.setText((String) getValueAt(row, column));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(new EmptyBorder(4, 4, 4, 4));
            textArea.setFont(getFont());
            textArea.setSize(getColumnModel().getColumn(column).getWidth(), getRowHeight(row));
            
            // Aplicar el color de fondo correcto basado en la fila (no la columna)
            if (!isRowSelected(row)) {
                textArea.setBackground(getRowBackgroundColor(row));
                textArea.setForeground(java.awt.Color.BLACK);
            }
            
            // Ajustar altura de la fila según el contenido
            int preferredHeight = textArea.getPreferredSize().height;
            if (getRowHeight(row) != preferredHeight) {
                setRowHeight(row, preferredHeight);
            }
            
            return textArea;
        }
        
        return component;
    }
    
    @Override
    public void cargarDatosIniciales() {
        cargarDatos("/concepto/list");
    }
    
    public void cargarDatosPorId(String id) {
        try {
            if(!id.matches("[0-9]")) {
                JOptionPane.showMessageDialog(null, 
                    "Favor de ingresar un valor númerico", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                
                return;
            }
            
            clearTable();
            String conceptoJson = http.executeRequest("/concepto/"+id);
            ConceptoDTO concepto = gson.fromJson(conceptoJson, ConceptoDTO.class);
            
            model.addRow(new Object[]{
                concepto.getId(),
                concepto.getClave(),
                concepto.getNombre(),
                concepto.getUnidad()
            });
        } catch (JsonSyntaxException | IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, 
                    "Error al cargar el concepto: \n" + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            });
            handleError(e, "cargarDatosPorId ConceptosTable");
        }
    }
    
    public void cargarDatosPorClave(String clave) {
        cargarDatos("/concepto/list?clave="+clave);
    }
    
    public void cargarDatosPorNombre(String nombre) {
        cargarDatos("/concepto/list?nombre="+nombre);
    }
    
    private void cargarDatos(String endpoint) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                clearTable();
                try {
                    List<Map<String, String>> conceptos = gson.fromJson(
                        http.executeRequest(endpoint), 
                        new TypeToken<List<Map<String, String>>>(){}.getType());
                    
                    for (Map<String, String> concepto : conceptos) {
                        model.addRow(new Object[]{
                            concepto.get("id"),
                            concepto.get("clave"),
                            concepto.get("nombre"),
                            concepto.get("unidad")
                        });
                    }
                } catch (JsonSyntaxException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, 
                            "Error al cargar los conceptos: \n" + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    public String getSelectedConceptoId() {
        return getSelectedId(); // Usar método de la clase base
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnMaxWidth(0, 40);           // ID
        setColumnMinWidth(1, 120);          // Clave - mínimo
        setColumnMaxWidth(1, 120);          // Clave - máximo
        setColumnMaxWidth(3, 60);           // Unidad
        
        // Altura inicial basada en la fuente
        this.setRowHeight(this.getFontMetrics(this.getFont()).getHeight() + 4);
    }
}