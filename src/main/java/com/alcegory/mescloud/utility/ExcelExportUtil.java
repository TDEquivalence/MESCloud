package com.alcegory.mescloud.utility;

import com.alcegory.mescloud.model.entity.ProductionOrderView;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelExportUtil {

    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final List<ProductionOrderView> productionOrders;

    public ExcelExportUtil(List<ProductionOrderView> productionOrders) {
        this.productionOrders = productionOrders;
        workbook = new XSSFWorkbook();
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);

        if (value instanceof Integer || value instanceof Double || value instanceof Boolean || value instanceof Long) {
            cell.setCellValue(value.toString());
        } else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell.setCellValue(dateFormat.format((Date) value));
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }

        cell.setCellStyle(style);
    }

    private void createHeaderRow() {
        sheet = workbook.createSheet("Ordens de Produção");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Ordens de Produção", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Equipamento", style);
        createCell(row, 1, "Ordem de Produção", style);
        createCell(row, 2, "IMS", style);
        createCell(row, 3, "Lote de Entrada", style);
        createCell(row, 4, "Proveniência", style);
        createCell(row, 5, "Calibre", style);
        createCell(row, 6, "Classe", style);
        createCell(row, 7, "Lavação", style);
        createCell(row, 8, "Quantidade", style);
        createCell(row, 9, "Início de Produção", style);
        createCell(row, 10, "Término de Produção", style);
    }

    private void writeData() {
        int rowCount = 2;
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

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createHeaderRow();
        writeData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}