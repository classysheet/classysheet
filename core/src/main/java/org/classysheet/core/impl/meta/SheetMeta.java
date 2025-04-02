package org.classysheet.core.impl.meta;

import org.classysheet.core.api.domain.Column;
import org.classysheet.core.api.domain.ColumnIgnore;
import org.classysheet.core.api.domain.IdColumn;
import org.classysheet.core.api.domain.Sheet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SheetMeta {

    private final WorkbookMeta workbookMeta;
    private final Field workbookField;
    private final Class<?> sheetClass;
    private final String name;
    private final List<ColumnMeta> columnMetas;
    private ColumnMeta idColumnMeta = null;
    private Constructor<?> sheetClassConstructor;

    public SheetMeta(WorkbookMeta workbookMeta, Field workbookField, Class<?> sheetClass, MetaBuilderContext builderContext) {
        this.workbookMeta = workbookMeta;
        this.workbookField = workbookField;
        workbookField.setAccessible(true);
        this.sheetClass = sheetClass;
        Sheet sheetAnnotation = sheetClass.getAnnotation(Sheet.class);
        if (sheetAnnotation == null) {
            throw new IllegalArgumentException("The sheet class (" + sheetClass.getName()
                    + ") is not annotated with @" + Sheet.class.getName() + ".");
        }
        String name = sheetAnnotation.name();
        if (name.isEmpty()) {
            name = workbookMeta.namingStrategy().sheetName(sheetClass);
        }
        this.name = name;

        Field[] fields = sheetClass.getDeclaredFields();
        this.columnMetas = new ArrayList<>(fields.length);
        List<Class<?>> parameterTypes = new ArrayList<>(fields.length);
        int index = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(ColumnIgnore.class)) {
                if (field.isAnnotationPresent(Column.class)) {
                    throw new IllegalArgumentException("The sheet class (" + sheetClass.getName()
                            + ") has a field (" + field + ") annotated with @" + ColumnIgnore.class.getName()
                            +  " and @" + Column.class.getName() + ".");
                }
                continue;
            }
            columnMetas.add(new ColumnMeta(this, index, field, builderContext));
            parameterTypes.add(field.getType());
            index++;
        }
        try {
            sheetClassConstructor = sheetClass.getDeclaredConstructor(parameterTypes.toArray(Class[]::new));
        } catch (NoSuchMethodException e) {
            sheetClassConstructor = null;
        }
    }

    public void setIdColumnMeta(ColumnMeta idColumnMeta) {
        if (this.idColumnMeta != null) {
            throw new IllegalArgumentException("There are two " + IdColumn.class.getSimpleName()
                    + " fields (" + this.idColumnMeta + ", " + idColumnMeta + ").");
        }
        this.idColumnMeta = idColumnMeta;
    }

    public void linkPotentialReferenceSheetMetas(List<SheetMeta> sheetMetas) {
        for (ColumnMeta columnMeta : columnMetas) {
            columnMeta.linkPotentialReferenceSheetMeta(sheetMetas);
        }
    }

    // ************************************************************************
    // At runtime
    // ************************************************************************

    public Object createRowObject(List<Object> fieldValues) {
        if (sheetClassConstructor == null) {
            throw new IllegalStateException("No valid constructor found for sheet class ("
                    + sheetClass.getName() + ").");
        }
        try {
            return sheetClassConstructor.newInstance(fieldValues.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Impossible state: could not call constructor (" + sheetClassConstructor
                    + ") with parameters (" + fieldValues + ")", e);
        }
    }

    public List<?> extractRows(Object workbook) {
        Object rows;
        try {
            rows = workbookField.get(workbook);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access the field (" + workbookField + ").", e);
        }
        return (List<?>) rows;
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Getters
    // ************************************************************************


    public WorkbookMeta workbookMeta() {
        return workbookMeta;
    }

    public Field workbookField() {
        return workbookField;
    }

    public Class<?> sheetClass() {
        return sheetClass;
    }

    public String name() {
        return name;
    }

    public List<ColumnMeta> columnMetas() {
        return columnMetas;
    }

    public ColumnMeta idColumnMeta() {
        return idColumnMeta;
    }


}
