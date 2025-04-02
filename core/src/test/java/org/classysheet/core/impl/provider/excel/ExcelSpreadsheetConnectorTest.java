package org.classysheet.core.impl.provider.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.classysheet.core.api.domain.TestSimpleRow;
import org.classysheet.core.api.domain.TestSimpleWorkbook;
import org.classysheet.core.impl.DefaultClassysheetService;
import org.classysheet.core.impl.data.WorkbookData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class ExcelSpreadsheetConnectorTest {

    @Test
    void readWorkbookWithEmptyRows() throws IOException {
        TestSimpleWorkbook in = new TestSimpleWorkbook(List.of(
                new TestSimpleRow("Ghent"),
                new TestSimpleRow("London")
        ));
        TestSimpleWorkbook out = writeRead(in, (Workbook workbook) -> {
            // Add empty rows to Excel workbook
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            sheet.createRow(lastRowNum + 1).createCell(0).setCellValue((String) null);
            sheet.createRow(lastRowNum + 2);
        });

        assertThat(out.rows()).hasSize(2);
    }

    private static TestSimpleWorkbook writeRead(TestSimpleWorkbook in, Consumer<Workbook> modifier)
            throws IOException {
        DefaultClassysheetService<TestSimpleWorkbook> classysheetService = new DefaultClassysheetService<>(TestSimpleWorkbook.class);
        ExcelSpreadsheetConnector excelSpreadsheetConnector = new ExcelSpreadsheetConnector();
        WorkbookData inData = classysheetService.extractWorkbookData(in);
        try (Workbook workbook = new XSSFWorkbook()) {
            excelSpreadsheetConnector.writeWorkbook(inData, workbook);

            modifier.accept(workbook);

            WorkbookData outData = excelSpreadsheetConnector.readWorkbook(classysheetService.getWorkbookMeta(), workbook);
            return (TestSimpleWorkbook) outData.workbook();
        }
    }

}