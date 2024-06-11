package com.alcegory.mescloud.utility.export;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AbstractExcelExportTest {

    @Mock
    private HttpServletResponse response;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void testExportDataToExcel() throws IOException {
        List<?> testData = Arrays.asList("Data1", "Data2", "Data3");
        String[] headers = {"Header1", "Header2", "Header3"};
        String tableName = "TableName";
        String sheetName = "SheetName";

        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStreamWrapper(outputStream));

        AbstractExcelExport excelExport = new AbstractExcelExport(testData, headers, tableName, sheetName) {
            @Override
            protected void writeData() {
                // Do nothing, we are not testing this method
            }
        };

        excelExport.exportDataToExcel(response);
        assertTrue(outputStream.size() > 0);
    }

    private static class ByteArrayOutputStreamWrapper extends ServletOutputStream {
        private final ByteArrayOutputStream outputStream;

        ByteArrayOutputStreamWrapper(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }
}

