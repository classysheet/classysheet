package org.classysheet.core.api;

import org.classysheet.core.impl.data.SheetData;
import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.SheetMeta;
import org.classysheet.core.impl.meta.WorkbookMeta;
import org.classysheet.core.sheet.google.SheetsServiceUtil;
import org.classysheet.excel.impl.ExcelSpreadsheetConnector;
import org.classysheet.googlesheets.impl.GoogleSpreadsheetConnector;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ClassysheetService<Workbook_> {

    public static <Workbook_> ClassysheetService<Workbook_> create(
            Class<Workbook_> workbookClass) {
        return new ClassysheetService<>(new WorkbookMeta(workbookClass));
    }

    private String SPREADSHEET_ID = "19sd5Bw51OsHruzZzkpygXkatBwjSkSxZPZpiW_hzoV8";

    protected final WorkbookMeta workbookMeta;
    protected final GoogleSpreadsheetConnector googleSpreadsheetConnector = new GoogleSpreadsheetConnector();
    protected final ExcelSpreadsheetConnector excelConnector = new ExcelSpreadsheetConnector();

    protected ClassysheetService(WorkbookMeta workbookMeta) {
        this.workbookMeta = workbookMeta;
    }

    protected WorkbookMeta getWorkbookMeta() {
        return workbookMeta;
    }

    protected WorkbookData extractWorkbookData(Workbook_ workbook) {
        return new WorkbookData(workbookMeta, workbook);
    }

    public void writeWorkbookToGoogle(Workbook_ workbook) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        Sheets sheetService = null;
        try {
            sheetService = SheetsServiceUtil.getSheetsService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        Spreadsheet sp = null;
        try {
            sp = sheetService.spreadsheets().get(SPREADSHEET_ID).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Sheet> sheets = sp.getSheets();
        List<String> sheetNames = sheets.stream().map(x -> x.getProperties().getTitle()).toList();
        for (SheetData sheetData : workbookData.sheetDatas()) {
            try {
                String currentSheetName = sheetData.sheetMeta().name();
                if (!sheetNames.contains(currentSheetName)) {
                    List<Request> requests = new ArrayList<>();
                    // Setting the sheet name
                    AddSheetRequest addSheet = new AddSheetRequest();
                    addSheet.setProperties(new SheetProperties().setTitle(currentSheetName));
                    requests.add(new Request().setAddSheet(addSheet));
                    BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                    sheetService.spreadsheets().batchUpdate(SPREADSHEET_ID, requestBody).execute();
                }

                SheetMeta sheetMeta = sheetData.sheetMeta();
                Values sheetValues = sheetService.spreadsheets().values();
                List<ValueRange> data = new ArrayList<>();
                ValueRange columnNames = new ValueRange()
                        .setRange(currentSheetName + "!A1")
                        .setValues(List.of(
                                sheetMeta.columnMetas()
                                        .stream()
                                        .map(columnMeta -> (Object) columnMeta.name())
                                        .toList()
                        ));
                data.add(columnNames);

                sheetData.streamRows().forEach((row) -> {
                    data.add(new ValueRange()
                            .setRange(currentSheetName + "!A" + (row.index() + 2))
                            .setValues(List.of(row.readValuesAsString())));
                    System.out.println("Row: " + row);
                });
                BatchUpdateValuesRequest valuesBody = new BatchUpdateValuesRequest()
                        .setValueInputOption("RAW")
                        .setData(data);
                sheetValues.batchUpdate(SPREADSHEET_ID, valuesBody).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeWorkbookToExcel(Workbook_ workbook) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        excelConnector.writeWorkbook(workbookData);
    }

}
