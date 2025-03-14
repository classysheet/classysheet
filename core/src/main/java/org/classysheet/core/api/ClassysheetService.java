package org.classysheet.core.api;

import org.classysheet.core.api.domain.Workbook;
import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.WorkbookMeta;
import org.classysheet.core.impl.provider.excel.ExcelSpreadsheetConnector;
import org.classysheet.core.impl.provider.google.GoogleSpreadsheetConnector;

import java.io.File;
import java.io.InputStream;

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

    @SuppressWarnings("unchecked")
    public Workbook_ readExcelFile(File file) {
        WorkbookData workbookData = excelConnector.readFile(workbookMeta, file);
        return (Workbook_) workbookData.workbook();
    }

    @SuppressWarnings("unchecked")
    public Workbook_ readExcelInputStream(InputStream inputStream) {
        WorkbookData workbookData = excelConnector.readInputStream(workbookMeta, inputStream);
        return (Workbook_) workbookData.workbook();
    }

    public File writeExcelTmpFileAndShow(Workbook_ workbook) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        return excelConnector.writeTmpFileAndShow(workbookData);
    }

}
