package tables;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.HistorialMaterialDTO;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import utils.HTTPManager;

public class HistorialMaterialTable extends JTable {
    private final HTTPManager http = HTTPManager.getInstance();
    private DefaultTableModel model;
    private Gson gson;
    
    public HistorialMaterialTable() {
        this.gson = new Gson();
        model = new DefaultTableModel(
                new Object[] {
                    "Fecha de Registro",
                    "Cantidad",
                    "Origen",
                    "Referencia",
                    "N. de Documento",
                    "Fecha de Origen"
                }, 0) 
        {
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
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar el renderer personalizado para la columna de Cantidad (columna 1)
        this.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
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
        });
    }
    
    public void cargarDatosNombre(String nombre) {
        try {
            nombre = URLEncoder.encode(nombre, "UTF-8");
            System.out.println("/historial/list?nombre="+nombre);
            cargarDatos("/historial/list?nombre="+nombre);
            packColumns(); // Ajustar el ancho de las columnas después de cargar los datos
        } catch (Exception e) {
            e.getMessage();
        }
    }
    
    private void cargarDatos(String endpoint) { 
        try {
            model.setRowCount(0);
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
            System.err.println(e.getMessage());
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
}