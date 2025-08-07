package utils;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelGenerator {
    private SXSSFWorkbook workbook;
    private Sheet sheet;
    private int currentRow;
    private Map<String, CellStyle> styles;
    private Component parentComponent;

    private static ExcelGenerator instance;
 
    private ExcelGenerator() {
        this.workbook = new SXSSFWorkbook();
        this.sheet = workbook.createSheet("Reporte");
        this.currentRow = 0;
        this.styles = createStyles();
        this.parentComponent = null;
    }
    
    public static synchronized ExcelGenerator getInstance() {
        if (instance == null) {
            instance = new ExcelGenerator();
        }
        return instance;
    }
    
    /**
     * Establece el componente padre para mostrar diálogos de error
     */
    public void setParentComponent(Component parent) {
        this.parentComponent = parent;
    }

    private Map<String, CellStyle> createStyles() {
        Map<String, CellStyle> styles = new HashMap<>();
        
        try {
            // Estilo para título
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short)14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            styles.put("title", titleStyle);
            
            // Estilo para subtítulo
            CellStyle subtitleStyle = workbook.createCellStyle();
            Font subtitleFont = workbook.createFont();
            subtitleFont.setBold(true);
            subtitleStyle.setFont(subtitleFont);
            subtitleStyle.setAlignment(HorizontalAlignment.CENTER);
            styles.put("subtitle", subtitleStyle);
            
            // Estilo para fecha
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setAlignment(HorizontalAlignment.CENTER);
            styles.put("date", dateStyle);
            
            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            styles.put("header", headerStyle);
            
            // Estilo para celdas normales centradas
            CellStyle centeredStyle = workbook.createCellStyle();
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);
            centeredStyle.setBorderTop(BorderStyle.THIN);
            centeredStyle.setBorderBottom(BorderStyle.THIN);
            centeredStyle.setBorderLeft(BorderStyle.THIN);
            centeredStyle.setBorderRight(BorderStyle.THIN);
            styles.put("centered", centeredStyle);
            
            // Estilo para celdas alineadas a la izquierda
            CellStyle leftAlignedStyle = workbook.createCellStyle();
            leftAlignedStyle.setAlignment(HorizontalAlignment.LEFT);
            leftAlignedStyle.setBorderTop(BorderStyle.THIN);
            leftAlignedStyle.setBorderBottom(BorderStyle.THIN);
            leftAlignedStyle.setBorderLeft(BorderStyle.THIN);
            leftAlignedStyle.setBorderRight(BorderStyle.THIN);
            styles.put("leftAligned", leftAlignedStyle);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error al crear estilos de celda: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        return styles;
    }

    public void addTitle(String title, int colSpan) {
        try {
            if (title == null || title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "El título no puede estar vacío", 
                    "Error de Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Row row = sheet.createRow(currentRow++);
            Cell cell = row.createCell(0);
            cell.setCellValue(title);
            cell.setCellStyle(styles.get("title"));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, colSpan-1));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error al añadir título: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addSubtitle(String subtitle, int colSpan) {
        try {
            if (subtitle == null || subtitle.trim().isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "El subtítulo no puede estar vacío", 
                    "Error de Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Row row = sheet.createRow(currentRow++);
            Cell cell = row.createCell(0);
            cell.setCellValue(subtitle);
            cell.setCellStyle(styles.get("subtitle"));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, colSpan-1));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error al añadir subtítulo: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addDate() {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy");
            String fecha = "Fecha: " + now.format(formatter);
            
            Row row = sheet.createRow(currentRow++);
            Cell cell = row.createCell(0);
            cell.setCellValue(fecha);
            cell.setCellStyle(styles.get("date"));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 5));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error al añadir fecha: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addEmptyRow() {
        currentRow++;
    }

    public void addJTable(JTable table, String title, int[] leftAlignedColumns) {
        addJTable(table, title, leftAlignedColumns, true);
    }
    
    private void addJTable(JTable table, String title, int[] leftAlignedColumns, boolean useTableColumnWidths) {
        try {
            if (table == null) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "La tabla no puede ser nula", 
                    "Error de Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (table.getModel().getRowCount() == 0) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "La tabla está vacía - no hay datos para exportar", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Añadir título si se especificó
            if (title != null && !title.isEmpty()) {
                Row titleRow = sheet.createRow(currentRow++);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(title);
                titleCell.setCellStyle(styles.get("subtitle"));
                sheet.addMergedRegion(new CellRangeAddress(
                    titleRow.getRowNum(), 
                    titleRow.getRowNum(), 
                    0, 
                    table.getColumnCount() - 1
                ));
            }
            
            TableModel model = table.getModel();
            
            // Añadir encabezados y configurar anchos
            Row headerRow = sheet.createRow(currentRow++);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                String columnName = model.getColumnName(i);
                cell.setCellValue(columnName != null ? columnName : "Col " + (i + 1));
                cell.setCellStyle(styles.get("header"));
                
                if (useTableColumnWidths) {
                    try {
                        int tableColumnWidth = table.getColumnModel().getColumn(i).getWidth();
                        int excelWidth = Math.max(1000, tableColumnWidth * 37);
                        sheet.setColumnWidth(i, Math.min(excelWidth, 15000));
                    } catch (Exception e) {
                        sheet.setColumnWidth(i, 4000);
                    }
                } else {
                    sheet.setColumnWidth(i, 4000);
                }
            }
            
            // Añadir datos
            for (int row = 0; row < model.getRowCount(); row++) {
                Row excelRow = sheet.createRow(currentRow++);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = excelRow.createCell(col);
                    Object value = model.getValueAt(row, col);
                    
                    try {
                        if (value == null) {
                            cell.setCellValue("");
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } catch (Exception cellException) {
                        cell.setCellValue("");
                        System.err.println("Error en celda [" + row + "," + col + "]: " + cellException.getMessage());
                    }
                    
                    // Determinar alineación
                    boolean leftAlign = false;
                    if (leftAlignedColumns != null) {
                        for (int leftCol : leftAlignedColumns) {
                            if (col == leftCol) {
                                leftAlign = true;
                                break;
                            }
                        }
                    }
                    
                    cell.setCellStyle(leftAlign ? styles.get("leftAligned") : styles.get("centered"));
                }
            }
            
            currentRow++;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error al procesar tabla: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean saveToFile(String defaultFileName) {
        try {
            if (defaultFileName == null || defaultFileName.trim().isEmpty()) {
                defaultFileName = "Reporte_" + System.currentTimeMillis();
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Excel");
            File defaultFile = new File(defaultFileName + ".xlsx");
            fileChooser.setSelectedFile(defaultFile);

            int userSelection = fileChooser.showSaveDialog(parentComponent);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
                }
                
                // Verificar si el archivo ya existe
                if (fileToSave.exists()) {
                    int option = JOptionPane.showConfirmDialog(
                        parentComponent,
                        "El archivo ya existe. ¿Desea sobrescribirlo?",
                        "Archivo Existente",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (option != JOptionPane.YES_OPTION) {
                        return false;
                    }
                }
                
                // Verificar permisos de escritura
                File parentDir = fileToSave.getParentFile();
                if (parentDir != null && !parentDir.canWrite()) {
                    JOptionPane.showMessageDialog(parentComponent, 
                        "No tiene permisos para escribir en la carpeta seleccionada", 
                        "Error de Permisos", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                    workbook.write(fileOut);
                    fileOut.flush();
                    
                    // Mostrar mensaje de éxito
                    JOptionPane.showMessageDialog(parentComponent, 
                        "Archivo guardado exitosamente en:\n" + fileToSave.getAbsolutePath(), 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Abrir el archivo automáticamente
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().open(fileToSave);
                        } catch (Exception desktopEx) {
                            System.err.println("No se pudo abrir el archivo automáticamente: " + desktopEx.getMessage());
                        }
                    }
                    return true;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(parentComponent, 
                        "Error al escribir el archivo: " + e.getMessage(), 
                        "Error de E/S", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                } finally {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        System.err.println("Error al cerrar workbook: " + e.getMessage());
                    }
                    workbook.dispose();
                }
            }
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error inesperado al guardar archivo: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void exportJTablesToExcel(String reportTitle, int[] leftAlignedColumns, JTable... tables) {
        exportJTablesToExcel(reportTitle, leftAlignedColumns, true, tables);
    }
    
    public void exportJTablesToExcel(String reportTitle, int[] leftAlignedColumns, boolean useTableColumnWidths, JTable... tables) {
        try {
            if (tables == null || tables.length == 0) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "No se proporcionaron tablas para exportar", 
                    "Error de Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (reportTitle == null || reportTitle.trim().isEmpty()) {
                reportTitle = "Reporte_Excel";
            }
            
            boolean hasData = false;
            for (JTable table : tables) {
                if (table != null && table.getModel().getRowCount() > 0) {
                    hasData = true;
                    break;
                }
            }
            
            if (!hasData) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "Todas las tablas están vacías - no hay datos para exportar", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Reiniciar el workbook
            this.workbook = new SXSSFWorkbook();
            this.sheet = workbook.createSheet("Reporte");
            this.currentRow = 0;
            this.styles = createStyles();
            
            LocalDateTime now = LocalDateTime.now();
            String fileName = String.format("%s-%02d-%02d-%04d", 
                reportTitle.replaceAll("[^a-zA-Z0-9]", "_"), 
                now.getDayOfMonth(), 
                now.getMonthValue(), 
                now.getYear());
            
            int maxColumns = 0;
            for (JTable table : tables) {
                if (table != null && table.getColumnCount() > maxColumns) {
                    maxColumns = table.getColumnCount();
                }
            }
            
            if (maxColumns == 0) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "No se encontraron columnas válidas en las tablas", 
                    "Error de Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            addTitle(reportTitle, maxColumns);
            addDate();
            addEmptyRow();
            
            for (JTable table : tables) {
                if (table != null) {
                    addJTable(table, null, leftAlignedColumns, useTableColumnWidths);
                    addEmptyRow();
                }
            }
            
            saveToFile(fileName);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error durante la exportación: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void exportJTablesToExcelWithCustomWidths(String reportTitle, int[] leftAlignedColumns, List<int[]> customPixelWidths, JTable... tables) {
        try {
            if (tables == null || tables.length == 0) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "No se proporcionaron tablas para exportar", 
                    "Error de Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            this.workbook = new SXSSFWorkbook();
            this.sheet = workbook.createSheet("Reporte");
            this.currentRow = 0;
            this.styles = createStyles();

            LocalDateTime now = LocalDateTime.now();
            String fileName = String.format("%s-%02d-%02d-%04d",
                reportTitle != null ? reportTitle.replaceAll("[^a-zA-Z0-9]", "_") : "Reporte",
                now.getDayOfMonth(),
                now.getMonthValue(),
                now.getYear());

            int maxColumns = 0;
            for (JTable table : tables) {
                if (table != null && table.getColumnCount() > maxColumns) {
                    maxColumns = table.getColumnCount();
                }
            }

            addTitle(reportTitle != null ? reportTitle : "Reporte", maxColumns);
            addDate();
            addEmptyRow();

            for (int i = 0; i < tables.length; i++) {
                JTable table = tables[i];
                if (table != null) {
                    int[] pixelWidths = (customPixelWidths != null && i < customPixelWidths.size()) ? 
                                       customPixelWidths.get(i) : null;

                    int[] excelWidths = null;
                    if (pixelWidths != null) {
                        excelWidths = new int[pixelWidths.length];
                        for (int j = 0; j < pixelWidths.length; j++) {
                            int px = pixelWidths[j];
                            excelWidths[j] = Math.min(Math.max(px * 37, 1000), 15000);
                        }
                    }

                    addJTableWithCustomWidths(table, null, leftAlignedColumns, excelWidths);
                    addEmptyRow();
                }
            }

            saveToFile(fileName);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error durante la exportación con anchos personalizados: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addJTableWithCustomWidths(JTable table, String title, int[] leftAlignedColumns, int[] customWidths) {
        try {
            if (title != null && !title.isEmpty()) {
                Row titleRow = sheet.createRow(currentRow++);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(title);
                titleCell.setCellStyle(styles.get("subtitle"));
                sheet.addMergedRegion(new CellRangeAddress(
                    titleRow.getRowNum(),
                    titleRow.getRowNum(),
                    0,
                    table.getColumnCount() - 1
                ));
            }

            TableModel model = table.getModel();

            Row headerRow = sheet.createRow(currentRow++);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
                cell.setCellStyle(styles.get("header"));

                if (customWidths != null && i < customWidths.length) {
                    sheet.setColumnWidth(i, customWidths[i]);
                } else {
                    sheet.setColumnWidth(i, 4000);
                }
            }

            for (int row = 0; row < model.getRowCount(); row++) {
                Row excelRow = sheet.createRow(currentRow++);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = excelRow.createCell(col);
                    Object value = model.getValueAt(row, col);

                    try {
                        if (value == null) {
                            cell.setCellValue("");
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } catch (Exception cellException) {
                        cell.setCellValue("");
                    }

                    boolean leftAlign = false;
                    if (leftAlignedColumns != null) {
                        for (int leftCol : leftAlignedColumns) {
                            if (col == leftCol) {
                                leftAlign = true;
                                break;
                            }
                        }
                    }

                    cell.setCellStyle(leftAlign ? styles.get("leftAligned") : styles.get("centered"));
                }
            }

            currentRow++;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Error al procesar tabla con anchos personalizados: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void exportJTablesToExcel(String reportTitle, JTable... tables) {
        exportJTablesToExcel(reportTitle, new int[]{0}, true, tables);
    }
}