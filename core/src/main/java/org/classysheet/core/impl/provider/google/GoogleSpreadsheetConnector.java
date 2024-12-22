package org.classysheet.core.impl.provider.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.classysheet.core.impl.data.SheetData;
import org.classysheet.core.impl.meta.SheetMeta;
import org.classysheet.core.impl.provider.SpreadsheetConnector;
import org.classysheet.core.impl.data.WorkbookData;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSpreadsheetConnector implements SpreadsheetConnector {

    @Override
    public void writeWorkbook(WorkbookData workbookData) {

        Sheets sheetService = null;
        try {
            sheetService = SheetsServiceUtil.getSheetsService();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        Spreadsheet sp = null;
        String SPREADSHEET_ID = "19sd5Bw51OsHruzZzkpygXkatBwjSkSxZPZpiW_hzoV8";
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
                Sheets.Spreadsheets.Values sheetValues = sheetService.spreadsheets().values();
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

}
