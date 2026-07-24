package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import utils.HTTPManager;

public class NuevoMaterialDialog extends JDialog {
    private JLabel tituloLabel;
    
    private JLabel claveLabel;
    private JLabel nombreLabel;
    private JLabel unidadLabel;

    private JTextField claveField;
    private JTextField nombreField;
    private JTextField unidadField;

    private JButton guardarButton;
    private JButton cancelarButton;
    
    private JFrame parentFrame;
    private final HTTPManager http = HTTPManager.getInstance();
    private boolean guardado = false;

    public NuevoMaterialDialog(JFrame parentFrame) {
        super(parentFrame, "Nuevo Material", true);
        this.parentFrame = parentFrame;
        initComponents();
        setupLayout();
        setupBehavior();
        this.pack(); // Ajusta automáticamente el tamaño al contenido
        this.setMinimumSize(new Dimension(380, 220));
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }

    private void initComponents() {
        tituloLabel = new JLabel("Complete los datos del material:");
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);

        claveLabel = new JLabel("Clave:");
        nombreLabel = new JLabel("Nombre:");
        unidadLabel = new JLabel("Unidad:");

        claveField = new JTextField(15);
        nombreField = new JTextField(15);
        unidadField = new JTextField(10);

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

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JPanel datosPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        datosPanel.add(claveLabel);
        datosPanel.add(claveField);
        datosPanel.add(nombreLabel);
        datosPanel.add(nombreField);
        datosPanel.add(unidadLabel);
        datosPanel.add(unidadField);

        centerPanel.add(datosPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
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

    }

    private void onGuardar(ActionEvent e) {
        try {
            if (validarCampos()) {
                HashMap<String, Object> material = new HashMap<>();
                material.put("clave", claveField.getText().trim());
                material.put("nombre", nombreField.getText().trim());
                material.put("unidad", unidadField.getText().trim());

                http.executeRequest(HTTPManager.HttpMethod.POST, "/material", material);

                JOptionPane.showMessageDialog(this,
                        "¡Material creado con éxito!",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

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
            && !unidadField.getText().trim().isEmpty();
    }

    private void buscarExistente(ActionEvent e) {
        SeleccionarMaterialDialog dialog = new SeleccionarMaterialDialog(parentFrame, nombreField.getText());
        dialog.setVisible(true);

        if (dialog.getNombreSeleccionado() != null) {
            nombreField.setText(dialog.getNombreSeleccionado());
            unidadField.setText(dialog.getUnidadSeleccionada());
        }
    }

    // --- Getters para recuperar la información cargada ---

    public boolean isGuardado() {
        return guardado;
    }

    public String getClave() {
        return claveField.getText().trim();
    }

    public String getNombre() {
        return nombreField.getText().trim();
    }

    public String getUnidad() {
        return unidadField.getText().trim();
    }
}