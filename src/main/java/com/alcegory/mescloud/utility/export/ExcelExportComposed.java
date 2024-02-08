package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.List;

public class ExcelExportComposed extends AbstractExcelExport {

    private static final String SHEET_NAME = "Produções Compostas";
    private static final String TABLE_NAME = "ComposedProductionOrdersTable";
    private static final String TABLE_STYLE = "TableStyleMedium9";

    private final List<ComposedSummaryEntity> composedList;

    public ExcelExportComposed(List<ComposedSummaryEntity> composedList) {
        super(composedList, new String[]{
                "Produção Composta",
                "Lote de Entrada",
                "Proveniência",
                "Calibre",
                "Classe",
                "Lavação",
                "Amostra",
                "Data e Hora"
        }, TABLE_NAME, TABLE_STYLE, SHEET_NAME);
        this.composedList = composedList;
    }

    @Override
    protected void writeData() {
        int rowCount = 1; // Start from row 1 (row 0 is header)
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ComposedSummaryEntity composed : composedList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, composed.getCode(), style);
            createCell(row, columnCount++, composed.getInputBatch(), style);
            createCell(row, columnCount++, composed.getSource(), style);
            createCell(row, columnCount++, composed.getGauge(), style);
            createCell(row, columnCount++, composed.getCategory(), style);
            createCell(row, columnCount++, composed.getWashingProcess(), style);
            createCell(row, columnCount++, composed.getSampleAmount(), style);
            createCell(row, columnCount++, composed.getCreatedAt(), style);
        }
    }
}
