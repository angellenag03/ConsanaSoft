package ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import utils.FechaPicker;
import utils.HTTPManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NuevoMaterialDialog extends JDialog {
    private JLabel tituloLabel;
    private JLabel nombreLabel;
    private JLabel unidadLabel;
    private JLabel cantidadLabel;
    private JLabel origenLabel;
    private JLabel numeroDocumentoLabel;
    private JLabel fechaRegistroLabel;
    private JLabel referenciaLabel;

    private JTextField nombreField;
    private JTextField unidadField;
    private JTextField cantidadField;
    private JComboBox<String> origenBox;
    private JTextField numeroDocumentoField;
    private FechaPicker fechaOrigenPicker;
    private JTextField referenciaField;
    private JComboBox<String> obraComboBox;

    private JPanel referenciaContainer;
    private CardLayout referenciaLayout;

    private JButton guardarButton;
    private JButton cancelarButton;
    private JButton seleccionarExistenteButton;

    private JFrame parentFrame;
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson = new Gson();

    public NuevoMaterialDialog(JFrame parentFrame) {
        super(parentFrame, "Nuevo Material", true);
        initComponents();
        setupLayout();
        setupBehavior();
        this.setSize(450, 500);
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }

    private void initComponents() {
        tituloLabel = new JLabel("Complete los datos del nuevo material:");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);

        nombreLabel = new JLabel("Nombre:");
        unidadLabel = new JLabel("Unidad:");
        cantidadLabel = new JLabel("Cantidad:");
        origenLabel = new JLabel("Origen:");
        numeroDocumentoLabel = new JLabel("Número Documento:");
        fechaRegistroLabel = new JLabel("Fecha de Registro:");
        referenciaLabel = new JLabel("Referencia:");

        nombreField = new JTextField(15);
        unidadField = new JTextField(10);
        cantidadField = new JTextField(8);
        origenBox = new JComboBox<>(new String[]{"FACTURA", "VALE"});
        numeroDocumentoField = new JTextField(15);
        fechaOrigenPicker = new FechaPicker();
        referenciaField = new JTextField(20);
        obraComboBox = new JComboBox<>();

        Font fieldFont = new Font("Arial", Font.PLAIN, 12);
        nombreLabel.setFont(fieldFont);
        unidadLabel.setFont(fieldFont);
        cantidadLabel.setFont(fieldFont);
        origenLabel.setFont(fieldFont);
        numeroDocumentoLabel.setFont(fieldFont);
        fechaRegistroLabel.setFont(fieldFont);
        referenciaLabel.setFont(fieldFont);

        nombreField.setFont(fieldFont);
        unidadField.setFont(fieldFont);
        cantidadField.setFont(fieldFont);
        origenBox.setFont(fieldFont);
        numeroDocumentoField.setFont(fieldFont);
        referenciaField.setFont(fieldFont);
        obraComboBox.setFont(fieldFont);

        referenciaLayout = new CardLayout();
        referenciaContainer = new JPanel(referenciaLayout);
        referenciaContainer.add(referenciaField, "FACTURA");
        referenciaContainer.add(obraComboBox, "VALE");

        guardarButton = new JButton("Guardar");
        cancelarButton = new JButton("Cancelar");
        seleccionarExistenteButton = new JButton("Seleccionar existente");

        guardarButton.setEnabled(false);
        guardarButton.setPreferredSize(new Dimension(85, 25));
        cancelarButton.setPreferredSize(new Dimension(85, 25));
        
        showComponents(false);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel();
        topPanel.add(tituloLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel datosPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        datosPanel.add(nombreLabel);
        datosPanel.add(nombreField);
        datosPanel.add(unidadLabel);
        datosPanel.add(unidadField);
        datosPanel.add(cantidadLabel);
        datosPanel.add(cantidadField);
        datosPanel.add(origenLabel);
        datosPanel.add(origenBox);
        datosPanel.add(numeroDocumentoLabel);
        datosPanel.add(numeroDocumentoField);
        datosPanel.add(fechaRegistroLabel);
        datosPanel.add(fechaOrigenPicker);
        datosPanel.add(referenciaLabel);
        datosPanel.add(referenciaContainer);

        centerPanel.add(datosPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(seleccionarExistenteButton);
        buttonPanel.add(cancelarButton);
        buttonPanel.add(guardarButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    private void setupBehavior() {
        cancelarButton.addActionListener(e -> dispose());
        guardarButton.addActionListener(this::onGuardar);
        origenBox.addActionListener(this::alterarLabels);

        getRootPane().setDefaultButton(guardarButton);

        KeyStroke escapeKey = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarButton.doClick();
            }
        });

        nombreField.addActionListener(this::buscarExistente);
        
        seleccionarExistenteButton.addActionListener(e -> {
            SeleccionarMaterialDialog dialog = new SeleccionarMaterialDialog((JFrame) getParent());
            dialog.setVisible(true);

            if (dialog.getNombreSeleccionado() != null) {
                nombreField.setText(dialog.getNombreSeleccionado());
                unidadField.setText(dialog.getUnidadSeleccionada());
            }
        });
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

    private void onGuardar(ActionEvent e) {
        try {
            if (validarCampos()) {
                
                String origen = (String) origenBox.getSelectedItem();
                
                HashMap<String, Object> material = new HashMap<>();
                material.put("nombre", nombreField.getText().trim());
                material.put("unidad", unidadField.getText().trim());
                material.put("cantidad", cantidadField.getText().trim());
                material.put("origen", "VALE".equals(origen) ? "VALE_DE_ENTRADA" : origen);
                material.put("numeroDocumento", numeroDocumentoField.getText().trim());
                material.put("fechaOrigen", fechaOrigenPicker.getFecha());
                material.put("referencia", "VALE".equals(origen) ? 
                    obraComboBox.getSelectedItem() : referenciaField.getText().trim());

                http.executeRequest(HTTPManager.HttpMethod.POST, "/material", material);
                
                JOptionPane.showMessageDialog(this,
                        "Material creado/suministrado con éxito!",
                        "Atención", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor completa los campos obligatorios.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el material: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        return !nombreField.getText().trim().isEmpty()
            && !unidadField.getText().trim().isEmpty()
            && !cantidadField.getText().trim().isEmpty();
    }

    private void showComponents(boolean visible){
        numeroDocumentoLabel.setVisible(visible);
        numeroDocumentoField.setVisible(visible);
        fechaRegistroLabel.setVisible(visible);
        fechaOrigenPicker.setVisible(visible);
        referenciaLabel.setVisible(visible);
        referenciaContainer.setVisible(visible);
    }
    
    private void buscarExistente(ActionEvent e) {
        SeleccionarMaterialDialog dialog = new SeleccionarMaterialDialog(parentFrame, nombreField.getText());
        dialog.setVisible(true);
        
        if (dialog.getNombreSeleccionado() != null) {
                nombreField.setText(dialog.getNombreSeleccionado());
                unidadField.setText(dialog.getUnidadSeleccionada());
        }
    }
}