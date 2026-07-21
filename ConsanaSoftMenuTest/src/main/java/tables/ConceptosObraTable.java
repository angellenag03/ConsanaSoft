package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.ConceptoObraDTO;
import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class ConceptosObraTable extends BaseTable {
    private final String obraId;
    
    public ConceptosObraTable(String obraId) {
        super(); // Llamar al constructor de BaseTable
        this.obraId = obraId;
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[] {
            "Partida",
            "Nombre",
            "Unidad",
            "Cantidad",
            "Instalado",
            "Por Instalar",
            "ID"
        });
        
        ajustarTabla();
        cargarDatosIniciales();
    }
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        
        if (column == 1) { // Solo para la columna "nombre"
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
        cargarDatos("/concepto-obra/list?id="+obraId);
    }
    
    private void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest(endpoint);
            Type obraListType = new TypeToken<List<ConceptoObraDTO>>(){}.getType();
            List<ConceptoObraDTO> conceptosObra = gson.fromJson(response, obraListType);
            
            for (ConceptoObraDTO co : conceptosObra) {
                model.addRow(new Object[]{
                    co.getPartida(),
                    co.getNombre(),
                    co.getUnidad(),
                    co.getCantidad(),
                    co.getInstalado(),
                    co.getPorInstalar(),
                    co.getId()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos ConceptosObraTable");
        }
    }
    
    @Override
    protected void ajustarTabla() {
        setColumnPreferredWidth(0, 30);    // Partida
        setColumnPreferredWidth(1, 1000);  // Nombre
        setColumnPreferredWidth(2, 20);    // Unidad
        setColumnPreferredWidth(3, 20);    // Cantidad
        setColumnPreferredWidth(4, 20);    // Instalado
        setColumnPreferredWidth(5, 20);    // Por Instalar
        setColumnMinWidth(6, 0);
        setColumnMaxWidth(6, 0); // ID
    }
    
    public Long getConceptoId() {
        Object value = getSelectedValue(6);
        return value != null ? Long.parseLong(value.toString()) : null;
    }
    
    public int getCantidad() {
        Object value = getSelectedValue(3);
        return value != null ? Integer.parseInt(value.toString()) : null;
    }
    
    public int getInstalado() {
        Object value = getSelectedValue(4);
        return value != null ? Integer.parseInt(value.toString()) : null;
    }
    
    public boolean isFullInstalado() {
        return getCantidad() == getInstalado();
    }
}
