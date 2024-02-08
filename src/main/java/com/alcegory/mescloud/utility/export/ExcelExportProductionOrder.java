package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.List;

public class ExcelExportProductionOrder extends AbstractExcelExport {

    private static final String TABLE_NAME = "ProductionOrdersTable";
    private static final String TABLE_STYLE = "TableStyleMedium9";

    private final List<ProductionOrderSummaryEntity> productionOrders;

    public ExcelExportProductionOrder(List<ProductionOrderSummaryEntity> productionOrders, String sheetName) {
        super(productionOrders, new String[]{
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
                "Término de Produção"
        }, TABLE_NAME, TABLE_STYLE, sheetName);
        this.productionOrders = productionOrders;
    }

    @Override
    protected void writeData() {
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
}
