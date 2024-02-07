package com.alcegory.mescloud.utility;

import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelExportUtil {

    private static final String SHEET_NAME = "Ordens de Produção";

    private static final String TABLE_NAME = "ProductionOrdersTable";
    private static final String TABLE_STYLE = "TableStyleMedium9";

    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final List<ProductionOrderSummaryEntity> productionOrders;
    private final String[] headers = {
            "Equipamento",
            "Ordem de Produção",
            "IMS",
            "Lote de Entrada",
            "Proveniência",
            "Calibre",
            "Classe",
            "Lavação",
            "Quantidade",
            "Início de Produção",
            "Término de Produção"};

    public ExcelExportUtil(List<ProductionOrderSummaryEntity> productionOrders) {
        this.productionOrders = productionOrders;
        workbook = new XSSFWorkbook();
    }

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createHeaderRow();
        writeData();
        createTable();
        writeWorkbookToResponse(response);
    }

    private void createHeaderRow() {
        sheet = workbook.createSheet(SHEET_NAME);
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

    private void writeData() {
        int rowCount = 1; // Start from row 1 (row 0 is header)
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ProductionOrderSummaryEntity po : productionOrders) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, po.getEquipment() != null ? po.getEquipment().getAlias() : null, style);
            createCell(row, columnCount++, po.getCode(), style);
            createCell(row, columnCount++, po.getIms() != null ? po.getIms().getCode() : null, style);
            createCell(row, columnCount++, po.getInputBatch(), style);
            createCell(row, columnCount++, po.getSource(), style);
            createCell(row, columnCount++, po.getGauge(), style);
            createCell(row, columnCount++, po.getCategory(), style);
            createCell(row, columnCount++, po.getWashingProcess(), style);
            createCell(row, columnCount++, po.getValidAmount(), style);
            createCell(row, columnCount++, po.getCreatedAt(), style);
            createCell(row, columnCount++, po.getCompletedAt(), style);
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);

        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell.setCellValue(dateFormat.format((Date) value));// Apply date format
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }

        cell.setCellStyle(style);
    }

    private void createTable() {
        int firstRow = 0;
        int lastRow = productionOrders.size();
        int firstCol = 0;
        int lastCol = headers.length - 1;

        XSSFSheet sheetTable = workbook.getSheetAt(0);
        XSSFTable table = createTableObject(sheetTable, firstRow, lastRow, firstCol, lastCol);
        setTableProperties(table, TABLE_NAME, TABLE_STYLE);
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
        table.setDisplayName(TABLE_NAME);
        table.setName(TABLE_NAME);
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