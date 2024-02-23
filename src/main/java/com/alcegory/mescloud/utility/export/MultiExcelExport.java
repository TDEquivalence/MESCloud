package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
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

public class MultiExcelExport extends AbstractExcelExport {

    private static final String SHEET_NAME_PRODUCTION_ORDERS = "Ordens de Produção";
    private static final String SHEET_NAME_COMPOSED = "Produções Compostas";
    private static final String TABLE_NAME_PRODUCTION = "ProductionOrdersTable";
    private static final String TABLE_NAME_COMPOSED = "ComposedProductionOrdersTable";
    private static final String TABLE_STYLE = "TableStyleMedium9";

    public MultiExcelExport() {
        super(null, null, null, null);
        this.workbook = new XSSFWorkbook();
    }

    public void exportDataToExcel(HttpServletResponse response, List<ComposedSummaryEntity> composedList,
                                  List<ProductionOrderSummaryEntity> productionOrders) {

        XSSFSheet composedSheet = createSheet(SHEET_NAME_COMPOSED);
        createHeaderRow(composedSheet, getComposedHeaders());
        writeDataToComposed(composedSheet, composedList);

        XSSFSheet productionSheet = createSheet(SHEET_NAME_PRODUCTION_ORDERS);
        createHeaderRow(productionSheet, getProductionOrderHeaders());
        writeDataToProduction(productionSheet, productionOrders);

        createTable(composedSheet, TABLE_NAME_COMPOSED, getComposedHeaders().length - 1);
        createTable(productionSheet, TABLE_NAME_PRODUCTION, getProductionOrderHeaders().length - 1);

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
        int rowCount = 1; // Start from row 1 (row 0 is header)
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ComposedSummaryEntity composed : composedList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, composed.getBatchCode(), style);
            createCell(row, columnCount++, composed.getCode(), style);
            createCell(row, columnCount++, composed.getInputBatch(), style);
            createCell(row, columnCount++, composed.getSource(), style);
            createCell(row, columnCount++, composed.getGauge(), style);
            createCell(row, columnCount++, composed.getCategory(), style);
            createCell(row, columnCount++, composed.getWashingProcess(), style);
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

    protected void writeDataToProduction(XSSFSheet sheet, List<ProductionOrderSummaryEntity> productionOrders) {
        int rowCount = 1; // Start from row 1 (row 0 is header)
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ProductionOrderSummaryEntity po : productionOrders) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, po.getEquipment() != null ? po.getEquipment().getAlias() : null, style);
            createCell(row, columnCount++, po.getComposedProductionOrder() != null ? po.getComposedProductionOrder()
                    .getCode() : null, style);
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

    private void createTable(XSSFSheet sheet, String tableName, int lastCol) {
        int firstRow = 0;
        int lastRow = sheet.getLastRowNum();
        int firstCol = 0;

        XSSFTable table = createTableObject(sheet, firstRow, lastRow, firstCol, lastCol);
        setTableProperties(table, tableName, TABLE_STYLE);
        addAutoFilter(table, firstRow, lastCol);
        showStripes(table);

        setHeaderColor(workbook, table, 255, 239, 219); // Adjust RGB values as needed
    }

    @Override
    protected XSSFTable createTableObject(XSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        AreaReference areaReference = new AreaReference(new CellReference(firstRow, firstCol),
                new CellReference(lastRow, lastCol), sheet.getWorkbook().getSpreadsheetVersion());
        return sheet.createTable(areaReference);
    }

    private static String[] getComposedHeaders() {
        return new String[]{
                "Lote Final",
                "Lote Final2",
                "Lote de Entrada",
                "Proveniência",
                "Calibre",
                "Classe",
                "Lavação",
                "Quantidade",
                "Amostra",
                "Criação da composta",
                "Hits",
                "Fiabilidade",
                "Hits inseridos em",
                "Status",
                "Resolvido em"
        };
    }

    private static String[] getProductionOrderHeaders() {
        return new String[]{
                "Equipamento",
                "Produção Composta",
                "Ordem de Produção",
                "IMS",
                "Lote de Entrada",
                "Proveniência",
                "Calibre",
                "Classe",
                "Lavação",
                "Quantidade",
                "Início de Produção",
                "Conclusão de Produção"
        };
    }
}
