package com.alcegory.mescloud.service.export;

import com.alcegory.mescloud.model.dto.composed.ComposedExportInfoDto;
import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderExportInfoDto;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.alcegory.mescloud.service.export.ExcelConstants.*;

public class ExportExcelImpl extends AbstractExcelExport {

    private static final String INSTRUCTIONS = "instructions";

    public ExportExcelImpl() {
        super(null, null, null, null);
        this.workbook = new XSSFWorkbook();
    }

    public void exportDataToExcel(HttpServletResponse response, List<ComposedExportInfoDto> composedList,
                                  List<ProductionOrderExportInfoDto> productionOrders) {

        XSSFSheet composedSheet = createSheet(SHEET_NAME_COMPOSED);
        Set<String> composedHeaders = createComposedHeaderRow(composedSheet, composedList);

        if (!composedList.isEmpty()) {
            createTable(composedSheet, TABLE_NAME_COMPOSED, composedHeaders.size());
            writeDataToComposed(composedSheet, composedList);
        }

        XSSFSheet productionSheet = createSheet(SHEET_NAME_PRODUCTION_ORDERS);

        if (!productionOrders.isEmpty()) {
            createTable(productionSheet, TABLE_NAME_PRODUCTION, composedHeaders.size());
            writeDataToProduction(productionSheet, productionOrders);
        }

        writeWorkbookToResponse(response);
    }

    private XSSFSheet createSheet(String sheetName) {
        return workbook.createSheet(sheetName);
    }

    private Set<String> createComposedHeaderRow(XSSFSheet sheet, List<ComposedExportInfoDto> composedList) {
        Set<String> fieldSet = new LinkedHashSet<>();
        Map<String, String> portugueseFieldNames = translateFieldNamesMapping();

        Class<?> composedClass = ComposedExportInfoDto.class;
        Field[] fields = composedClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getName().equals(INSTRUCTIONS)) {
                String portugueseFieldName = portugueseFieldNames.get(field.getName());
                if (portugueseFieldName != null) {
                    fieldSet.add(portugueseFieldName);
                } else {
                    fieldSet.add(field.getName());
                }
            }
        }

        for (ComposedExportInfoDto composedExportInfoDto : composedList) {
            for (ProductionInstructionDto productionInstructionDto : composedExportInfoDto.getInstructions()) {
                fieldSet.add(productionInstructionDto.getName());
            }
        }

        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle();

        int columnIndex = 0;
        for (String fieldName : fieldSet) {
            createCell(headerRow, columnIndex++, fieldName, headerStyle);
        }

        return fieldSet;
    }

    protected void writeDataToComposed(XSSFSheet sheet, List<ComposedExportInfoDto> composedList) {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ComposedExportInfoDto composed : composedList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, composed.getBatchCode(), style);
            createCell(row, columnCount++, composed.getComposedCreatedAt(), style);
            createCell(row, columnCount++, composed.getComposedCode(), style);
            createCell(row, columnCount++, composed.getValidAmount(), style);
            createCell(row, columnCount++, composed.getSampleAmount(), style);
            createCell(row, columnCount++, composed.getAmountOfHits(), style);
            createCell(row, columnCount++, composed.getHitInsertedAt(), style);
            createCell(row, columnCount++, composed.getReliability(), style);
            createCell(row, columnCount++, composed.getIsBatchApproved(), style);
            createCell(row, columnCount++, composed.getApprovedAt(), style);

            for (ProductionInstructionDto instruction : composed.getInstructions()) {
                createCell(row, columnCount++, instruction.getValue(), style);
            }
        }
    }

    protected void writeDataToProduction(XSSFSheet sheet, List<ProductionOrderExportInfoDto> productionOrders) {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ProductionOrderExportInfoDto po : productionOrders) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, po.getEquipment(), style);
            createCell(row, columnCount++, po.getComposedCode(), style);
            createCell(row, columnCount++, po.getProductionCode(), style);
            createCell(row, columnCount++, po.getIms(), style);
            createCell(row, columnCount++, po.getValidAmount(), style);
            createCell(row, columnCount++, po.getProductionCreatedAt(), style);
            createCell(row, columnCount++, po.getProductionCompletedAt(), style);

            List<ProductionInstructionDto> instructionList = po.getInstructions();
            for (ProductionInstructionDto instruction : instructionList) {
                createCell(row, columnCount++, instruction.getValue(), style);
            }
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
