package org.classysheet.core.api;

import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.ColumnMeta;
import org.classysheet.core.impl.meta.SheetMeta;
import org.classysheet.core.impl.meta.WorkbookMeta;
import org.classysheet.example.domain.Employee;
import org.classysheet.example.domain.Schedule;
import org.classysheet.example.domain.Shift;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClassysheetServiceTest {

    @Test
    void getWorkbookMeta() {
        ClassysheetService<Schedule> classysheetService = ClassysheetService.create(Schedule.class);
        WorkbookMeta workbookMeta = classysheetService.getWorkbookMeta();
        assertThat(workbookMeta).isNotNull();
        assertThat(workbookMeta.workbookClass()).isEqualTo(Schedule.class);
        List<SheetMeta> sheetMetas = workbookMeta.sheetMetas();
        assertThat(sheetMetas).hasSize(2);

        SheetMeta employeeSheetMeta = sheetMetas.stream()
                .filter(sheetMeta -> sheetMeta.name().equals("Employees"))
                .findFirst().get();
        assertThat(employeeSheetMeta.sheetClass()).isEqualTo(Employee.class);
        List<ColumnMeta> employeeColumnMetas = employeeSheetMeta.columnMetas();
        assertThat(employeeColumnMetas).hasSize(3);

        SheetMeta shiftsSheetMeta = sheetMetas.stream()
                .filter(sheetMeta -> sheetMeta.name().equals("Shifts"))
                .findFirst().get();
        assertThat(shiftsSheetMeta.sheetClass()).isEqualTo(Shift.class);
        List<ColumnMeta> shiftColumnMetas = shiftsSheetMeta.columnMetas();
        assertThat(shiftColumnMetas).hasSize(4);
    }

    final List<TestSheet> TEST_SHEETS = List.of(
            new TestSheet("Ann", 1, LocalDate.of(2000, 1, 1), LocalTime.of(1,0), LocalDateTime.of(2000, 1, 2, 1, 1)),
            new TestSheet("Beth", 2, LocalDate.of(2000, 2, 1), LocalTime.of(2,0), LocalDateTime.of(2000, 2, 2, 2, 1))
    );
    final List<TestSheetAlt> TEST_SHEET_ALTS = List.of(
            new TestSheetAlt("Ghent"),
            new TestSheetAlt("London")
    );
    final TestWorkbook WORKBOOK = new TestWorkbook(TEST_SHEETS, TEST_SHEET_ALTS);

    @Test
    void extractWorkbookData() {
        ClassysheetService<TestWorkbook> classysheetService = ClassysheetService.create(TestWorkbook.class);
        WorkbookData workbookData = classysheetService.extractWorkbookData(WORKBOOK);
        assertThat(workbookData).isNotNull();


    }


}