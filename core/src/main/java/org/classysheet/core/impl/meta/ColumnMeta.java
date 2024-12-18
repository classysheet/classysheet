package org.classysheet.core.impl.meta;

import org.classysheet.core.api.domain.Column;
import org.classysheet.core.api.domain.IdColumn;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ColumnMeta {

    private final SheetMeta sheetMeta;
    private final int index;
    private final String name;
    private final Field field;
    private final Class<?> type;
    private final boolean isIdColumn;
    private SheetMeta referenceSheetMeta = null;

    public ColumnMeta(SheetMeta sheetMeta, int index, Field field) {
        this.sheetMeta = sheetMeta;
        this.index = index;
        this.field = field;
        Column columnAnnotation = field.getAnnotation(Column.class);
        String name = (columnAnnotation == null) ? "" : columnAnnotation.name();
        if (name.isEmpty()) {
            name = sheetMeta.workbookMeta().namingStrategy().columnName(field);
        }
        this.name = name;
        this.type = field.getType();
        field.setAccessible(true);
        IdColumn idColumnAnnotation = field.getAnnotation(IdColumn.class);
        isIdColumn = (idColumnAnnotation != null);
        if (isIdColumn) {
            if (!type.isAssignableFrom(String.class)) {
                throw new IllegalArgumentException("The type of the  " + IdColumn.class.getSimpleName()
                        + " field (" + field + ") is not a String.");
            }
            sheetMeta.setIdColumnMeta(this);
        }
    }

    public void linkPotentialReferenceSheetMeta(List<SheetMeta> sheetMetas) {
        for (SheetMeta sheetMeta : sheetMetas) {
            if (sheetMeta.sheetClass().isAssignableFrom(type)) {
                if (referenceSheetMeta != null) {
                    throw new IllegalStateException("The field (" + field + ") can reference two SheetMetas ("
                            + referenceSheetMeta + ", " + sheetMeta + ").\n"
                            + "Polymorphism is not yet supported.");
                }
                if (sheetMeta.idColumnMeta() == null) {
                    throw new IllegalStateException("The sheet class (" + sheetMeta.sheetClass()
                            + ") has no @" + IdColumn.class.getSimpleName() + " annotated field,"
                            + " but the field (" + field + ") references it, so it needs one.");
                }
                referenceSheetMeta = sheetMeta;
            }
        }
    }

    public boolean isTypeString() {
        return type.isAssignableFrom(String.class);
    }

    public boolean isTypeLong() {
        return type == Integer.TYPE || type.isAssignableFrom(Integer.class)
                || type == Long.TYPE || type.isAssignableFrom(Long.class);
    }

    public boolean isTypeDouble() {
        return type == Double.TYPE || type.isAssignableFrom(Double.class)
                || type == Float.TYPE || type.isAssignableFrom(Float.class);
    }

    public boolean isTypeLocalDate() {
        return type.isAssignableFrom(LocalDate.class);
    }

    public boolean isTypeLocalDateTime() {
        return type.isAssignableFrom(LocalDateTime.class);
    }

    public boolean isReference() {
        return referenceSheetMeta != null;
    }

    @Override
    public String toString() {
        return sheetMeta.toString() + "!" + name;
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public int index() {
        return index;
    }

    public String name() {
        return name;
    }

    public Field field() {
        return field;
    }

    public Class<?> type() {
        return type;
    }

    public boolean isIdColumn() {
        return isIdColumn;
    }

    public SheetMeta referenceSheetMeta() {
        return referenceSheetMeta;
    }

}
