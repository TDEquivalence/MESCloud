package com.alcegory.mescloud.utility.export;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class AbstractExcelExport {

    private static final String DECIMAL_FORMAT_PATTERN = "#0.00%";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    protected final XSSFWorkbook workbook;

    protected XSSFSheet sheet;
    private final List<?> data;
    private final String[] headers;
    private final String tableName;
    private final String tableStyle;
    private final String sheetName;


    protected AbstractExcelExport(List<?> data, String[] headers, String tableName, String tableStyle, String sheetName) {
        this.data = data;
        this.headers = headers;
        this.tableName = tableName;
        this.tableStyle = tableStyle;
        this.sheetName = sheetName;
        workbook = new XSSFWorkbook();
    }

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createHeaderRow();
        writeData();
        createTable();
        writeWorkbookToResponse(response);
    }

    private void createHeaderRow() {
        sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle();
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], headerStyle);
        }
    }

    private CellStyle createHeaderStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    protected void writeData() {
    }

    protected void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);

        if (value instanceof Number) {
            if (value instanceof Double) {
                double numericValue = ((Number) value).doubleValue() / 100.0; // Divide by 100
                DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT_PATTERN); // Format as percentage with two decimal places
                cell.setCellValue(decimalFormat.format(numericValue));
            } else {
                cell.setCellValue(((Number) value).doubleValue());
            }
        } else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            cell.setCellValue(dateFormat.format((Date) value)); // Apply date format
        } else if (value instanceof Boolean) {
            boolean booleanValue = (Boolean) value;
            cell.setCellValue(booleanValue ? "Aprovado" : "Reprovado");
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }

        style.setAlignment(HorizontalAlignment.LEFT);
        cell.setCellStyle(style);
    }

    private void createTable() {
        int firstRow = 0;
        int lastRow = data.size();
        int firstCol = 0;
        int lastCol = headers.length - 1;

        XSSFSheet sheetTable = workbook.getSheetAt(0);
        XSSFTable table = createTableObject(sheetTable, firstRow, lastRow, firstCol, lastCol);
        setTableProperties(table, tableName, tableStyle);
        defineTableColumns(table);
        addAutoFilter(table, firstRow, lastCol);
        showStripes(table);
    }

    private void addAutoFilter(XSSFTable table, int firstRow, int lastCol) {
        CTTable ctTable = table.getCTTable();
        String range = "A1:" + CellReference.convertNumToColString(lastCol) + (firstRow + 1);
        ctTable.addNewAutoFilter().setRef(range);
    }

    private void showStripes(XSSFTable table) {
        CTTable ctTable = table.getCTTable();
        CTTableStyleInfo tableStyle = ctTable.getTableStyleInfo();

        if (tableStyle == null) {
            tableStyle = ctTable.addNewTableStyleInfo();
        }

        tableStyle.setShowRowStripes(true);
    }

    private XSSFTable createTableObject(XSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        AreaReference areaReference = new AreaReference(new CellReference(firstRow, firstCol),
                new CellReference(lastRow, lastCol), workbook.getSpreadsheetVersion());
        XSSFTable table = sheet.createTable(areaReference);
        table.setDisplayName(tableName);
        table.setName(tableName);
        return table;
    }

    private void setTableProperties(XSSFTable table, String tableName, String tableStyle) {
        CTTable ctTable = table.getCTTable();
        CTTableStyleInfo tableStyleInfo = ctTable.addNewTableStyleInfo();
        tableStyleInfo.setName(tableStyle);
        ctTable.setTableStyleInfo(tableStyleInfo);
        table.setDisplayName(tableName);
        table.setName(tableName);
    }

    private void defineTableColumns(XSSFTable table) {
        CTTable ctTable = table.getCTTable();
        CTTableColumns ctTableColumns = ctTable.getTableColumns();
        for (int i = 0; i < headers.length; i++) {
            CTTableColumn ctTableColumn = ctTableColumns.addNewTableColumn();
            ctTableColumn.setName(headers[i]);
            ctTableColumn.setId((long) i + 1); // Column IDs start from 1
        }
    }

    private void writeWorkbookToResponse(HttpServletResponse response) throws IOException {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }
}