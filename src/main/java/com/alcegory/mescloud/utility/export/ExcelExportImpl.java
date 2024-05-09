package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

import static com.alcegory.mescloud.utility.export.ExcelConstants.*;

public class ExcelExportImpl extends AbstractExcelExport {

    public ExcelExportImpl() {
        super(null, null, null, null);
        this.workbook = new XSSFWorkbook();
    }

    public void exportDataToExcel(HttpServletResponse response, List<ComposedSummaryEntity> composedList,
                                  List<ProductionOrderEntity> productionOrders) {

        XSSFSheet composedSheet = createSheet(SHEET_NAME_COMPOSED);
        createHeaderRow(composedSheet, getComposedHeaders());

        if (!composedList.isEmpty()) {
            writeDataToComposed(composedSheet, composedList);
            createTable(composedSheet, TABLE_NAME_COMPOSED, getComposedHeaders().length - 1);
        }

        XSSFSheet productionSheet = createSheet(SHEET_NAME_PRODUCTION_ORDERS);
        createHeaderRow(productionSheet, getProductionOrderHeaders());

        if (!productionOrders.isEmpty()) {
            writeDataToProduction(productionSheet, productionOrders);
            createTable(productionSheet, TABLE_NAME_PRODUCTION, getProductionOrderHeaders().length - 1);
        }

        writeWorkbookToResponse(response);
    }

    private XSSFSheet createSheet(String sheetName) {
        return workbook.createSheet(sheetName);
    }

    private void createHeaderRow(XSSFSheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle();
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], headerStyle);
        }
    }

    protected void writeDataToComposed(XSSFSheet sheet, List<ComposedSummaryEntity> composedList) {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ComposedSummaryEntity composed : composedList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, composed.getBatchCode(), style);
            createCell(row, columnCount++, composed.getCode(), style);
            /*createCell(row, columnCount++, composed.getInputBatch(), style);
            createCell(row, columnCount++, composed.getSource(), style);
            createCell(row, columnCount++, composed.getGauge(), style);
            createCell(row, columnCount++, composed.getCategory(), style);
            createCell(row, columnCount++, composed.getWashingProcess(), style);*/
            createCell(row, columnCount++, composed.getValidAmount(), style);
            createCell(row, columnCount++, composed.getSampleAmount(), style);
            createCell(row, columnCount++, composed.getCreatedAt(), style);
            createCell(row, columnCount++, composed.getAmountOfHits(), style);
            createCell(row, columnCount++, composed.getReliability(), style);
            createCell(row, columnCount++, composed.getHitInsertedAt(), style);
            createCell(row, columnCount++, composed.getIsBatchApproved(), style);
            createCell(row, columnCount++, composed.getApprovedAt(), style);
        }
    }

    protected void writeDataToProduction(XSSFSheet sheet, List<ProductionOrderEntity> productionOrders) {
        int rowCount = 1; // Start from row 1 (row 0 is header)
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ProductionOrderEntity po : productionOrders) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, po.getEquipment() != null ? po.getEquipment().getAlias() : null, style);
            createCell(row, columnCount++, po.getComposedProductionOrder() != null ? po.getComposedProductionOrder()
                    .getCode() : null, style);
            createCell(row, columnCount++, po.getCode(), style);
            createCell(row, columnCount++, po.getIms() != null ? po.getIms().getCode() : null, style);
            /*createCell(row, columnCount++, po.getInputBatch(), style);
            createCell(row, columnCount++, po.getSource(), style);
            createCell(row, columnCount++, po.getGauge(), style);
            createCell(row, columnCount++, po.getCategory(), style);
            createCell(row, columnCount++, po.getWashingProcess(), style);*/
            createCell(row, columnCount++, po.getValidAmount(), style);
            createCell(row, columnCount++, po.getCreatedAt(), style);
            createCell(row, columnCount++, po.getCompletedAt(), style);
        }
    }

    private void createTable(XSSFSheet sheet, String tableName, int lastCol) {
        int firstRow = 0;
        int lastRow = sheet.getLastRowNum();
        int firstCol = 0;

        XSSFTable table = createTableObject(sheet, firstRow, lastRow, firstCol, lastCol);
        setTableProperties(table, tableName, TABLE_STYLE);
        addAutoFilter(table, firstRow, lastCol);
        showStripes(table);
    }

    @Override
    protected XSSFTable createTableObject(XSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        AreaReference areaReference = new AreaReference(new CellReference(firstRow, firstCol),
                new CellReference(lastRow, lastCol), sheet.getWorkbook().getSpreadsheetVersion());
        return sheet.createTable(areaReference);
    }
}
