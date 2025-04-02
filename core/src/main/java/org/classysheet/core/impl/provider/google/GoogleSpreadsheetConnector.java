package org.classysheet.core.impl.provider.google;

import org.classysheet.core.impl.provider.SpreadsheetConnector;
import org.classysheet.core.impl.data.SheetData;
import org.classysheet.core.impl.meta.SheetMeta;
import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.ColumnMeta;
import org.classysheet.core.impl.data.RowData;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSpreadsheetConnector {

    private final String spreadsheetId;
    private final Sheets sheetsService;

    public GoogleSpreadsheetConnector(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
        try {
            this.sheetsService = SheetsServiceUtil.getSheetsService();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to initialize Google Sheets service", e);
        }
    }

    @Override
    public void writeWorkbook(WorkbookData workbookData) {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            List<String> existingSheetNames = spreadsheet.getSheets().stream()
                    .map(sheet -> sheet.getProperties().getTitle())
                    .toList();

            List<Request> requests = new ArrayList<>();

            for (SheetData sheetData : workbookData.sheetDatas()) {
                String sheetName = sheetData.sheetMeta().name();
                if (!existingSheetNames.contains(sheetName)) {
                    AddSheetRequest addSheetRequest = new AddSheetRequest()
                            .setProperties(new SheetProperties().setTitle(sheetName));
                    requests.add(new Request().setAddSheet(addSheetRequest));
                }
            }

            if (!requests.isEmpty()) {
                BatchUpdateSpreadsheetRequest batchAddRequest = new BatchUpdateSpreadsheetRequest()
                        .setRequests(requests);
                sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchAddRequest).execute();
            }

            requests.clear();

            List<ValueRange> data = new ArrayList<>();

            for (SheetData sheetData : workbookData.sheetDatas()) {
                String sheetName = sheetData.sheetMeta().name();

                List<Object> headers = new ArrayList<>();
                for (ColumnMeta columnMeta : sheetData.sheetMeta().columnMetas()) {
                    headers.add(columnMeta.name());
                }

                ValueRange headerRange = new ValueRange()
                        .setRange(sheetName + "!A1")
                        .setValues(List.of(headers));
                data.add(headerRange);

                AtomicInteger rowIndex = new AtomicInteger(2);
                sheetData.streamRows().forEach(rowData -> {
                    List<Object> rowValues = new ArrayList<>();
                    for (ColumnMeta columnMeta : sheetData.sheetMeta().columnMetas()) {
                        Object cellValue = getCellValue(rowData, columnMeta);
                        rowValues.add(cellValue);
                    }
                    ValueRange rowRange = new ValueRange()
                            .setRange(sheetName + "!A" + rowIndex.getAndIncrement())
                            .setValues(List.of(rowValues));
                    data.add(rowRange);
                });
            }

            if (!data.isEmpty()) {
                BatchUpdateValuesRequest updateRequest = new BatchUpdateValuesRequest()
                        .setValueInputOption("RAW")
                        .setData(data);
                sheetsService.spreadsheets().values().batchUpdate(spreadsheetId, updateRequest).execute();
            }

            applyFormattingAndAdjustColumns(workbookData);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write data to Google Sheets", e);
        }
    }

    private Object getCellValue(RowData rowData, ColumnMeta columnMeta) {
        try {
            if (columnMeta.isTypeString()) {
                return rowData.readString(columnMeta);
            } else if (columnMeta.isTypeLong()) {
                return rowData.readLong(columnMeta);
            } else if (columnMeta.isTypeDouble()) {
                return rowData.readDouble(columnMeta);
            } else if (columnMeta.isTypeLocalDate()) {
                return rowData.readLocalDate(columnMeta).format(DateTimeFormatter.ISO_DATE);
            } else if (columnMeta.isTypeLocalDateTime()) {
                return rowData.readLocalDateTime(columnMeta).format(DateTimeFormatter.ISO_DATE_TIME);
            } else if (columnMeta.isReference()) {
                return rowData.readReference(columnMeta);
            } else {
                return "<unsupported>";
            }
        } catch (Exception e) {
            return "<conversion error>";
        }
    }

    private void applyFormattingAndAdjustColumns(WorkbookData workbookData) throws IOException {
        List<Request> requests = new ArrayList<>();

        for (SheetData sheetData : workbookData.sheetDatas()) {
            String sheetName = sheetData.sheetMeta().name();

            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            Sheet sheet = spreadsheet.getSheets().stream()
                    .filter(s -> s.getProperties().getTitle().equals(sheetName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Sheet not found: " + sheetName));
            Integer sheetId = sheet.getProperties().getSheetId();

            requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                    .setRange(new GridRange()
                            .setSheetId(sheetId)
                            .setStartRowIndex(0)
                            .setEndRowIndex(1))
                    .setCell(new CellData()
                            .setUserEnteredFormat(new CellFormat()
                                    .setTextFormat(new TextFormat().setBold(true))))
                    .setFields("userEnteredFormat.textFormat.bold")));

            for (ColumnMeta columnMeta : sheetData.sheetMeta().columnMetas()) {
                int startIndex = columnMeta.index();
                int endIndex = startIndex + 1;

                CellFormat cellFormat = new CellFormat();

                if (columnMeta.isTypeLong()) {
                    cellFormat.setNumberFormat(new NumberFormat().setType("NUMBER").setPattern("0"));
                } else if (columnMeta.isTypeDouble()) {
                    cellFormat.setNumberFormat(new NumberFormat().setType("NUMBER").setPattern("0.00"));
                } else if (columnMeta.isTypeLocalDate()) {
                    cellFormat.setNumberFormat(new NumberFormat().setType("DATE").setPattern("yyyy-MM-dd"));
                } else if (columnMeta.isTypeLocalDateTime()) {
                    cellFormat.setNumberFormat(new NumberFormat().setType("DATE_TIME").setPattern("yyyy-MM-dd HH:mm:ss"));
                } else if (columnMeta.isReference()) {
                    cellFormat.setTextFormat(new TextFormat().setItalic(true));
                }

                if (cellFormat.getNumberFormat() != null || cellFormat.getTextFormat() != null) {
                    requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(sheetId)
                                    .setStartColumnIndex(startIndex)
                                    .setEndColumnIndex(endIndex))
                            .setCell(new CellData().setUserEnteredFormat(cellFormat))
                            .setFields("userEnteredFormat.numberFormat,userEnteredFormat.textFormat")));
                }
            }

            requests.add(new Request().setAutoResizeDimensions(new AutoResizeDimensionsRequest()
                    .setDimensions(new DimensionRange()
                            .setSheetId(sheetId)
                            .setDimension("COLUMNS")
                            .setStartIndex(0)
                            .setEndIndex(sheetData.sheetMeta().columnMetas().size()))));

            requests.add(new Request().setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                    .setProperties(new SheetProperties()
                            .setSheetId(sheetId)
                            .setGridProperties(new GridProperties()
                                    .setFrozenRowCount(1)))
                    .setFields("gridProperties.frozenRowCount")));
        }

        if (!requests.isEmpty()) {
            BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute();
        }
    }
}
