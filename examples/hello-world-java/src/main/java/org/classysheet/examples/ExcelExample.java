package org.classysheet.examples;

import org.classysheet.core.api.ClassysheetService;
import org.classysheet.examples.data.ScheduleGenerator;
import org.classysheet.examples.domain.Employee;
import org.classysheet.examples.domain.Schedule;
import org.classysheet.examples.domain.Shift;

import java.io.File;

public class ExcelExample {

    public static void main(String[] args) {
        // Setup: once, at build or bootstrap time
        ClassysheetService<Schedule> classysheetService = ClassysheetService.create(Schedule.class);

        // Write a workbook: per workbook, at runtime
        Schedule schedule = ScheduleGenerator.generateDemoData();
        File file = classysheetService.writeExcelTmpFileAndShow(schedule);

        // Read a workbook: per workbook, at runtime
        Schedule schedule2 = classysheetService.readExcelFile(file);
        System.out.println("Employees");
        for (Employee employee : schedule2.getEmployees()) {
            System.out.println("  " + employee);
        }
        System.out.println("Shifts");
        for (Shift shift : schedule2.getShifts()) {
            System.out.println("  " + shift);
        }
    }

}
