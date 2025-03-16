package org.classysheet.core.impl.meta;

import org.classysheet.core.api.domain.Workbook;
import org.classysheet.core.api.domain.naming.NamingStrategy;
import org.classysheet.core.impl.data.SheetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkbookMeta {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbookMeta.class);

    private final Class<?> workbookClass;
    private final NamingStrategy namingStrategy;
    private final String name;
    private Constructor<?> workbookClassConstructor;
    private final List<SheetMeta> sheetMetas;

    public <Workbook_> WorkbookMeta(Class<Workbook_> workbookClass) {
        this.workbookClass = workbookClass;
        Workbook workbookAnnotation = workbookClass.getAnnotation(Workbook.class);
        if (workbookAnnotation == null) {
            throw new IllegalArgumentException("The workbook class (" + workbookClass.getName()
                    + ") is not annotated with @" + Workbook.class.getName() + ".");
        }
        Class<? extends NamingStrategy> namingStrategyClass = workbookAnnotation.namingStrategyClass();
        try {
            namingStrategy = namingStrategyClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("The namingStrategyClass (" + namingStrategyClass
                    + ") could not be instantiated.", e);
        }
        String name = workbookAnnotation.name();
        if (name.isEmpty()) {
            name = namingStrategy.workbookName(workbookClass);
        }
        this.name = name;
        Field[] fields = workbookClass.getDeclaredFields();
        this.sheetMetas = new ArrayList<>(fields.length);
        List<Class<?>> parameterTypes = new ArrayList<>(fields.length);
        for (Field field : fields) {
            processField(field);
            parameterTypes.add(field.getType());
        }
        for (SheetMeta sheetMeta : sheetMetas) {
            sheetMeta.linkPotentialReferenceSheetMetas(sheetMetas);
        }
        try {
            workbookClassConstructor = workbookClass.getDeclaredConstructor(parameterTypes.toArray(Class[]::new));
        } catch (NoSuchMethodException e) {
            workbookClassConstructor = null;
        }
    }

    private void processField(Field field) {
        Class<?> sheetClass = null;
        if (List.class.isAssignableFrom(field.getType())) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                    sheetClass = (Class<?>) typeArguments[0];
                }
            }
        }
        if (sheetClass != null) {
            sheetMetas.add(new SheetMeta(this, field, sheetClass));
        } else {
            LOG.trace("Ignoring field ({}) because it is not a List<T>.", field);
        }
    }

    // ************************************************************************
    // At runtime
    // ************************************************************************

    public Object createWorkbookObject(List<SheetData> sheetDatas) {
        if (workbookClassConstructor == null) {
            throw new IllegalStateException("No valid constructor found for workbook class ("
                    + workbookClass.getName() + ").");
        }
        Object[] parameters = sheetDatas.stream().map(SheetData::rows).toArray();
        try {
            return workbookClassConstructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Impossible state: could not call constructor (" + workbookClassConstructor
                    + ") with parameters (" + Arrays.toString(parameters) + ")", e);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public Class<?> workbookClass() {
        return workbookClass;
    }

    public NamingStrategy namingStrategy() {
        return namingStrategy;
    }

    public String name() {
        return name;
    }

    public List<SheetMeta> sheetMetas() {
        return sheetMetas;
    }

}
