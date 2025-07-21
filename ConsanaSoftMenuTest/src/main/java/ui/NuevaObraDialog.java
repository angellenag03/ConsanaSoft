package ui;

import com.google.gson.Gson;
import dto.ObraDTO;
import utils.HTTPManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

public class NuevaObraDialog extends JDialog {
    private JLabel tituloLabel;
    private JTextField numObraField;
    private JTextField nombreField;
    private JTextField direccionField;
    private JTextField latitudField;
    private JTextField longitudField;
    private JButton crearButton;
    private JButton cancelarButton;
    
    private final HTTPManager http = HTTPManager.getInstance();
    private final Gson gson = new Gson();
    
    
    public NuevaObraDialog(JFrame parentFrame) {
        super(parentFrame, "Nueva Obra", true);
        initComponents();
        setupLayout();
        configurarComportamiento();
        
        this.setSize(400, 350);
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }
    
    private void initComponents() {
        // Configuración del título
        tituloLabel = new JLabel("Complete los datos de la nueva obra:");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Campos de texto para la obra
        numObraField = new JTextField(15);
        nombreField = new JTextField(15);
        direccionField = new JTextField(15);
        latitudField = new JTextField(10);
        longitudField = new JTextField(10);
        
        // Establecer fuentes consistentes
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);
        numObraField.setFont(fieldFont);
        nombreField.setFont(fieldFont);
        direccionField.setFont(fieldFont);
        latitudField.setFont(fieldFont);
        longitudField.setFont(fieldFont);
        
        // Botones con estilo consistente
        crearButton = new JButton("Crear");
        cancelarButton = new JButton("Cancelar");
        
        crearButton.setPreferredSize(new Dimension(80, 25));
        cancelarButton.setPreferredSize(new Dimension(80, 25));
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior (título)
        JPanel topPanel = new JPanel();
        topPanel.add(tituloLabel);
        
        // Panel central (campos de entrada)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Panel para datos generales
        JPanel datosPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        datosPanel.add(new JLabel("Número de Obra:"));
        datosPanel.add(numObraField);
        datosPanel.add(new JLabel("Nombre:"));
        datosPanel.add(nombreField);
        datosPanel.add(new JLabel("Dirección:"));
        datosPanel.add(direccionField);
        
        // Panel específico para coordenadas
        JPanel coordenadasPanel = new JPanel();
        coordenadasPanel.setBorder(BorderFactory.createTitledBorder("Coordenadas Geográficas"));
        coordenadasPanel.setLayout(new GridLayout(2, 2, 10, 5));
        
        coordenadasPanel.add(new JLabel("Latitud:"));
        coordenadasPanel.add(latitudField);
        coordenadasPanel.add(new JLabel("Longitud:"));
        coordenadasPanel.add(longitudField);
        
        // Añadir paneles al centro
        centerPanel.add(datosPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(coordenadasPanel);
        
        // Panel de botones (derecha, estilo consistente)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(crearButton);
        
        // Ensamblaje final
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    private void configurarComportamiento() {
        // Comportamiento del botón Cancelar
        cancelarButton.addActionListener(e -> dispose());
        
        // Comportamiento del botón Crear
        crearButton.addActionListener(this::crearObra);
        
        // Atajos de teclado
        getRootPane().setDefaultButton(crearButton);
        
        // Atajo ESC para cancelar
        KeyStroke escapeKey = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarButton.doClick();
            }
        });
    }
    
    // El método chido
    private void crearObra(ActionEvent e) {
        if(isValidFormat()) {
            try {
                HashMap<String, Object> nuevaObra = new HashMap<>();
                
                nuevaObra.put("id", getNumObra());
                nuevaObra.put("nombre", getNombre());
                nuevaObra.put("direccion", getDireccion());
                nuevaObra.put("latitud", getLatitud());
                nuevaObra.put("longitud", getLongitud());
                
                String response = http.executeRequest(
                        HTTPManager.HttpMethod.POST, 
                        "/obra", 
                        nuevaObra
                );
                
                ObraDTO obra = gson.fromJson(response, ObraDTO.class);
                
                JOptionPane.showMessageDialog(this, 
                        "Obra "+getNumObra()+" creado con éxito!", 
                        "Atención", JOptionPane.INFORMATION_MESSAGE);
                
                JFrame parentFrame = (JFrame) this.getParent();
                
                dispose();
                
                SwingUtilities.invokeLater(() -> {
                    parentFrame.getContentPane().removeAll();
                    parentFrame.add(new ObraPanel(parentFrame, obra));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                    parentFrame.setTitle("ConsanaSoft: "+obra.getNombre());
                });
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                        "Favor de llenar todos los campos correctamente", 
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
       
    public String getNumObra() { 
        return numObraField == null ? "" : numObraField.getText().trim(); 
    }
    
    public String getNombre() { 
        return nombreField == null ? "" : nombreField.getText().trim(); 
    }
    
    public String getDireccion() { 
        return direccionField == null ? "" : direccionField.getText().trim(); 
    }
    
    public Double getLatitud() {
        try {
            return Double.parseDouble(latitudField.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public Double getLongitud() {
        try {
            return Double.valueOf(longitudField.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private boolean isValidFormat() {
        // Validar que todos los campos de texto tengan contenido
        if (getNumObra().isEmpty() || getNombre().isEmpty() || getDireccion().isEmpty()) {
            
            return false;
        }
        
        // Validar que las coordenadas sean números decimales válidos
        try {
            Double.valueOf(latitudField.getText().trim());
            Double.valueOf(longitudField.getText().trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void addCrearObraListener(ActionListener listener) {
        crearButton.addActionListener(listener);
    }
    
    public void mostrarDialogo() { 
        numObraField.setText(""); 
        nombreField.setText("");
        direccionField.setText("");
        latitudField.setText("");
        longitudField.setText("");
        setVisible(true); 
    }
    
    public void cerrarDialogo() { 
        dispose(); 
    }
}