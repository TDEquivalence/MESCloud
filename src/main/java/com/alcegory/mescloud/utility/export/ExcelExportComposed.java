package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelExportComposed extends AbstractExcelExport {

    private static final String TABLE_NAME = "ComposedProductionOrdersTable";
    private static final String TABLE_STYLE = "TableStyleMedium9";

    private final List<ComposedSummaryEntity> composedList;
    private final boolean withHits;

    private final boolean isCompleted;

    public ExcelExportComposed(List<ComposedSummaryEntity> composedList, boolean withHits, String sheetName, boolean isCompleted) {
        super(composedList, getHeaders(withHits, isCompleted), TABLE_NAME, TABLE_STYLE, sheetName);
        this.composedList = composedList;
        this.withHits = withHits;
        this.isCompleted = isCompleted;
    }

    private static String[] getHeaders(boolean withHits, boolean isCompleted) {
        List<String> headersList = new ArrayList<>();

        if (isCompleted) {
            headersList.add("Lote Final");
        }

        String[] commonHeaders = {
                "Produção Composta",
                "Lote de Entrada",
                "Proveniência",
                "Calibre",
                "Classe",
                "Lavação",
                "Quantidade",
                "Amostra",
                "Criação da composta"
        };

        headersList.addAll(Arrays.asList(commonHeaders));

        if (withHits) {
            headersList.addAll(Arrays.asList("Hits", "Fiabilidade", "Hits inseridos em"));
        }

        if (isCompleted) {
            headersList.add("Status");
            headersList.add("Resolvido em");
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

        for (ComposedSummaryEntity composed : composedList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            if (isCompleted) {
                createCell(row, columnCount++, composed.getBatchCode(), style);
            }

            createCell(row, columnCount++, composed.getCode(), style);
            createCell(row, columnCount++, composed.getInputBatch(), style);
            createCell(row, columnCount++, composed.getSource(), style);
            createCell(row, columnCount++, composed.getGauge(), style);
            createCell(row, columnCount++, composed.getCategory(), style);
            createCell(row, columnCount++, composed.getWashingProcess(), style);
            createCell(row, columnCount++, composed.getValidAmount(), style);
            createCell(row, columnCount++, composed.getSampleAmount(), style);
            createCell(row, columnCount++, composed.getCreatedAt(), style);

            if (withHits) {
                createCell(row, columnCount++, composed.getAmountOfHits(), style);
                createCell(row, columnCount++, composed.getReliability(), style);
                createCell(row, columnCount++, composed.getHitInsertedAt(), style);
            }

            if (isCompleted) {
                createCell(row, columnCount++, composed.getIsBatchApproved(), style);
                createCell(row, columnCount++, composed.getApprovedAt(), style);
            }
        }
    }
}
