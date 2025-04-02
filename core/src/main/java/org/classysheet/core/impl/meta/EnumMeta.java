package org.classysheet.core.impl.meta;

import org.classysheet.core.api.domain.CellValue;
import org.classysheet.core.api.domain.naming.NamingStrategy;

import java.util.HashMap;
import java.util.Map;

public class EnumMeta {

    private final Class<? extends Enum<?>> type;
    private final Map<Enum<?>, String> enumToNameMap;
    private final Map<String, Enum<?>> nameToEnumMap;

    public EnumMeta(Class<? extends Enum<?>> type, NamingStrategy namingStrategy) {
        this.type = type;
        if (!type.isEnum()) {
            throw new IllegalStateException("Impossible state: type (" + type + ") is not an enum.");
        }
        Enum<?>[] enumConstants = type.getEnumConstants();
        enumToNameMap = new HashMap<>(enumConstants.length);
        nameToEnumMap = new HashMap<>(enumConstants.length);
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
            if (enumToNameMap.put(enumConstant, name) != null) {
                throw new IllegalStateException("Impossible state: enum (" + enumConstant + ") is added twice.");
            }
            if (nameToEnumMap.put(name, enumConstant) != null) {
                throw new IllegalArgumentException("The enum constant (" + enumConstant
                        + ") has the same name (" + name + ") as another enum constant.");
            }
        }
    }

    public String convertToString(Enum<?> enumConstant) {
        return enumToNameMap.computeIfAbsent(enumConstant, enumConstant_ -> {
            throw new IllegalStateException("Impossible state: the enum (" + type
                    + ") has no enum constant (" + enumConstant_ + ").");
        });
    }

    public Enum<?> convertToEnum(String name) {
        return nameToEnumMap.computeIfAbsent(name, name_ -> {
            throw new IllegalArgumentException("The enum (" + type
                    + ") has no enum constant with name (" + name_ + ").");
        });
    }

    public String[] getNames() {
        return nameToEnumMap.keySet().toArray(String[]::new);
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public Class<?> type() {
        return type;
    }

}
