package org.classysheet.core.impl.data;

import org.classysheet.core.impl.meta.ColumnMeta;
import org.classysheet.core.impl.meta.SheetMeta;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RowData {

    private final SheetMeta sheetMeta;
    private final int index;
    private final Object row;

    public RowData(SheetMeta sheetMeta, int index, Object row) {
        this.sheetMeta = sheetMeta;
        this.index = index;
        this.row = row;
        if (row == null) {
            // TODO better error message that says which class etc
            throw new IllegalArgumentException("The row cannot be null.");
        }
    }

    public List<Object> readValuesAsString() {
        return sheetMeta.columnMetas().stream().map(columnMeta -> {
            if (columnMeta.isTypeString()) {
                return readString(columnMeta);
            } else if (columnMeta.isTypeLocalDate()) {
                return readLocalDate(columnMeta).toString();
            } else if (columnMeta.isTypeLocalDateTime()) {
                return readLocalDateTime(columnMeta).toString();
            } else {
                return (Object) "<unsupported type>";
            }
        }).toList();
    }

    public String readString(ColumnMeta columnMeta) {
        return (String) readObject(columnMeta);
    }

    public long readLong(ColumnMeta columnMeta) {
        if (columnMeta.field().getType().isPrimitive()) {
            try {
                return columnMeta.field().getLong(row);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot read field (" + columnMeta.field()
                        + ") for row object (" + row + ").", e);
            }
        } else {
            return ((Number) readObject(columnMeta)).longValue();
        }
    }

    public double readDouble(ColumnMeta columnMeta) {
        if (columnMeta.field().getType().isPrimitive()) {
            try {
                return columnMeta.field().getDouble(row);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot read field (" + columnMeta.field()
                        + ") for row object (" + row + ").", e);
            }
        } else {
            return ((Number) readObject(columnMeta)).doubleValue();
        }
    }

    public LocalDate readLocalDate(ColumnMeta columnMeta) {
        return (LocalDate) readObject(columnMeta);
    }

    public LocalDateTime readLocalDateTime(ColumnMeta columnMeta) {
        return (LocalDateTime) readObject(columnMeta);
    }

    public String readReference(ColumnMeta columnMeta) {
        Object object = readObject(columnMeta);
        if (object == null) {
            return "";
        }
        Field idColumnField = columnMeta.referenceSheetMeta().idColumnMeta().field();
        try {
            return (String) idColumnField.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot read ID field (" + columnMeta.field()
                    + ") for reference object (" + object + ").", e);
        }
    }

    protected Object readObject(ColumnMeta columnMeta) {
        try {
            return columnMeta.field().get(row);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot read field (" + columnMeta.field()
                    + ") for row object (" + row + ").", e);
        }
    }

    @Override
    public String toString() {
        return sheetMeta.toString() + " row " + index;
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public SheetMeta sheetMeta() {
        return sheetMeta;
    }

    public int index() {
        return index;
    }

    protected Object row() {
        return row;
    }

}
