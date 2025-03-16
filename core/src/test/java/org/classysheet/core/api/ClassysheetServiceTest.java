package org.classysheet.core.api;

import org.classysheet.core.api.domain.*;
import org.classysheet.core.impl.data.WorkbookData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClassysheetServiceTest {

//    @Test
//    void getWorkbookMeta() {
//        ClassysheetService<Schedule> classysheetService = ClassysheetService.create(Schedule.class);
//        WorkbookMeta workbookMeta = classysheetService.getWorkbookMeta();
//        assertThat(workbookMeta).isNotNull();
//        assertThat(workbookMeta.workbookClass()).isEqualTo(Schedule.class);
//        List<SheetMeta> sheetMetas = workbookMeta.sheetMetas();
//        assertThat(sheetMetas).hasSize(2);
//
//        SheetMeta employeeSheetMeta = sheetMetas.stream()
//                .filter(sheetMeta -> sheetMeta.name().equals("Employees"))
//                .findFirst().get();
//        assertThat(employeeSheetMeta.sheetClass()).isEqualTo(Employee.class);
//        List<ColumnMeta> employeeColumnMetas = employeeSheetMeta.columnMetas();
//        assertThat(employeeColumnMetas).hasSize(3);
//
//        SheetMeta shiftsSheetMeta = sheetMetas.stream()
//                .filter(sheetMeta -> sheetMeta.name().equals("Shifts"))
//                .findFirst().get();
//        assertThat(shiftsSheetMeta.sheetClass()).isEqualTo(Shift.class);
//        List<ColumnMeta> shiftColumnMetas = shiftsSheetMeta.columnMetas();
//        assertThat(shiftColumnMetas).hasSize(4);
//    }

    final List<TestSheet> TEST_SHEETS = List.of(
            new TestSheet("Ann", 1, LocalDate.of(2000, 1, 1), LocalTime.of(1,0), LocalDateTime.of(2000, 1, 2, 1, 1),
                    TestEnum.FIRST),
            new TestSheet("Beth", 2, LocalDate.of(2000, 2, 1), LocalTime.of(2,0), LocalDateTime.of(2000, 2, 2, 2, 1),
                    TestEnum.THIRD)
    );
    final List<TestSheetAlt> TEST_SHEET_ALTS = List.of(
            new TestSheetAlt("Ghent"),
            new TestSheetAlt("London")
    );
    final TestWorkbook WORKBOOK = new TestWorkbook(TEST_SHEETS, TEST_SHEET_ALTS, "Ignored status");

    @Test
    void extractWorkbookData() {
        ClassysheetService<TestWorkbook> classysheetService = ClassysheetService.create(TestWorkbook.class);
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
        assertThat(workbook2.testSheets()).containsExactly(workbook1.testSheets().toArray(TestSheet[]::new));
        assertThat(workbook2.testSheetAlts()).containsExactly(workbook1.testSheetAlts().toArray(TestSheetAlt[]::new));
    }


    // TODO test workbook with non-list fields that are not ignored
    // TODO test sheet with non supported fields that are not ignored

}