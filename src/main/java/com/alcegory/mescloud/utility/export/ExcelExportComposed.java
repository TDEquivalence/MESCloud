package com.alcegory.mescloud.utility.export;

import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.List;

import static com.alcegory.mescloud.utility.export.ExcelConstants.TABLE_NAME_COMPOSED;
import static com.alcegory.mescloud.utility.export.ExcelConstants.getHeaders;

public class ExcelExportComposed extends AbstractExcelExport {


    private final List<ComposedSummaryEntity> composedList;
    private final boolean withHits;
    private final boolean isCompleted;

    public ExcelExportComposed(List<ComposedSummaryEntity> composedList, boolean withHits, String sheetName, boolean isCompleted) {
        super(composedList, getHeaders(withHits, isCompleted), TABLE_NAME_COMPOSED, sheetName);
        this.composedList = composedList;
        this.withHits = withHits;
        this.isCompleted = isCompleted;
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
