package org.classysheet.examples;

import org.classysheet.core.api.ClassysheetService;
import org.classysheet.examples.data.ScheduleGenerator;
import org.classysheet.examples.domain.Schedule;

public class GoogleSheetsExample {

    public static void main(String[] args) {
        // Once, at build or bootstrap time:
        ClassysheetService<Schedule> classysheetService = ClassysheetService.create(Schedule.class);

        // At runtime, per workbook:
        Schedule schedule = ScheduleGenerator.generateDemoData();
        classysheetService.writeWorkbookToGoogle(schedule);
    }

}
