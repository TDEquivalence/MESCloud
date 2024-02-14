package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.exception.ExcelExportException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
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
    private final String tableStyleStr;
    private final String sheetName;


    protected AbstractExcelExport(List<?> data, String[] headers, String tableName, String tableStyleStr, String sheetName) {
        this.data = data;
        this.headers = headers;
        this.tableName = tableName;
        this.tableStyleStr = tableStyleStr;
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

        if (value instanceof Double doubleValue) {
            double numericValue = doubleValue / 100.0;
            DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
            cell.setCellValue(decimalFormat.format(numericValue));
        } else if (value instanceof Number numberValue) {
            cell.setCellValue(numberValue.doubleValue());
        } else if (value instanceof Date dateValue) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            cell.setCellValue(dateFormat.format(dateValue));
        } else if (value instanceof Boolean booleanValue) {
            cell.setCellValue(Boolean.TRUE.equals(booleanValue) ? "Aprovado" : "Reprovado");
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
        setTableProperties(table, tableName, tableStyleStr);
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

    private void writeWorkbookToResponse(HttpServletResponse response) throws IOException {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            // Log the exception
            e.printStackTrace();
            throw new ExcelExportException("ERROR while exporting excel", e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}