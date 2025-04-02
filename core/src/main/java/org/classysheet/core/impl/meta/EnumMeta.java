package org.classysheet.core.impl.meta;

import org.classysheet.core.api.domain.CellValue;
import org.classysheet.core.api.domain.Column;
import org.classysheet.core.api.domain.IdColumn;
import org.classysheet.core.api.domain.naming.NamingStrategy;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumMeta {

    private final Class<? extends Enum<?>> type;
    private final Map<Enum<?>, String> enumToStringMap;
    private final Map<String, Enum<?>> stringToEnumMap;

    public EnumMeta(Class<? extends Enum<?>> type, NamingStrategy namingStrategy) {
        this.type = type;
        if (!type.isEnum()) {
            throw new IllegalStateException("Impossible state: type (" + type + ") is not an enum.");
        }
        Enum<?>[] enumConstants = type.getEnumConstants();
        enumToStringMap = new HashMap<>(enumConstants.length);
        stringToEnumMap = new HashMap<>(enumConstants.length);
        for (Enum<?> enumConstant : enumConstants) {
            CellValue cellValueAnnotation;
            try {
                cellValueAnnotation = type.getField(enumConstant.name()).getAnnotation(CellValue.class);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("Impossible state: the enum (" + type
                        + ") does not have a field for the enum constant (" + enumConstant + ").", e);
            }
            String name = (cellValueAnnotation == null) ? "" : cellValueAnnotation.name();
            if (name.isEmpty()) {
                name = namingStrategy.enumName(enumConstant);
            }
            if (enumToStringMap.put(enumConstant, name) != null) {
                throw new IllegalStateException("Impossible state: enum (" + enumConstant + ") is added twice.");
            }
            if (stringToEnumMap.put(name, enumConstant) != null) {
                throw new IllegalArgumentException("The enum constant (" + enumConstant
                        + ") has the same name (" + name + ") as another enum constant.");
            }
        }
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public Class<?> type() {
        return type;
    }

    public String convertToString(Enum<?> enumConstant) {
        return enumToStringMap.computeIfAbsent(enumConstant, enumConstant_ -> {
            throw new IllegalStateException("Impossible state: the enum (" + type
                    + ") has no enum constant (" + enumConstant_ + ").");
        });
    }

    public Enum<?> convertToEnum(String name) {
        return stringToEnumMap.computeIfAbsent(name, name_ -> {
            throw new IllegalArgumentException("The enum (" + type
                    + ") has no enum constant with name (" + name_ + ").");
        });
    }

}
