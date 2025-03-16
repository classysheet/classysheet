package org.classysheet.core.impl.provider.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.classysheet.core.impl.data.RowData;
import org.classysheet.core.impl.data.SheetData;
import org.classysheet.core.impl.data.WorkbookData;
import org.classysheet.core.impl.meta.ColumnMeta;
import org.classysheet.core.impl.meta.SheetMeta;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.classysheet.core.impl.meta.WorkbookMeta;

import java.awt.Desktop;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelSpreadsheetConnector {

    public WorkbookData readInputStream(WorkbookMeta workbookMeta, InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            return readWorkbook(workbookMeta, workbook);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file from input stream.", e);
        }
    }

    public WorkbookData readFile(WorkbookMeta workbookMeta, File file) {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return readWorkbook(workbookMeta, workbook);
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException("Failed to read Excel file from file (" + file + ").", e);
        }
    }

    public WorkbookData readWorkbook(WorkbookMeta workbookMeta, Workbook workbook) {
        List<SheetData> sheetDatas = new ArrayList<>(workbookMeta.sheetMetas().size());
        for (SheetMeta sheetMeta : workbookMeta.sheetMetas()) {
            Sheet sheet = workbook.getSheet(sheetMeta.name());
            if (sheet == null) {
                throw new IllegalArgumentException("The Excel workbook does not contain a sheet with the name ("
                        + sheetMeta.name() + ").");
            }
            List<Object> rows = new ArrayList<>(sheet.getLastRowNum());
            Row headerRow = sheet.getRow(0);
            for (ColumnMeta columnMeta : sheetMeta.columnMetas()) {
                Cell headerCell = headerRow.getCell(columnMeta.index());
                if (headerCell == null) {
                    throw newIllegalFormatException(sheet, columnMeta.index(), 0,
                            "The column (" + columnMeta.name() + ") is missing.");
                }
                String name = headerCell.getStringCellValue();
                if (!Objects.equals(name, columnMeta.name())) {
                    throw newIllegalFormatException(headerCell, "The actual column name (" + name + ")"
                            + ") is not the expected column name (" + columnMeta.name() + ").");
                }
            }
            int columnCount = headerRow.getLastCellNum();
            if (columnCount != sheetMeta.columnMetas().size()) {
                throw newIllegalFormatException(sheet, sheetMeta.columnMetas().size(), 0,
                        "The actual column count (" + columnCount
                        + ") is more than the expected column count (" + sheetMeta.columnMetas().size() + ").");
            }
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                List<Object> values = new ArrayList<>(sheetMeta.columnMetas().size());
                for (ColumnMeta columnMeta : sheetMeta.columnMetas()) {
                    Cell cell = row.getCell(columnMeta.index());
                    Object value;
                    if (cell == null || cell.getCellType() == CellType.BLANK) {
                        value = null;
                    } else if (columnMeta.isTypeString()) {
                        if (cell.getCellType() != CellType.STRING) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.STRING + ").");
                        }
                        value = cell.getStringCellValue();
                    } else if (columnMeta.isTypeLong()) {
                        if (cell.getCellType() != CellType.NUMERIC) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.NUMERIC + ").");
                        }
                        value = (long) cell.getNumericCellValue();
                    } else if (columnMeta.isTypeDouble()) {
                        if (cell.getCellType() != CellType.NUMERIC) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.NUMERIC + ").");
                        }
                        value = cell.getNumericCellValue();
                    } else if (columnMeta.isTypeLocalDate()) {
                        if (cell.getCellType() != CellType.NUMERIC) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.NUMERIC + ").");
                        }
                        value = cell.getLocalDateTimeCellValue().toLocalDate();
                    } else if (columnMeta.isTypeLocalDateTime()) {
                        if (cell.getCellType() != CellType.NUMERIC) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.NUMERIC + ").");
                        }
                        value = cell.getLocalDateTimeCellValue();
                    } else if (columnMeta.isTypeLocalTime()) {
                        if (cell.getCellType() != CellType.STRING) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.STRING + ").");
                        }
                        try {
                            value = DateTimeFormatter.ISO_TIME.parse(cell.getStringCellValue());
                        } catch (DateTimeParseException e) {
                            throw newIllegalFormatException(cell, "The ceel value (" + cell.getStringCellValue()
                                    + ") is not a valid time.", e);
                        }
                    } else if (columnMeta.isEnum()) {
                        if (cell.getCellType() != CellType.STRING) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.STRING + ").");
                        }
                        String stringValue = cell.getStringCellValue();
                        // TODO extract to non-excel specific code
                        value = stringValue == null ? null
                                : Enum.valueOf((Class<? extends Enum>)columnMeta.type(), stringValue);
                    } else if (columnMeta.isReference()) {
                        if (cell.getCellType() != CellType.STRING) {
                            throw newIllegalFormatException(cell, "The actual cell type (" + cell.getCellType() + ")"
                                    + ") is not the expected cell type (" + CellType.STRING + ").");
                        }
                        value = cell.getStringCellValue();
                        value = null; // TODO FIXME
                    } else {
                        throw newIllegalFormatException(cell, "Unsupported type.");
                    }
                    values.add(value);
                }
                rows.add(sheetMeta.createRowObject(values));
            }
            SheetData sheetData = SheetData.ofRowDatas(sheetMeta, rows);
            sheetDatas.add(sheetData);
        }
        return new WorkbookData(workbookMeta, sheetDatas);
    }

    protected IllegalArgumentException newIllegalFormatException(Cell cell, String message) {
        return newIllegalFormatException(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex(), message);
    }

    protected IllegalArgumentException newIllegalFormatException(Sheet sheet, int columnIndex, int rowIndex, String message) {
        return newIllegalFormatException(sheet, columnIndex, rowIndex, message, null);
    }

    protected IllegalArgumentException newIllegalFormatException(Cell cell, String message, Exception e) {
        return newIllegalFormatException(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex(), message, e);
    }

    protected IllegalArgumentException newIllegalFormatException(Sheet sheet, int columnIndex, int rowIndex, String message, Exception e) {
        return new IllegalArgumentException("Sheet (" + sheet.getSheetName()
                + "), cell (" + getCellAddress(columnIndex, rowIndex) + "): " + message, e);
    }

    protected String getCellAddress(int columnIndex, int rowIndex) {
        StringBuilder columnLetter = new StringBuilder();
        while (columnIndex >= 0) {
            columnLetter.insert(0, (char) ('A' + (columnIndex % 26)));
            columnIndex = (columnIndex / 26) - 1;
        }
        return columnLetter.toString() + (rowIndex + 1);
    }

    public void writeExcelOutputStream(WorkbookData workbookData, OutputStream outputStream) {
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
            CellStyle timeCellStyle = workbook.createCellStyle();
            timeCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("HH:mm:ss"));
            CellStyle enumCellStyle = workbook.createCellStyle(); // TODO enum style per enum type
            Font referenceFont = workbook.createFont();
            CellStyle referenceCellStyle = workbook.createCellStyle();
            referenceFont.setItalic(true);
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
                    } else if (columnMeta.isTypeLocalTime()) {
                        columnCellStyle = timeCellStyle;
                    } else if (columnMeta.isEnum()) {
                        columnCellStyle = enumCellStyle;
                    } else if (columnMeta.isReference()) {
                        columnCellStyle = referenceCellStyle;
                    } else {
                        throw new IllegalArgumentException(columnMeta.buildWriteContext()
                                + "The type (" + columnMeta.type() + ") is not supported.");
                    }
                    sheet.setDefaultColumnStyle(columnMeta.index(), columnCellStyle);
                }

                AtomicInteger rowIndex = new AtomicInteger(1);
                sheetData.streamRowDatas().forEach(rowData -> {
                    Row row = sheet.createRow(rowIndex.getAndIncrement());
                    for (ColumnMeta columnMeta : sheetMeta.columnMetas()) {
                        Cell cell = row.createCell(columnMeta.index());
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
                        } else if (columnMeta.isTypeLocalTime()) {
                            cell.setCellValue(rowData.readLocalTime(columnMeta));
                        } else if (columnMeta.isEnum()) {
                            cell.setCellValue(rowData.readEnum(columnMeta));
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
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Excel file for workbook name (" +
                    workbookData.workbookMeta().name() + ").", e);
        }
    }

    public File writeTmpFileAndShow(WorkbookData workbookData) {
        try {
            File file = File.createTempFile(workbookData.workbookMeta().name(), ".xlsx");
            writeExcelOutputStream(workbookData, new FileOutputStream(file));
            Desktop.getDesktop().open(file);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create or open the Excel temp file.", e);
        }
    }

}
