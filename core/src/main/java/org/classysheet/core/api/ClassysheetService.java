package org.classysheet.core.api;

import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.WorkbookMeta;
import org.classysheet.core.impl.provider.excel.ExcelSpreadsheetConnector;
import org.classysheet.core.impl.provider.google.GoogleSpreadsheetConnector;


public class ClassysheetService<Workbook_> {

    private final String spreadsheetId;
    protected final WorkbookMeta workbookMeta;
    protected final GoogleSpreadsheetConnector googleSpreadsheetConnector;
    protected final ExcelSpreadsheetConnector excelConnector = new ExcelSpreadsheetConnector();

    private ClassysheetService(WorkbookMeta workbookMeta, String spreadsheetId) {
        this.workbookMeta = workbookMeta;
        this.spreadsheetId = spreadsheetId;
        this.googleSpreadsheetConnector = new GoogleSpreadsheetConnector(spreadsheetId);
    }

    private ClassysheetService(WorkbookMeta workbookMeta) {
        this.workbookMeta = workbookMeta;
        this.spreadsheetId = null;
        this.googleSpreadsheetConnector = null;
    }

    public static <Workbook_> ClassysheetService<Workbook_> create(
            Class<Workbook_> workbookClass, String spreadsheetId) {
        return new ClassysheetService<>(new WorkbookMeta(workbookClass), spreadsheetId);
    }

    public static <Workbook_> ClassysheetService<Workbook_> create(
            Class<Workbook_> workbookClass) {
        return new ClassysheetService<>(new WorkbookMeta(workbookClass));
    }

    protected WorkbookMeta getWorkbookMeta() {
        return workbookMeta;
    }

    protected WorkbookData extractWorkbookData(Workbook_ workbook) {
        return new WorkbookData(workbookMeta, workbook);
    }

    public void writeWorkbookToGoogle(Workbook_ workbook) {
        if (spreadsheetId == null) {
            throw new IllegalStateException("spreadsheetId is null. Initialize ClassysheetService with a valid spreadsheetId for Google Sheets.");
        }
        WorkbookData workbookData = extractWorkbookData(workbook);
        assert googleSpreadsheetConnector != null;
        googleSpreadsheetConnector.writeWorkbook(workbookData);
    }

    public void writeWorkbookToExcel(Workbook_ workbook) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        excelConnector.writeWorkbook(workbookData);
    }
}
