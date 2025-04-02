package org.classysheet.core.api.domain.naming;

import java.lang.reflect.Field;

public class EnglishNamingStrategy implements NamingStrategy {

    @Override
    public String workbookName(Class<?> workbookClass) {
        return decamelCase(workbookClass.getSimpleName());
    }

    @Override
    public String sheetName(Class<?> sheetClass) {
        String name = decamelCase(sheetClass.getSimpleName());
        if (name.endsWith("y")) {
            name = name.substring(0, name.length() - 1) + "ie";
        }
        return name + "s";
    }

    @Override
    public String columnName(Field columnField) {
        String name = columnField.getName();
        return decamelCase(name);
    }

    @Override
    public String enumName(Enum<?> enumValue) {
        String name = enumValue.name();
        String withSpaces =  name.replaceAll("_", " ");
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1).toLowerCase();
    }

    private static String decamelCase(String name) {
        String withSpaces = name.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1).toLowerCase();
    }

}
