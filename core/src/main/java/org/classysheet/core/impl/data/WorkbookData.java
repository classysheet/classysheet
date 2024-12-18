package org.classysheet.core.impl.data;


import org.classysheet.core.impl.meta.SheetMeta;
import org.classysheet.core.impl.meta.WorkbookMeta;

import java.util.ArrayList;
import java.util.List;

public class WorkbookData {

    private final WorkbookMeta workbookMeta;
    private final Object workbook;
    private List<SheetData> sheetDatas;

    public WorkbookData(WorkbookMeta workbookMeta, Object workbook) {
        this.workbookMeta = workbookMeta;
        this.workbook = workbook;
        if (workbook == null || !workbookMeta.workbookClass().isAssignableFrom(workbook.getClass())) {
            throw new IllegalArgumentException("The workbook class "
                    + (workbook == null ? "null" : workbook.getClass()) + " is not an instance of "
                    + workbookMeta.workbookClass() + ".");
        }
        List<SheetMeta> sheetMetas = workbookMeta.sheetMetas();
        sheetDatas = new ArrayList<>(sheetMetas.size());
        for (SheetMeta sheetMeta : sheetMetas) {
            List<?> rows = sheetMeta.extractRows(workbook);
            sheetDatas.add(new SheetData(sheetMeta, rows));
        }
    }

    @Override
    public String toString() {
        return workbookMeta.toString() + " workbook";
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public WorkbookMeta workbookMeta() {
        return workbookMeta;
    }

    public List<SheetData> sheetDatas() {
        return sheetDatas;
    }

}
