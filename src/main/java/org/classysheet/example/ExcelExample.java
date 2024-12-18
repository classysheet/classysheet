package org.classysheet.example;

import org.classysheet.core.api.ClassysheetService;
import org.classysheet.example.data.ScheduleGenerator;
import org.classysheet.example.domain.Schedule;

public class ExcelExample {

    public static void main(String[] args) {
        // Once, at build or bootstrap time:
        ClassysheetService<Schedule> classysheetService = ClassysheetService.create(Schedule.class);

        // At runtime, per workbook:
        Schedule schedule = ScheduleGenerator.generateDemoData();
        classysheetService.writeWorkbookToExcel(schedule);
    }

}
