package org.classysheet.core.api;

import org.classysheet.core.api.domain.*;
import org.classysheet.core.impl.DefaultClassysheetService;
import org.classysheet.core.impl.data.WorkbookData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassysheetServiceTest {

    public final List<TestRow> TEST_SHEETS = List.of(
            new TestRow("Ann", 1, LocalDate.of(2000, 1, 1), LocalTime.of(1,0), LocalDateTime.of(2000, 1, 2, 1, 1),
                    TestEnum.FIRST),
            new TestRow("Beth", 2, LocalDate.of(2000, 2, 1), LocalTime.of(2,0), LocalDateTime.of(2000, 2, 2, 2, 1),
                    TestEnum.THIRD)
    );
    public final List<TestSimpleRow> TEST_SIMPLE_SHEET = List.of(
            new TestSimpleRow("Ghent"),
            new TestSimpleRow("London")
    );
    public final TestWorkbook WORKBOOK = new TestWorkbook(TEST_SHEETS, TEST_SIMPLE_SHEET, "Ignored status");

    @Test
    void extractWorkbookData() {
        DefaultClassysheetService<TestWorkbook> classysheetService = new DefaultClassysheetService<>(TestWorkbook.class);
        WorkbookData workbookData = classysheetService.extractWorkbookData(WORKBOOK);
        assertThat(workbookData).isNotNull();
    }

    @Test
    void excel() throws IOException {
        ClassysheetService<TestWorkbook> classysheetService = ClassysheetService.create(TestWorkbook.class);

        TestWorkbook workbook1 = WORKBOOK;
        TestWorkbook workbook2;

        // TODO extract these methods to easily test workbook variations in isolation
        Path testDir = Paths.get("target/test");
        Files.createDirectories(testDir);
        Path tempFile = Files.createTempFile(testDir, "test-", ".xls");
        classysheetService.writeExcelOutputStream(workbook1, Files.newOutputStream(tempFile));
        workbook2 = classysheetService.readExcelInputStream(Files.newInputStream(tempFile));
        Files.deleteIfExists(tempFile);

        assertThat(workbook2).isNotNull();
        assertThat(workbook2.rows()).containsExactly(workbook1.rows().toArray(TestRow[]::new));
        assertThat(workbook2.simpleRows()).containsExactly(workbook1.simpleRows().toArray(TestSimpleRow[]::new));
    }


    // TODO test workbook with non-list fields that are not ignored
    // TODO test sheet with non supported fields that are not ignored

}