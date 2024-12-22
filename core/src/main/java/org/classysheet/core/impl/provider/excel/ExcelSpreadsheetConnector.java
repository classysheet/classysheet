package org.classysheet.core.impl.provider.excel;

import org.classysheet.core.impl.data.SheetData;
import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.ColumnMeta;
import org.classysheet.core.impl.meta.SheetMeta;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelSpreadsheetConnector {

    public File writeTmpFileAndShow(WorkbookData workbookData) {
        File file;
        try (Workbook workbook = new XSSFWorkbook()) {
            CreationHelper creationHelper = workbook.getCreationHelper();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            CellStyle stringCellStyle = workbook.createCellStyle();
            CellStyle longCellStyle = workbook.createCellStyle();
            longCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"));
            CellStyle doubleCellStyle = workbook.createCellStyle();
            doubleCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0.00"));
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
            CellStyle dateTimeCellStyle = workbook.createCellStyle();
            dateTimeCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
            Font referenceFont = workbook.createFont();
            referenceFont.setItalic(true);
            CellStyle referenceCellStyle = workbook.createCellStyle();
            referenceCellStyle.setFont(referenceFont);

            for (SheetData sheetData : workbookData.sheetDatas()) {
                SheetMeta sheetMeta = sheetData.sheetMeta();
                Sheet sheet = workbook.createSheet(sheetMeta.name());
                Row headerRow = sheet.createRow(0);
                for (ColumnMeta columnMeta : sheetMeta.columnMetas()) {
                    Cell cell = headerRow.createCell(columnMeta.index());
                    cell.setCellStyle(headerCellStyle);
                    cell.setCellValue(columnMeta.name());
                    CellStyle columnCellStyle;
                    if (columnMeta.isTypeString()) {
                        columnCellStyle = stringCellStyle;
                    } else if (columnMeta.isTypeLong()) {
                        columnCellStyle = longCellStyle;
                    } else if (columnMeta.isTypeDouble()) {
                        columnCellStyle = doubleCellStyle;
                    } else if (columnMeta.isTypeLocalDate()) {
                        columnCellStyle = dateCellStyle;
                    } else if (columnMeta.isTypeLocalDateTime()) {
                        columnCellStyle = dateTimeCellStyle;
                    } else if (columnMeta.isReference()) {
                        columnCellStyle = referenceCellStyle;
                    } else {
                        columnCellStyle = null;
                    }
                    sheet.setDefaultColumnStyle(columnMeta.index(), columnCellStyle);
                }

                AtomicInteger rowIndex = new AtomicInteger(1);
                sheetData.streamRows().forEach(rowData -> {
                    Row row = sheet.createRow(rowIndex.getAndIncrement());
                    int columnIndex = 0;
                    for (ColumnMeta columnMeta : sheetMeta.columnMetas()) {
                        Cell cell = row.createCell(columnIndex++);
                        if (columnMeta.isTypeString()) {
                            cell.setCellValue(rowData.readString(columnMeta));
                        } else if (columnMeta.isTypeLong()) {
                            cell.setCellValue(rowData.readLong(columnMeta));
                        } else if (columnMeta.isTypeDouble()) {
                            cell.setCellValue(rowData.readDouble(columnMeta));
                        } else if (columnMeta.isTypeLocalDate()) {
                            cell.setCellValue(rowData.readLocalDate(columnMeta));
                        } else if (columnMeta.isTypeLocalDateTime()) {
                            cell.setCellValue(rowData.readLocalDateTime(columnMeta));
                        } else if (columnMeta.isReference()) {
                            cell.setCellValue(rowData.readReference(columnMeta));
                        } else {
                            cell.setCellValue("<unsupported>");
                        }
                    }
                });
                for (ColumnMeta columnMeta : sheetMeta.columnMetas()) {
                    sheet.autoSizeColumn(columnMeta.index());
                }
                sheet.createFreezePane(0, 1);
            }

            file = File.createTempFile(workbookData.workbookMeta().name(), ".xlsx");
            workbook.write(new FileOutputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Excel file for workbook name (" +
                    workbookData.workbookMeta().name() + ").", e);
        }

        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open the Excel file (" + file + ").", e);
        }
        return file;
    }

}
