package com.alcegory.mescloud.utility;

import com.alcegory.mescloud.model.entity.ProductionOrderView;
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

    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final List<ProductionOrderView> productionOrders;
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

    public ExcelExportUtil(List<ProductionOrderView> productionOrders) {
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
        sheet = workbook.createSheet("Ordens de Produção");
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

        for (ProductionOrderView po : productionOrders) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, po.getCountingEquipmentAlias(), style);
            createCell(row, columnCount++, po.getProductionOrderCode(), style);
            createCell(row, columnCount++, po.getImsCode(), style);
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

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFTable table = createTableObject(sheet, firstRow, lastRow, firstCol, lastCol);
        setTableProperties(table);
        defineTableColumns(table);
        addAutoFilter(table);
        showStripes(table);
    }

    private void addAutoFilter(XSSFTable table) {
        CTTable ctTable = table.getCTTable();
        ctTable.addNewAutoFilter().setRef("A1:" + CellReference.convertNumToColString(headers.length - 1) + "1");
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
        table.setDisplayName("ProductionOrdersTable");
        table.setName("ProductionOrdersTable");
        return table;
    }

    private void setTableProperties(XSSFTable table) {
        CTTable ctTable = table.getCTTable();
        CTTableStyleInfo tableStyle = ctTable.addNewTableStyleInfo();
        tableStyle.setName("TableStyleMedium9");
        ctTable.setTableStyleInfo(tableStyle);
    }

    private void defineTableColumns(XSSFTable table) {
        CTTable ctTable = table.getCTTable();
        CTTableColumns ctTableColumns = ctTable.getTableColumns();
        for (int i = 0; i < headers.length; i++) {
            CTTableColumn ctTableColumn = ctTableColumns.addNewTableColumn();
            ctTableColumn.setName(headers[i]);
            ctTableColumn.setId(i + 1); // Column IDs start from 1
        }
    }

    private void writeWorkbookToResponse(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}