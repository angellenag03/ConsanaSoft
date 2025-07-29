package ui;

import tables.ObrasNombreFechaTable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dto.ObraDTO;
import utils.HTTPManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class AbrirObraDialog extends JDialog {
    private JLabel tituloLabel;
    private JLabel buscarLabel;
    private ObrasNombreFechaTable obrasTable;
    private final HTTPManager http = HTTPManager.getInstance();
    private Gson gson;
    
    private JPanel infoPanel;
    private JLabel numObraLabel;
    private JLabel nombreObraLabel;
    private JLabel direccionLabel;
    private JLabel coordenadasLabel;
    private JLabel fechaCreacionLabel;
    private JLabel fechaModificacionLabel;
    private JButton abrirButton;
    private JButton cancelarButton;
    private JButton buscarButton;
    private JTextField buscarField;
    private JComboBox<String> tipoBusquedaCombo;
    
    public AbrirObraDialog(JFrame parentFrame) {
        super(parentFrame, "Seleccionar Obra", true);
        this.gson = new Gson();
        initComponents();
        setupLayout();
        configurarComportamiento();
        
        this.setSize(650, 400);
        this.setLocationRelativeTo(parentFrame);
        this.setResizable(false);
    }
    
    private void initComponents() {
        tituloLabel = new JLabel("Selecciona la obra:");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        obrasTable = new ObrasNombreFechaTable();
        
        // Panel de información
        infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información de la Obra"));
        
        numObraLabel = new JLabel("Número de Obra: ");
        nombreObraLabel = new JLabel("Nombre: ");
        direccionLabel = new JLabel("Dirección: ");
        coordenadasLabel = new JLabel("Coordenadas: ");
        fechaCreacionLabel = new JLabel("Fecha de Creación: ");
        fechaModificacionLabel = new JLabel("Ú. Fecha de Modificación: ");
        
        // Componentes de búsqueda
        buscarLabel = new JLabel("Buscar por:");
        buscarField = new JTextField(25);
        buscarButton = new JButton("Buscar");
        tipoBusquedaCombo = new JComboBox<>(new String[]{"Nombre", "Clave"});
        
        infoPanel.add(numObraLabel);
        infoPanel.add(nombreObraLabel);
        infoPanel.add(direccionLabel);
        infoPanel.add(coordenadasLabel);
        infoPanel.add(fechaCreacionLabel);
        infoPanel.add(fechaModificacionLabel);
        
        // Botones
        abrirButton = new JButton("Abrir"); abrirButton.setEnabled(false);
        cancelarButton = new JButton("Cancelar");
        
        abrirButton.setPreferredSize(new Dimension(85, 25));
        cancelarButton.setPreferredSize(new Dimension(85, 25));
        buscarButton.setPreferredSize(new Dimension(85, 25));
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior (búsqueda)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(buscarLabel);
        searchPanel.add(buscarField);
        searchPanel.add(tipoBusquedaCombo);
        searchPanel.add(buscarButton);
        
        // Panel central (tabla e información)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        JScrollPane scrollPane = new JScrollPane(obrasTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelarButton);
        buttonPanel.add(abrirButton);
        
        // Ensamblaje final
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    
    private void configurarComportamiento() {
        // Comportamiento del botón Cancelar
        cancelarButton.addActionListener(e -> dispose());
        
        // Comportamiento de selección de tabla
        obrasTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && obrasTable.getSelectedRow() != -1) {
                upInfoObra();
            }
        });
        
        // Comportamiento del botón Abrir
        abrirButton.addActionListener(this::abrirObraSeleccionada);
        
        // Comportamiento del botón Buscar
        buscarButton.addActionListener(e -> realizarBusqueda());
        
        // Buscar al presionar Enter en el campo de búsqueda
        buscarField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarBusqueda();
                }
            }
        });
        
        // Atajos de teclado
        getRootPane().setDefaultButton(abrirButton);
        
        KeyStroke escapeKey = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarButton.doClick();
            }
        });
    }
    
    private void realizarBusqueda() {
        String textoBusqueda = buscarField.getText().trim();
        if (textoBusqueda.isEmpty()) {
            obrasTable.cargarDatosIniciales();
            return;
        }
        
        int tipoBusqueda = tipoBusquedaCombo.getSelectedIndex();
        if (tipoBusqueda == 0) { // Búsqueda por nombre
            obrasTable.cargarDatosPorNombre(textoBusqueda);
        } else { // Búsqueda por ID
            obrasTable.cargarDatosPorID(textoBusqueda);
        }
    }
    
    private void upInfoObra() {
            abrirButton.setEnabled(true);
        
            numObraLabel.setText("Número de obra: "+getObra().getId());
            nombreObraLabel.setText("Nombre: "+getObra().getNombre());
            direccionLabel.setText("Dirección: "+getObra().getDireccion());
            coordenadasLabel.setText("Coordenadas: "+getObra().getLatitud()+", "+getObra().getLongitud());
            fechaModificacionLabel.setText("Ú. Fecha de Modificación: "+getObra().getFechaModificacion());
            fechaCreacionLabel.setText("Fecha de Creación: "+getObra().getFechaCreacion());
    }
    
    public ObraDTO getObra() {
        try {
            String clave = (String) obrasTable.getValueAt(obrasTable.getSelectedRow(), 0);
            String obraJson = http.executeRequest("/obra/get-clave?id="+clave);
            ObraDTO obra = gson.fromJson(obraJson, ObraDTO.class);

            return obra;
        } catch (JsonSyntaxException | IOException e) { return null; }
    }
    
    private void abrirObraSeleccionada(ActionEvent e) {
        JFrame parentFrame = (JFrame) this.getParent();
        
        dispose();
        
        SwingUtilities.invokeLater(() -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new ObraPanel(parentFrame, getObra()));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
    }
}