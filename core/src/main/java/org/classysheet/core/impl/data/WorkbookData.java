package org.classysheet.core.impl.data;


import org.classysheet.core.impl.meta.SheetMeta;
import org.classysheet.core.impl.meta.WorkbookMeta;

import java.util.ArrayList;
import java.util.List;

public class WorkbookData {

    private final WorkbookMeta workbookMeta;
    private final Object workbookObject;
    private List<SheetData> sheetDatas;

    public WorkbookData(WorkbookMeta workbookMeta, List<SheetData> sheetDatas) {
        this.workbookMeta = workbookMeta;
        workbookObject = workbookMeta.createWorkbookObject(sheetDatas);
        this.sheetDatas = sheetDatas;
    }

    public WorkbookData(WorkbookMeta workbookMeta, Object workbookObject) {
        this.workbookMeta = workbookMeta;
        this.workbookObject = workbookObject;
        if (workbookObject == null || !workbookMeta.workbookClass().isAssignableFrom(workbookObject.getClass())) {
            throw new IllegalArgumentException("The workbook class "
                    + (workbookObject == null ? "null" : workbookObject.getClass()) + " is not an instance of "
                    + workbookMeta.workbookClass() + ".");
        }
        List<SheetMeta> sheetMetas = workbookMeta.sheetMetas();
        sheetDatas = new ArrayList<>(sheetMetas.size());
        for (SheetMeta sheetMeta : sheetMetas) {
            List<?> rows = sheetMeta.extractRows(workbookObject);
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

    public Object workbook() {
        return workbookObject;
    }

    public List<SheetData> sheetDatas() {
        return sheetDatas;
    }

}
