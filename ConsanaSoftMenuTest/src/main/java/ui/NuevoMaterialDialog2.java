
package ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.SuministrarManyDTO;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import tables.MaterialIngresaAlmacenTable;
import tables.MaterialesTable;
import utils.FechaPicker;
import utils.HTTPManager;

public class NuevoMaterialDialog2 extends JDialog {
    private JTextField buscarField;
    private MaterialesTable materialesTable;
    private MaterialIngresaAlmacenTable materialIngresaAlmacenTable;
    
    private JLabel origenLabel;
    private JLabel numeroDocumentoLabel;
    private JLabel fechaRegistroLabel;
    private JLabel referenciaLabel;
    
    private JComboBox<String> origenBox;
    private JTextField numeroDocumentoField;
    private FechaPicker fechaOrigenPicker;
    private JTextField referenciaField;
    private JComboBox<String> obraComboBox;
    
    private JPanel materialesPanel;
    private JPanel materialesIngresarPanel;
    private JPanel datosOrigenPanel;
    
    private JPanel izqButtonsPanel;
    private JPanel derButtonsPanel;
    
    private JPanel referenciaContainer;
    private CardLayout referenciaLayout;
    
    private JButton addButton;
    private JButton delButton;
    private JButton guardarButton;
    private JButton cancelarButton;
    
    private JFrame parentFrame;
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson = new Gson();
    
    public NuevoMaterialDialog2(JFrame parentFrame) {
        super(parentFrame, "Nuevo Material", true);
        initComponents();
        setupLayout();
        setupBehavior();
        this.setSize(1000,600);
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }
    
    private void initComponents(){
        buscarField = new JTextField();
        buscarField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar");
        buscarField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSVGIcon("icons/search.svg"));
        origenLabel = new JLabel("Origen:");
        numeroDocumentoLabel = new JLabel("Número Documento:");
        fechaRegistroLabel = new JLabel("Fecha de Registro:");
        referenciaLabel = new JLabel("Referencia:");
        
        origenBox = new JComboBox<>(new String[]{"FACTURA", "VALE"});
        numeroDocumentoField = new JTextField(15);
        fechaOrigenPicker = new FechaPicker();
        referenciaField = new JTextField(20);
        obraComboBox = new JComboBox<>();
        
        referenciaLayout = new CardLayout();
        referenciaContainer = new JPanel(referenciaLayout);
        referenciaContainer.add(referenciaField, "FACTURA");
        referenciaContainer.add(obraComboBox, "VALE");
        
        addButton = new JButton("+ Añadir");
        delButton = new JButton("- Quitar");
        
        guardarButton = new JButton("Guardar", new FlatSVGIcon("icons/save.svg"));
        cancelarButton = new JButton("Cancelar", new FlatSVGIcon("icons/x.svg"));
        
        guardarButton.setEnabled(false);
        
        // CARGANDO LAS TABLAS
        materialesTable = new MaterialesTable();
        materialIngresaAlmacenTable = new MaterialIngresaAlmacenTable();
        
