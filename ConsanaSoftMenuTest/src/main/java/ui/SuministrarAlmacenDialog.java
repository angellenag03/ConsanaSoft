package ui;

import com.google.gson.Gson;
import utils.FechaPicker;
import utils.HTTPManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class SuministrarAlmacenDialog extends JDialog {
    private JLabel tituloLabel;
    private JLabel cantidadLabel;
    private JLabel origenLabel;
    private JLabel numeroDocumentoLabel;
    private JLabel referenciaLabel;
    private JLabel fechaOrigenLabel;

    private JTextField cantidadField;
    private JComboBox<String> origenBox;
    private JTextField numeroDocumentoField;
    private JTextField referenciaField;
    private FechaPicker fechaOrigenPicker;

    private JButton guardarButton;
    private JButton cancelarButton;

    private final Long materialId;
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson = new Gson();

    public SuministrarAlmacenDialog(JFrame parentFrame, Long materialId) {
        super(parentFrame, "Suministrar Material al Almacén", true);
        this.materialId = materialId;
        initComponents();
        setupLayout();
        configurarComportamiento();
        this.setSize(400, 350);
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }

    private void initComponents() {
        tituloLabel = new JLabel("Complete los datos del suministro:");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);

        cantidadLabel = new JLabel("Cantidad:");
        origenLabel = new JLabel("Origen:");
        numeroDocumentoLabel = new JLabel("Número Documento:");
        referenciaLabel = new JLabel("Referencia:");
        fechaOrigenLabel = new JLabel("Fecha de Origen:");

        cantidadField = new JTextField(10);
        origenBox = new JComboBox<>(new String[]{"FACTURA", "VALE"});
        numeroDocumentoField = new JTextField(15);
        referenciaField = new JTextField(20);
        fechaOrigenPicker = new FechaPicker();
        fechaOrigenPicker.setPreferredSize(new Dimension(150, 25));

        Font fieldFont = new Font("Arial", Font.PLAIN, 12);
        cantidadLabel.setFont(fieldFont);
        origenLabel.setFont(fieldFont);
        numeroDocumentoLabel.setFont(fieldFont);
        referenciaLabel.setFont(fieldFont);
        fechaOrigenLabel.setFont(fieldFont);

        cantidadField.setFont(fieldFont);
        origenBox.setFont(fieldFont);
        numeroDocumentoField.setFont(fieldFont);
        referenciaField.setFont(fieldFont);

        guardarButton = new JButton("Guardar");
        cancelarButton = new JButton("Cancelar");

        guardarButton.setPreferredSize(new Dimension(85, 25));
        cancelarButton.setPreferredSize(new Dimension(85, 25));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel();
        topPanel.add(tituloLabel);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Cantidad
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(cantidadLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(cantidadField, gbc);

        // Fila 1: Origen
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        centerPanel.add(origenLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(origenBox, gbc);

        // Fila 2: Número Documento
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        centerPanel.add(numeroDocumentoLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(numeroDocumentoField, gbc);

        // Fila 3: Referencia
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        centerPanel.add(referenciaLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(referenciaField, gbc);

        // Fila 4: Fecha Origen
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        centerPanel.add(fechaOrigenLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel fechaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fechaPanel.add(fechaOrigenPicker);
        centerPanel.add(fechaPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(guardarButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    private void configurarComportamiento() {
        cancelarButton.addActionListener(e -> dispose());
        guardarButton.addActionListener(this::onGuardar);

        getRootPane().setDefaultButton(guardarButton);

        KeyStroke escapeKey = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarButton.doClick();
            }
        });
    }

    private void onGuardar(ActionEvent e) {
        try {
            if (validarCampos()) {
                HashMap<String, Object> suministro = new HashMap<>();
                suministro.put("materialId", materialId);
                suministro.put("cantidad", Double.parseDouble(cantidadField.getText().trim()));
                suministro.put("numeroDocumento", numeroDocumentoField.getText().trim());
                suministro.put("referencia", referenciaField.getText().trim());
                suministro.put("origen", origenBox.getSelectedItem());
                suministro.put("fechaOrigen", fechaOrigenPicker.getFecha());

                http.executeRequest(HTTPManager.HttpMethod.POST, "/almacen/suministrar", suministro);
                
                JOptionPane.showMessageDialog(this,
                    "El material ha sido suministrado con éxito!",
                    "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor completa todos los campos obligatorios.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un número válido.",
                    "Error en cantidad", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al registrar el suministro: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        return !cantidadField.getText().trim().isEmpty()
                && !numeroDocumentoField.getText().trim().isEmpty()
                && !referenciaField.getText().trim().isEmpty()
                && fechaOrigenPicker.getDate() != null;
    }
}
