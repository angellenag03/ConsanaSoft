package utils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DebugConsole {
    private static JDialog consoleDialog;
    private static JTextArea textArea;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = new Color(129, 230, 147); // #81e693
    private static final Color ERROR_COLOR = new Color(255, 102, 102); // Rojo claro
    private static final Color SYSTEM_OUT_COLOR = new Color(200, 200, 255); // Azul claro para System.out
    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 0, 100); // Amarillo transparente
    private static Font terminalFont;
    
    // Variables para los streams originales
    private static PrintStream originalOut;
    private static PrintStream originalErr;
    private static boolean streamsRedirected = false;
    
    // Variables para búsqueda
    private static JTextField searchField;
    private static boolean caseSensitive = false;
    private static boolean useRegex = false;
    private static int currentSearchIndex = -1;
    private static String lastSearch = "";
    
    // Variables para auto-scroll y filtros
    private static boolean autoScroll = true;
    private static boolean showSystemOut = true;
    private static boolean showSystemErr = true;
    private static boolean showCustomLogs = true;

    static {
        try {
            terminalFont = new Font("Consolas", Font.PLAIN, 14);
        } catch (Exception e) {
            terminalFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        }
    }

    public static void init(JFrame parent) {
        consoleDialog = new JDialog(parent, "Consola de Depuración", false);
        consoleDialog.setSize(900, 600);
        
        // Crear MenuBar
        JMenuBar menuBar = new JMenuBar();
        createMenus(menuBar);
        consoleDialog.setJMenuBar(menuBar);
        
        // Crear área de texto
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(BACKGROUND_COLOR);
        textArea.setForeground(TEXT_COLOR);
        textArea.setFont(terminalFont);
        textArea.setCaretColor(TEXT_COLOR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Panel de búsqueda
        JPanel searchPanel = createSearchPanel();
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(searchPanel, BorderLayout.SOUTH);

        consoleDialog.add(mainPanel);
        consoleDialog.setLocationRelativeTo(parent);
        
        // Configurar teclas globales
        setupKeyBindings();
        
        // Auto-redirigir streams al inicializar
        redirectSystemStreams();
        
        // Iniciar oculta para no interferir con la aplicación principal
        consoleDialog.setVisible(false);
        
        log("=== Consola de Depuración Iniciada ===", TEXT_COLOR);
    }
    
    private static void createMenus(JMenuBar menuBar) {
        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic(KeyEvent.VK_A);
        
        JMenuItem exportItem = new JMenuItem("Exportar Log...");
        exportItem.setMnemonic(KeyEvent.VK_E);
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        exportItem.addActionListener(e -> exportLog());
        
        JMenuItem clearItem = new JMenuItem("Limpiar Consola");
        clearItem.setMnemonic(KeyEvent.VK_L);
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
        clearItem.addActionListener(e -> clearConsole());
        
        JMenuItem exitItem = new JMenuItem("Cerrar Consola");
        exitItem.setMnemonic(KeyEvent.VK_C);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        exitItem.addActionListener(e -> hideConsole());
        
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Menú Terminal
        JMenu terminalMenu = new JMenu("Terminal");
        terminalMenu.setMnemonic(KeyEvent.VK_T);
        
        JCheckBoxMenuItem captureItem = new JCheckBoxMenuItem("Capturar Terminal", streamsRedirected);
        captureItem.setMnemonic(KeyEvent.VK_C);
        captureItem.addActionListener(e -> {
            if (captureItem.isSelected()) {
                redirectSystemStreams();
            } else {
                restoreSystemStreams();
            }
        });
        
        JMenuItem restoreItem = new JMenuItem("Restaurar Terminal Original");
        restoreItem.setMnemonic(KeyEvent.VK_R);
        restoreItem.addActionListener(e -> {
            restoreSystemStreams();
            captureItem.setSelected(false);
        });
        
        terminalMenu.add(captureItem);
        terminalMenu.add(restoreItem);
        
        // Menú Vista
        JMenu viewMenu = new JMenu("Vista");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JCheckBoxMenuItem autoScrollItem = new JCheckBoxMenuItem("Auto-Scroll", autoScroll);
        autoScrollItem.addActionListener(e -> autoScroll = autoScrollItem.isSelected());
        
        JMenu filterMenu = new JMenu("Filtros");
        JCheckBoxMenuItem showOutItem = new JCheckBoxMenuItem("Mostrar System.out", showSystemOut);
        showOutItem.addActionListener(e -> showSystemOut = showOutItem.isSelected());
        
        JCheckBoxMenuItem showErrItem = new JCheckBoxMenuItem("Mostrar System.err", showSystemErr);
        showErrItem.addActionListener(e -> showSystemErr = showErrItem.isSelected());
        
        JCheckBoxMenuItem showCustomItem = new JCheckBoxMenuItem("Mostrar Logs Personalizados", showCustomLogs);
        showCustomItem.addActionListener(e -> showCustomLogs = showCustomItem.isSelected());
        
        filterMenu.add(showOutItem);
        filterMenu.add(showErrItem);
        filterMenu.add(showCustomItem);
        
        JMenu fontMenu = new JMenu("Fuente");
        JMenuItem increaseFontItem = new JMenuItem("Aumentar Tamaño");
        increaseFontItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
        increaseFontItem.addActionListener(e -> changeFontSize(2));
        
        JMenuItem decreaseFontItem = new JMenuItem("Disminuir Tamaño");
        decreaseFontItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
        decreaseFontItem.addActionListener(e -> changeFontSize(-2));
        
        JMenuItem resetFontItem = new JMenuItem("Restablecer Tamaño");
        resetFontItem.addActionListener(e -> resetFontSize());
        
        fontMenu.add(increaseFontItem);
        fontMenu.add(decreaseFontItem);
        fontMenu.addSeparator();
        fontMenu.add(resetFontItem);
        
        viewMenu.add(autoScrollItem);
        viewMenu.addSeparator();
        viewMenu.add(filterMenu);
        viewMenu.addSeparator();
        viewMenu.add(fontMenu);
        
        // Menú Buscar
        JMenu searchMenu = new JMenu("Buscar");
        searchMenu.setMnemonic(KeyEvent.VK_B);
        
        JMenuItem findItem = new JMenuItem("Buscar...");
        findItem.setMnemonic(KeyEvent.VK_B);
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        findItem.addActionListener(e -> focusSearchField());
        
        JMenuItem findNextItem = new JMenuItem("Buscar Siguiente");
        findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        findNextItem.addActionListener(e -> findNext());
        
        JMenuItem findPrevItem = new JMenuItem("Buscar Anterior");
        findPrevItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.SHIFT_DOWN_MASK));
        findPrevItem.addActionListener(e -> findPrevious());
        
        JCheckBoxMenuItem caseSensitiveItem = new JCheckBoxMenuItem("Sensible a Mayúsculas", caseSensitive);
        caseSensitiveItem.addActionListener(e -> {
            caseSensitive = caseSensitiveItem.isSelected();
            if (!searchField.getText().isEmpty()) {
                performSearch(searchField.getText());
            }
        });
        
        JCheckBoxMenuItem regexItem = new JCheckBoxMenuItem("Expresiones Regulares", useRegex);
        regexItem.addActionListener(e -> {
            useRegex = regexItem.isSelected();
            if (!searchField.getText().isEmpty()) {
                performSearch(searchField.getText());
            }
        });
        
        searchMenu.add(findItem);
        searchMenu.addSeparator();
        searchMenu.add(findNextItem);
        searchMenu.add(findPrevItem);
        searchMenu.addSeparator();
        searchMenu.add(caseSensitiveItem);
        searchMenu.add(regexItem);
        
        // Menú Ayuda
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("Acerca de...");
        aboutItem.addActionListener(e -> showAbout());
        
        JMenuItem shortcutsItem = new JMenuItem("Atajos de Teclado");
        shortcutsItem.addActionListener(e -> showKeyboardShortcuts());
        
        helpMenu.add(shortcutsItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        // Agregar menús a la barra
        menuBar.add(fileMenu);
        menuBar.add(terminalMenu);
        menuBar.add(viewMenu);
        menuBar.add(searchMenu);
        menuBar.add(helpMenu);
    }
    
    private static JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        
        searchField = new JTextField();
        searchField.addActionListener(e -> performSearch(searchField.getText()));
        
        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> performSearch(searchField.getText()));
        
        JButton nextButton = new JButton("▼");
        nextButton.setToolTipText("Siguiente (F3)");
        nextButton.addActionListener(e -> findNext());
        
        JButton prevButton = new JButton("▲");
        prevButton.setToolTipText("Anterior (Shift+F3)");
        prevButton.addActionListener(e -> findPrevious());
        
        JButton clearSearchButton = new JButton("✖");
        clearSearchButton.setToolTipText("Limpiar búsqueda");
        clearSearchButton.addActionListener(e -> clearSearch());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        buttonPanel.add(searchButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(clearSearchButton);
        
        searchPanel.add(new JLabel("Buscar: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        
        return searchPanel;
    }
    
    private static void setupKeyBindings() {
        // Solo configurar teclas cuando la consola tiene el foco
        // Esto evita conflictos con teclas del frame principal
        KeyStroke escapeKey = KeyStroke.getKeyStroke("ESCAPE");
        consoleDialog.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeKey, "ESCAPE");
        consoleDialog.getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleDialog.setVisible(false);
            }
        });
        
        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
        consoleDialog.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(ctrlF, "FOCUS_SEARCH");
        consoleDialog.getRootPane().getActionMap().put("FOCUS_SEARCH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusSearchField();
            }
        });
        
        // Agregar F3 para búsqueda
        KeyStroke f3Key = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
        consoleDialog.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(f3Key, "FIND_NEXT");
        consoleDialog.getRootPane().getActionMap().put("FIND_NEXT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findNext();
            }
        });
        
        KeyStroke shiftF3Key = KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.SHIFT_DOWN_MASK);
        consoleDialog.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(shiftF3Key, "FIND_PREV");
        consoleDialog.getRootPane().getActionMap().put("FIND_PREV", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findPrevious();
            }
        });
    }

    // Métodos de redirección de streams (sin cambios)
    public static void redirectSystemStreams() {
        if (streamsRedirected) return;
        
        originalOut = System.out;
        originalErr = System.err;
        
        PrintStream customOut = new PrintStream(new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            
            @Override
            public void write(int b) throws IOException {
                originalOut.write(b);
                
                char c = (char) b;
                if (c == '\n') {
                    logSystemOut(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append(c);
                }
            }
        });
        
        PrintStream customErr = new PrintStream(new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            
            @Override
            public void write(int b) throws IOException {
                originalErr.write(b);
                
                char c = (char) b;
                if (c == '\n') {
                    logSystemErr(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append(c);
                }
            }
        });
        
        System.setOut(customOut);
        System.setErr(customErr);
        streamsRedirected = true;
        
        log("✓ Captura de terminal activada", TEXT_COLOR);
    }
    
    public static void restoreSystemStreams() {
        if (!streamsRedirected) return;
        
        System.setOut(originalOut);
        System.setErr(originalErr);
        streamsRedirected = false;
        
        log("✓ Terminal restaurada", TEXT_COLOR);
    }
    
    private static void logSystemOut(String message) {
        if (!message.trim().isEmpty() && showSystemOut) {
            log("[OUT] " + message, SYSTEM_OUT_COLOR);
        }
    }
    
    private static void logSystemErr(String message) {
        if (!message.trim().isEmpty() && showSystemErr) {
            log("[ERR] " + message, ERROR_COLOR);
        }
    }

    public static void log(String message) {
        if (showCustomLogs) {
            log(message, TEXT_COLOR);
        }
    }

    public static void logError(String message) {
        if (showCustomLogs) {
            log(message, ERROR_COLOR);
        }
    }

    public static void logException(Throwable ex) {
        if (!showCustomLogs) return;
        
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String errorString = sw.toString();
        
        int splitIndex = errorString.indexOf("\n\tat");
        if (splitIndex > 0) {
            logError(errorString.substring(0, splitIndex));
            log(errorString.substring(splitIndex), new Color(200, 200, 200));
        } else {
            logError(errorString);
        }
    }

    private static void log(String message, Color color) {
        if (textArea != null) {
            SwingUtilities.invokeLater(() -> {
                textArea.setForeground(color);
                textArea.append(message + "\n");
                if (autoScroll) {
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
                textArea.setForeground(TEXT_COLOR);
            });
        }
    }
    
    // Métodos de búsqueda
    private static void focusSearchField() {
        searchField.requestFocus();
        searchField.selectAll();
    }
    
    private static void performSearch(String searchText) {
        if (searchText.isEmpty()) {
            clearSearch();
            return;
        }
        
        clearHighlights();
        lastSearch = searchText;
        currentSearchIndex = -1;
        
        String content = textArea.getText();
        String searchPattern = caseSensitive ? searchText : searchText.toLowerCase();
        String searchContent = caseSensitive ? content : content.toLowerCase();
        
        try {
            if (useRegex) {
                Pattern pattern = Pattern.compile(searchPattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
                highlightRegexMatches(content, pattern);
            } else {
                highlightTextMatches(searchContent, searchPattern);
            }
            
            findNext();
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(consoleDialog, "Expresión regular inválida: " + e.getMessage(),
                    "Error en Regex", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void highlightTextMatches(String content, String searchPattern) {
        Highlighter highlighter = textArea.getHighlighter();
        DefaultHighlighter.DefaultHighlightPainter painter = 
            new DefaultHighlighter.DefaultHighlightPainter(HIGHLIGHT_COLOR);
        
        int index = 0;
        while ((index = content.indexOf(searchPattern, index)) >= 0) {
            try {
                highlighter.addHighlight(index, index + searchPattern.length(), painter);
            } catch (BadLocationException e) {
                // Ignorar
            }
            index += searchPattern.length();
        }
    }
    
    private static void highlightRegexMatches(String content, Pattern pattern) {
        Highlighter highlighter = textArea.getHighlighter();
        DefaultHighlighter.DefaultHighlightPainter painter = 
            new DefaultHighlighter.DefaultHighlightPainter(HIGHLIGHT_COLOR);
        
        java.util.regex.Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            try {
                highlighter.addHighlight(matcher.start(), matcher.end(), painter);
            } catch (BadLocationException e) {
                // Ignorar
            }
        }
    }
    
    private static void findNext() {
        if (lastSearch.isEmpty()) return;
        
        String content = textArea.getText();
        String searchContent = caseSensitive ? content : content.toLowerCase();
        String searchPattern = caseSensitive ? lastSearch : lastSearch.toLowerCase();
        
        int startPos = currentSearchIndex + 1;
        int foundIndex = searchContent.indexOf(searchPattern, startPos);
        
        if (foundIndex == -1) {
            foundIndex = searchContent.indexOf(searchPattern, 0);
        }
        
        if (foundIndex != -1) {
            currentSearchIndex = foundIndex;
            textArea.setCaretPosition(foundIndex);
            textArea.select(foundIndex, foundIndex + searchPattern.length());
        }
    }
    
    private static void findPrevious() {
        if (lastSearch.isEmpty()) return;
        
        String content = textArea.getText();
        String searchContent = caseSensitive ? content : content.toLowerCase();
        String searchPattern = caseSensitive ? lastSearch : lastSearch.toLowerCase();
        
        int endPos = currentSearchIndex - 1;
        if (endPos < 0) endPos = searchContent.length();
        
        int foundIndex = searchContent.lastIndexOf(searchPattern, endPos);
        
        if (foundIndex != -1) {
            currentSearchIndex = foundIndex;
            textArea.setCaretPosition(foundIndex);
            textArea.select(foundIndex, foundIndex + searchPattern.length());
        }
    }
    
    private static void clearSearch() {
        searchField.setText("");
        clearHighlights();
        lastSearch = "";
        currentSearchIndex = -1;
    }
    
    private static void clearHighlights() {
        textArea.getHighlighter().removeAllHighlights();
    }
    
    // Métodos de fuente
    private static void changeFontSize(int delta) {
        Font currentFont = textArea.getFont();
        int newSize = Math.max(8, Math.min(72, currentFont.getSize() + delta));
        Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), newSize);
        textArea.setFont(newFont);
    }
    
    private static void resetFontSize() {
        textArea.setFont(terminalFont);
    }
    
    // Métodos de exportación
    private static void exportLog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("debug_log.txt"));
        
        if (fileChooser.showSaveDialog(consoleDialog) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.write(textArea.getText());
                JOptionPane.showMessageDialog(consoleDialog, "Log exportado exitosamente.",
                        "Exportación Completa", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(consoleDialog, "Error al exportar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Métodos de ayuda
    private static void showAbout() {
        String message = """
            Consola de Depuración v2.0
            
            Una consola avanzada para capturar y visualizar
            la salida del sistema y logs personalizados.
            
            Características:
            • Captura de System.out y System.err
            • Búsqueda con expresiones regulares
            • Filtros de contenido
            • Exportación de logs
            • Atajos de teclado
            """;
        
        JOptionPane.showMessageDialog(consoleDialog, message, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static void showKeyboardShortcuts() {
        String shortcuts = """
            Atajos de Teclado:
            
            Ctrl+F          - Buscar
            F3              - Buscar siguiente
            Shift+F3        - Buscar anterior
            Ctrl+L          - Limpiar consola
            Ctrl+E          - Exportar log
            Ctrl++          - Aumentar fuente
            Ctrl+-          - Disminuir fuente
            Escape          - Cerrar consola
            """;
        
        JOptionPane.showMessageDialog(consoleDialog, shortcuts, "Atajos de Teclado", JOptionPane.INFORMATION_MESSAGE);
    }

    // Métodos públicos (sin cambios en la interfaz)
    public static void toggleVisibility() {
        if (consoleDialog != null) {
            consoleDialog.setVisible(!consoleDialog.isVisible());
        }
    }

    public static boolean isVisible() {
        return consoleDialog != null && consoleDialog.isVisible();
    }

    public static void showConsole() {
        if (consoleDialog != null) {
            consoleDialog.setVisible(true);
            consoleDialog.toFront();
            consoleDialog.requestFocus();
        }
    }

    public static void hideConsole() {
        if (consoleDialog != null) {
            consoleDialog.setVisible(false);
        }
    }
    
    public static void clearConsole() {
        if (textArea != null) {
            textArea.setText("");
            log("=== Consola Limpiada ===", TEXT_COLOR);
        }
    }
    
    public static void cleanup() {
        restoreSystemStreams();
    }
}