        showComponents(false);
    }
    private void setupLayout(){
        JPanel mainPanel = new JPanel(new BorderLayout());
        materialesPanel = new JPanel(new BorderLayout());
        materialesPanel.add(buscarField, BorderLayout.NORTH);
        materialesPanel.add(new JScrollPane(materialesTable), BorderLayout.CENTER);
        materialesPanel.setPreferredSize(new Dimension(400, 600));
        
        materialesIngresarPanel = new JPanel(new BorderLayout());
        materialesIngresarPanel.add(new JScrollPane(materialIngresaAlmacenTable), BorderLayout.CENTER);
        
        datosOrigenPanel = new JPanel(new GridBagLayout());
        datosOrigenPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        datosOrigenPanel.add(origenLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        datosOrigenPanel.add(origenBox, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        datosOrigenPanel.add(numeroDocumentoLabel, gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.7;
        datosOrigenPanel.add(numeroDocumentoField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        datosOrigenPanel.add(fechaRegistroLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        datosOrigenPanel.add(fechaOrigenPicker, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        datosOrigenPanel.add(referenciaLabel, gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.7;
        datosOrigenPanel.add(referenciaContainer, gbc);
        
        materialesIngresarPanel.add(datosOrigenPanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        izqButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        izqButtonsPanel.add(delButton);
        izqButtonsPanel.add(addButton);
        
        derButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        derButtonsPanel.add(cancelarButton, new FlatSVGIcon("icons/x.png"));
        derButtonsPanel.add(guardarButton, new FlatSVGIcon("icons/save.svg"));
        
        bottomPanel.add(izqButtonsPanel, BorderLayout.WEST);
        bottomPanel.add(derButtonsPanel, BorderLayout.EAST);
        
        mainPanel.add(materialesIngresarPanel, BorderLayout.CENTER);
        mainPanel.add(materialesPanel, BorderLayout.WEST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    private void setupBehavior(){
        cancelarButton.addActionListener(this::cancelar);
        origenBox.addActionListener(this::alterarLabels);

        // Escuchadores para los botones Añadir y Quitar
        addButton.addActionListener(e -> agregarMaterial());
        delButton.addActionListener(e -> quitarMaterial());

        guardarButton.addActionListener(this::guardarMateriales);
        
        buscarField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarBusqueda();
                }
            }
        });
    }

    private void guardarMateriales(ActionEvent e) {
        // 1. Validar que la tabla tenga filas registradas
        int totalFilas = materialIngresaAlmacenTable.getRowCount();
        if (totalFilas == 0) {
            JOptionPane.showMessageDialog(this, 
                "Debe agregar al menos un material a la lista.", 
                "Atención", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Detener la edición activa en la tabla (por si el usuario dejó el foco en una celda de Cantidad)
        if (materialIngresaAlmacenTable.isEditing()) {
            materialIngresaAlmacenTable.getCellEditor().stopCellEditing();
        }

        // 3. Extraer los datos del formulario y los arreglos de la tabla
        Long[] materialesId = new Long[totalFilas];
        Double[] cantidades = new Double[totalFilas];

        for (int i = 0; i < totalFilas; i++) {
            // Obtenemos el ID de la columna 0 y la Cantidad de la columna 4
            Object idVal = materialIngresaAlmacenTable.getValueAt(i, 0);
            Object cantVal = materialIngresaAlmacenTable.getValueAt(i, 4);

            materialesId[i] = Long.parseLong(idVal.toString());

            // Conversión segura de la cantidad a Double
            double cantidad = cantVal instanceof Number ? 
                ((Number) cantVal).doubleValue() : Double.parseDouble(cantVal.toString());

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Las cantidades deben ser mayores a 0 (Fila " + (i + 1) + ").", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            cantidades[i] = cantidad;
        }

        // 4. Obtener datos de la cabecera del formulario
        String origen = (String) origenBox.getSelectedItem();
        String numeroDocumento = numeroDocumentoField.getText().trim();

        String referencia = "";
        if ("VALE".equals(origen)) {
            referencia = (String) obraComboBox.getSelectedItem();
        } else {
            referencia = referenciaField.getText().trim();
        }

        // Ajusta la obtención de la fecha según cómo exponga el valor tu componente 'FechaPicker'
        String fechaOrigen = fechaOrigenPicker.getFecha(); // Formato esperado: "YYYY-MM-DD"

        // Validar campos obligatorios de cabecera
        if (numeroDocumento.isEmpty() || referencia == null || referencia.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos requeridos del formulario.", 
                "Campos Incompletos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 5. Construir DTO y enviar la petición HTTP
        try {
            SuministrarManyDTO dto = new SuministrarManyDTO(
                materialesId, cantidades, numeroDocumento, referencia, origen, fechaOrigen
            );

            String jsonBody = gson.toJson(dto);

            // Llamada a tu servicio HTTP
            // (Ajusta la invocación de 'executePost' u otro método HTTP según la API de tu HTTPManager)
            String respuesta = http.executeRequest(HTTPManager.HttpMethod.POST, "/almacen/suministrar-many", jsonBody);

            JOptionPane.showMessageDialog(this, 
                "Materiales ingresados correctamente al almacén.", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            dispose(); // Cerrar la ventana tras guardar con éxito

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al procesar el ingreso de materiales: " + ex.getMessage(), 
                "Error API", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Lógica del botón + Añadir
    private void agregarMaterial() {
        int selectedRow = materialesTable.getSelectedRow();

        if (selectedRow != -1) {
            // Extraemos los valores de la fila seleccionada de materialesTable
            Object id = materialesTable.getValueAt(selectedRow, 0);
            Object clave = materialesTable.getValueAt(selectedRow, 1);
            Object nombre = materialesTable.getValueAt(selectedRow, 2);
            Object unidad = materialesTable.getValueAt(selectedRow, 3);

            // Transferimos los datos a la tabla destino
            materialIngresaAlmacenTable.agregarMaterial(id, clave, nombre, unidad);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un material de la tabla izquierda para añadir.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    // Lógica del botón - Quitar
    private void quitarMaterial() {
        materialIngresaAlmacenTable.eliminarMaterialSeleccionado();
    }

    private void realizarBusqueda() {
        String nombre = buscarField.getText().trim();
        if (!nombre.isEmpty()) {
            materialesTable.cargarDatosPorNombre(nombre);
        } else {
            materialesTable.cargarDatosIniciales();
        }
    }
    
    private void cancelar(ActionEvent e) {
        dispose();
    } 
    
    private void alterarLabels(ActionEvent e) {
        String item = (String) origenBox.getSelectedItem();
        switch (item) {
            case "VALE":
                numeroDocumentoLabel.setText("Número de Vale:");
                referenciaLabel.setText("Número de Obra:");
                referenciaLayout.show(referenciaContainer, "VALE");
                cargarObrasDisponibles();
                break;
            case "FACTURA":
                numeroDocumentoLabel.setText("Número de Factura:");
                referenciaLabel.setText("Proveedor:");
                referenciaLayout.show(referenciaContainer, "FACTURA");
                break;
        }
        guardarButton.setEnabled(true);
        showComponents(true);
    }
    
    private boolean validarCampos() {
        return true; // PROCESO PENDIENTE
    }
    
    private void showComponents(boolean visible){
        numeroDocumentoLabel.setVisible(visible);
        numeroDocumentoField.setVisible(visible);
        fechaRegistroLabel.setVisible(visible);
        fechaOrigenPicker.setVisible(visible);
        referenciaLabel.setVisible(visible);
        referenciaContainer.setVisible(visible);
    }
    
    private void cargarObrasDisponibles() {
        try {
            String response = http.executeRequest("/obra/claves");
            List<String> obras = gson.fromJson(response, new TypeToken<List<String>>(){}.getType());
            obraComboBox.removeAllItems();
            for (String obra : obras) {
                obraComboBox.addItem(obra);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las obras disponibles: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}