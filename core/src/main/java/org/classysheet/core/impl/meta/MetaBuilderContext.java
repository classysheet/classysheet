package org.classysheet.core.impl.meta;

import org.classysheet.core.api.domain.naming.NamingStrategy;

import java.util.HashMap;
import java.util.Map;

public class MetaBuilderContext {

    private Map<Class<? extends Enum<?>>, EnumMeta> enumMetaMap = new HashMap<>();

    public EnumMeta getEnumMeta(Class<? extends Enum<?>> type, NamingStrategy namingStrategy) {
        return enumMetaMap.computeIfAbsent(type, type_ -> new EnumMeta(type_, namingStrategy));
    }

}
