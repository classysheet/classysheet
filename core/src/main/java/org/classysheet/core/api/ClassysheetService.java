package org.classysheet.core.api;

import org.classysheet.core.api.domain.Workbook;
import org.classysheet.core.impl.data.SheetData;
import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.SheetMeta;
import org.classysheet.core.impl.meta.WorkbookMeta;
import org.classysheet.core.impl.provider.google.SheetsServiceUtil;
import org.classysheet.core.impl.provider.excel.ExcelSpreadsheetConnector;
import org.classysheet.core.impl.provider.google.GoogleSpreadsheetConnector;
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

    /**
     * Create a new service from domain annotations.
     * Typically called once at bootstrap or build time.
     *
     * @param workbookClass never null
     * @return never null
     * @param <Workbook_> The class with a {@link Workbook} annotation.
     */
    public static <Workbook_> ClassysheetService<Workbook_> create(
            Class<Workbook_> workbookClass) {
        return new ClassysheetService<>(new WorkbookMeta(workbookClass));
    }

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
        googleSpreadsheetConnector.writeWorkbook(workbookData);
    }

    public void writeWorkbookToExcel(Workbook_ workbook) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        excelConnector.writeWorkbook(workbookData);
    }

}
