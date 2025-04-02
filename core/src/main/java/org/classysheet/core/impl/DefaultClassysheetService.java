package org.classysheet.core.impl;

import org.classysheet.core.api.ClassysheetService;
import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.WorkbookMeta;
import org.classysheet.core.impl.provider.excel.ExcelSpreadsheetConnector;
import org.classysheet.core.impl.provider.google.GoogleSpreadsheetConnector;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class DefaultClassysheetService<Workbook_>implements ClassysheetService<Workbook_> {

    protected final WorkbookMeta workbookMeta;
    protected final GoogleSpreadsheetConnector googleSpreadsheetConnector = new GoogleSpreadsheetConnector();
    protected final ExcelSpreadsheetConnector excelConnector = new ExcelSpreadsheetConnector();

    public DefaultClassysheetService(Class<Workbook_> workbookClass) {
        this.workbookMeta = new WorkbookMeta(workbookClass);
    }

    public WorkbookData extractWorkbookData(Workbook_ workbook) {
        return new WorkbookData(workbookMeta, workbook);
    }

    @Override
    public void writeWorkbookToGoogle(Workbook_ workbook) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        googleSpreadsheetConnector.writeWorkbook(workbookData);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Workbook_ readExcelFile(File file) {
        WorkbookData workbookData = excelConnector.readFile(workbookMeta, file);
        return (Workbook_) workbookData.workbook();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Workbook_ readExcelInputStream(InputStream inputStream) {
        WorkbookData workbookData = excelConnector.readInputStream(workbookMeta, inputStream);
        return (Workbook_) workbookData.workbook();
    }

    @Override
    public void writeExcelOutputStream(Workbook_ workbook, OutputStream outputStream) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        excelConnector.writeExcelOutputStream(workbookData, outputStream);
    }

    @Override
    public File writeExcelTmpFileAndShow(Workbook_ workbook) {
        WorkbookData workbookData = extractWorkbookData(workbook);
        return excelConnector.writeTmpFileAndShow(workbookData);
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public WorkbookMeta getWorkbookMeta() {
        return workbookMeta;
    }

}
