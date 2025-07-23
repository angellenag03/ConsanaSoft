package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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

    private static ExcelGenerator instance;
 
    private ExcelGenerator() {
        this.workbook = new SXSSFWorkbook();
        this.sheet = workbook.createSheet("Reporte");
        this.currentRow = 0;
        this.styles = createStyles();
    }
    
    public static synchronized ExcelGenerator getInstance() {
        if (instance == null) {
            instance = new ExcelGenerator();
        }
        return instance;
    }

    private Map<String, CellStyle> createStyles() {
        Map<String, CellStyle> styles = new HashMap<>();
        
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
        
        return styles;
    }

    public void addTitle(String title, int colSpan) {
        Row row = sheet.createRow(currentRow++);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, colSpan-1));
    }

    public void addSubtitle(String subtitle, int colSpan) {
        Row row = sheet.createRow(currentRow++);
        Cell cell = row.createCell(0);
        cell.setCellValue(subtitle);
        cell.setCellStyle(styles.get("subtitle"));
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, colSpan-1));
    }

    public void addDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy");
        String fecha = "Fecha: " + now.format(formatter);
        
        Row row = sheet.createRow(currentRow++);
        Cell cell = row.createCell(0);
        cell.setCellValue(fecha);
        cell.setCellStyle(styles.get("date"));
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 5));
    }

    public void addEmptyRow() {
        currentRow++;
    }

    public void addJTable(JTable table, String title, int[] leftAlignedColumns) {
        addJTable(table, title, leftAlignedColumns, true); // Por defecto usar anchos del JTable
    }
    
    // Método interno con control completo
    private void addJTable(JTable table, String title, int[] leftAlignedColumns, boolean useTableColumnWidths) {
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
            cell.setCellValue(model.getColumnName(i));
            cell.setCellStyle(styles.get("header"));
            
            if (useTableColumnWidths) {
                // Usar el ancho de la columna del JTable original
                int tableColumnWidth = table.getColumnModel().getColumn(i).getWidth();
                // Convertir píxeles a unidades de Excel (1/256th of a character width)
                // Aproximadamente: 1 píxel ≈ 35-40 unidades de Excel
                int excelWidth = Math.max(1000, tableColumnWidth * 37); // Mínimo 1000 unidades
                sheet.setColumnWidth(i, Math.min(excelWidth, 15000)); // Máximo 15000 unidades
            } else {
                // Usar ancho fijo predeterminado
                sheet.setColumnWidth(i, 4000);
            }
        }
        
        // Añadir datos
        for (int row = 0; row < model.getRowCount(); row++) {
            Row excelRow = sheet.createRow(currentRow++);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = excelRow.createCell(col);
                Object value = model.getValueAt(row, col);
                
                // Manejar diferentes tipos de datos
                if (value == null) {
                    cell.setCellValue("");
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else {
                    cell.setCellValue(value.toString());
                }
                
                // Determinar alineación basada en el parámetro leftAlignedColumns
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
        
        currentRow++; // Espacio después de la tabla
    }

    public boolean saveToFile(String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Excel");
        File defaultFile = new File(defaultFileName + ".xlsx");
        fileChooser.setSelectedFile(defaultFile);

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }
            
            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                workbook.write(fileOut);
                
                // Abrir el archivo automáticamente
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToSave);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                workbook.dispose();
            }
        }
        return false;
    }

    // Método principal de exportación con control de alineación por tabla
    public void exportJTablesToExcel(String reportTitle, int[] leftAlignedColumns, JTable... tables) {
        exportJTablesToExcel(reportTitle, leftAlignedColumns, true, tables);
    }
    
    // Método con control completo de alineación y anchos
    public void exportJTablesToExcel(String reportTitle, int[] leftAlignedColumns, boolean useTableColumnWidths, JTable... tables) {
        // Reiniciar el workbook para cada exportación
        this.workbook = new SXSSFWorkbook();
        this.sheet = workbook.createSheet("Reporte");
        this.currentRow = 0;
        this.styles = createStyles();
        
        // Configurar nombre del archivo
        LocalDateTime now = LocalDateTime.now();
        String fileName = String.format("%s-%02d-%02d-%04d", 
            reportTitle.replaceAll("[^a-zA-Z0-9]", "_"), 
            now.getDayOfMonth(), 
            now.getMonthValue(), 
            now.getYear());
        
        // Añadir título y fecha
        int maxColumns = 0;
        for (JTable table : tables) {
            if (table.getColumnCount() > maxColumns) {
                maxColumns = table.getColumnCount();
            }
        }
        
        addTitle(reportTitle, maxColumns);
        addDate();
        addEmptyRow();
        
        // Añadir cada tabla con la configuración de alineación especificada
        for (JTable table : tables) {
            addJTable(table, null, leftAlignedColumns, useTableColumnWidths);
            addEmptyRow();
        }
        
        // Guardar el archivo
        saveToFile(fileName);
    }
    
    // Método de conveniencia para compatibilidad con código existente
    public void exportJTablesToExcel(String reportTitle, JTable... tables) {
        exportJTablesToExcel(reportTitle, new int[]{0}, true, tables); // Columna 0 alineada a la izquierda por defecto
    }

}