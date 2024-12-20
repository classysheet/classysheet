package org.classysheet.examples;

import org.classysheet.core.api.ClassysheetService;
import org.classysheet.examples.data.ScheduleGenerator;
import org.classysheet.examples.domain.Schedule;
import org.classysheet.core.impl.provider.google.SheetsServiceUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

public class GoogleSheetsExample {
    public static void main(String[] args) {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService();

            Spreadsheet newSpreadsheet = new Spreadsheet()
                    .setProperties(new SpreadsheetProperties().setTitle("ClassySheet Example Workbook"));
            Spreadsheet createdSpreadsheet = sheetsService.spreadsheets().create(newSpreadsheet).execute();
            String spreadsheetId = createdSpreadsheet.getSpreadsheetId();
            if (spreadsheetId == null) {
                throw new RuntimeException("Error creating workbook on google sheets");
            }

            ClassysheetService<Schedule> classysheetService = ClassysheetService.<Schedule>create(Schedule.class, spreadsheetId);

            Schedule schedule = ScheduleGenerator.generateDemoData();

            classysheetService.writeWorkbookToGoogle(schedule);

            System.out.println("data successfully written on" + spreadsheetId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
