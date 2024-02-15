package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelExportProductionOrder extends AbstractExcelExport {

    private static final String TABLE_NAME = "ProductionOrdersTable";

    private final List<ProductionOrderSummaryEntity> productionOrders;

    private final boolean isCompleted;

    public ExcelExportProductionOrder(List<ProductionOrderSummaryEntity> productionOrders, String sheetName, boolean isCompleted) {
        super(productionOrders, getHeaders(isCompleted), TABLE_NAME, sheetName);
        this.productionOrders = productionOrders;
        this.isCompleted = isCompleted;
    }

    private static String[] getHeaders(boolean isCompleted) {
        List<String> headersList = new ArrayList<>();

        String[] commonHeaders = {
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
                "Conclusão de Produção"
        };

        // Add common headers
        headersList.addAll(Arrays.asList(commonHeaders));

        if (isCompleted) {
            headersList.add(1, "Produção Composta");
        }

        return headersList.toArray(new String[0]);
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

            if (isCompleted) {
                createCell(row, columnCount++, po.getComposedProductionOrder() != null ? po.getComposedProductionOrder()
                        .getCode() : null, style);
            }

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
