package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.HistorialMaterialDTO;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class HistorialMaterialTable extends BaseTable {
    
    public HistorialMaterialTable() {
        super(); // Llamar al constructor de BaseTable
        
        // Inicializar el modelo con las columnas específicas
        initializeModel(new String[] {
            "Fecha de Registro",
            "Cantidad",
            "Origen",
            "Referencia",
            "N. de Documento",
            "Fecha de Origen"
        });
        
        // Configurar el renderer personalizado para la columna de Cantidad (columna 1)
        this.getColumnModel().getColumn(1).setCellRenderer(new CantidadRenderer());
        
        ajustarTabla();
    }
    
    @Override
    public void cargarDatosIniciales() {
        // Esta tabla no carga datos inicialmente
        clearTable();
    }
    
    public void cargarDatosNombre(String nombre) {
        try {
            nombre = URLEncoder.encode(nombre, "UTF-8");
            cargarDatos("/historial/list?nombre="+nombre);
            packColumns(); // Ajustar el ancho de las columnas después de cargar los datos
        } catch (Exception e) {
            handleError(e, "cargarDatosNombre HistorialMaterialTable");
        }
    }
    
    public void cargarDatosNombreObra(String obraId, String nombre) {
        try {
            nombre = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
            obraId = URLEncoder.encode(obraId, StandardCharsets.UTF_8);
            cargarDatos("/historial/list?obraId="+obraId+"&nombre="+nombre);
            packColumns(); // Ajustar el ancho de las columnas después de cargar los datos
        } catch (Exception e) {
            handleError(e, "cargarDatosNombreObra HistorialMaterialTable");
        }
    }
    
    private void cargarDatos(String endpoint) { 
        try {
            clearTable();
            String response = http.executeRequest(endpoint);
            Type historialJson = new TypeToken<List<HistorialMaterialDTO>>(){}.getType();
            List<HistorialMaterialDTO> historial = gson.fromJson(response, historialJson);
            
            for(HistorialMaterialDTO h : historial) {
                model.addRow(new Object[]{
                    h.getFechaRegistro(),
                    h.getCantidad(),
                    h.getOrigen(),
                    h.getReferencia(),
                    h.getNumeroDocumento(),
                    h.getFechaOrigen()
                });
            }
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos HistorialMaterialTable");
        }
    }
    
    // Método para ajustar automáticamente el ancho de las columnas
    private void packColumns() {
        for (int i = 0; i < this.getColumnCount(); i++) {
            TableColumn column = this.getColumnModel().getColumn(i);
            int maxWidth = 0;
            
            // Obtener el ancho máximo del encabezado
            Component headerComp = this.getTableHeader().getDefaultRenderer()
                .getTableCellRendererComponent(this, column.getHeaderValue(), false, false, 0, i);
            maxWidth = headerComp.getPreferredSize().width;
            
            // Obtener el ancho máximo del contenido de la columna
            for (int row = 0; row < this.getRowCount(); row++) {
                Component comp = this.prepareRenderer(this.getCellRenderer(row, i), row, i);
                maxWidth = Math.max(comp.getPreferredSize().width + 10, maxWidth); // +10 para un pequeño margen
            }
            
            // Establecer el ancho de la columna
            column.setPreferredWidth(maxWidth);
        }
    }
    
    @Override
    protected void ajustarTabla() {
        // Esta tabla ajusta sus columnas automáticamente con packColumns()
        // No necesita configuración fija de anchos
    }
    
    /**
     * Renderer personalizado para la columna de Cantidad que muestra números negativos en rojo
     * y mantiene los colores alternados de las columnas
     */
    private class CantidadRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Aplicar colores alternados por columna si no está seleccionada
            if (!isSelected) {
                if (column % 2 == 0) { // Columna par
                    c.setBackground(new Color(245, 245, 245));
                } else { // Columna impar
                    c.setBackground(Color.WHITE);
                }
            }
            
            try {
                // Verificar si el valor es un número negativo
                if (value != null) {
                    String strValue = value.toString();
                    if (!strValue.isEmpty()) {
                        double cantidad = Double.parseDouble(strValue);
                        if (cantidad < 0) {
                            c.setForeground(Color.RED);
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                c.setForeground(Color.BLACK);
            }
            
            return c;
        }
    }
}