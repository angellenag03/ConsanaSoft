
package tables;

import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableModel;
import utils.HTTPManager;

/**
 * Clase base para todas las tablas del sistema.
 * Proporciona funcionalidad común como el HTTPManager, Gson, 
 * configuración básica de la tabla con filas alternadas y encabezado personalizado.
 */
public abstract class BaseTable extends JTable {
    
    // Instancias compartidas
    protected final HTTPManager http = HTTPManager.getInstance();
    protected final Gson gson = new Gson();
    protected DefaultTableModel model;
    
    // Configuración de colores
    private static final Color COLOR_FILA_IMPAR = Color.WHITE;
    private static final Color COLOR_FILA_PAR = new Color(245, 245, 245);
    private static final Color COLOR_ENCABEZADO = new Color(205, 205, 205);
    private static final Color COLOR_TEXTO_ENCABEZADO = Color.BLACK;
    private static final Color COLOR_BORDE = new Color(220, 220, 220);
    
    /**
     * Constructor base que inicializa la configuración común de todas las tablas
     */
    protected BaseTable() {
        configurarTablaBase();
    }
    
    /**
     * Configuración básica común para todas las tablas
     */
    private void configurarTablaBase() {
        // Configuración básica de la tabla
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setShowGrid(true);
        this.setGridColor(COLOR_BORDE);
        this.setRowHeight(25);
        
        // Configurar renderers para filas alternadas
        this.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        this.setDefaultRenderer(String.class, new AlternatingRowRenderer());
    }
    
    /**
     * Configura el estilo del encabezado de la tabla
     */
    private void configurarEncabezado() {
        JTableHeader header = this.getTableHeader();
        if (header != null) {
            header.setBackground(COLOR_ENCABEZADO);
            header.setForeground(COLOR_TEXTO_ENCABEZADO);
//            header.setFont(header.getFont().deriveFont(Font.BOLD));
            header.setReorderingAllowed(false);
            
            // Forzar repintado
            header.repaint();
        }
    }
    
    /**
     * Inicializa el modelo de la tabla con las columnas especificadas.
     * @param columnNames Array con los nombres de las columnas
     */
    protected void initializeModel(String[] columnNames) {
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        this.setModel(model);
        
        // Configurar el encabezado
        configurarEncabezado();
    }
    
    /**
     * Limpia todos los datos de la tabla
     */
    protected void clearTable() {
        if (model != null) {
            model.setRowCount(0);
        }
    }
    
    /**
     * Obtiene el valor de una celda en la fila seleccionada
     * @param column Índice de la columna
     * @return Valor de la celda o null si no hay selección
     */
    protected Object getSelectedValue(int column) {
        int selectedRow = this.getSelectedRow();
        if (selectedRow >= 0 && column >= 0 && column < this.getColumnCount()) {
            return this.getValueAt(selectedRow, column);
        }
        return null;
    }
    
    /**
     * Obtiene el ID de la fila seleccionada (asumiendo que está en la primera columna)
     * @return ID como String o null si no hay selección
     */
    public String getSelectedId() {
        Object value = getSelectedValue(0);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Configura el ancho máximo de una columna
     * @param columnIndex Índice de la columna
     * @param maxWidth Ancho máximo
     */
    protected void setColumnMaxWidth(int columnIndex, int maxWidth) {
        if (columnIndex >= 0 && columnIndex < this.getColumnCount()) {
            this.getColumnModel().getColumn(columnIndex).setMaxWidth(maxWidth);
        }
    }
    
    /**
     * Configura el ancho mínimo de una columna
     * @param columnIndex Índice de la columna
     * @param minWidth Ancho mínimo
     */
    protected void setColumnMinWidth(int columnIndex, int minWidth) {
        if (columnIndex >= 0 && columnIndex < this.getColumnCount()) {
            this.getColumnModel().getColumn(columnIndex).setMinWidth(minWidth);
        }
    }
    
    /**
     * Configura el ancho preferido de una columna
     * @param columnIndex Índice de la columna
     * @param preferredWidth Ancho preferido
     */
    protected void setColumnPreferredWidth(int columnIndex, int preferredWidth) {
        if (columnIndex >= 0 && columnIndex < this.getColumnCount()) {
            this.getColumnModel().getColumn(columnIndex).setPreferredWidth(preferredWidth);
        }
    }
    
    /**
     * Renderer personalizado para filas alternadas
     */
    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                component.setBackground(row % 2 == 0 ? COLOR_FILA_IMPAR : COLOR_FILA_PAR);
                component.setForeground(Color.BLACK);
            }
            
            return component;
        }
    }
    
    public abstract void cargarDatosIniciales();
    protected abstract void ajustarTabla();
    
    /**
     * Manejo de errores
     * @param e Excepción
     * @param context Contexto del error
     */
    protected void handleError(Exception e, String context) {
        System.err.println("Error en " + context + ": " + e.getMessage());
        e.printStackTrace();
    }
}