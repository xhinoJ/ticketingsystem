package com.traffic.eventmanager;

import com.traffic.eventmanager.entity.enums.EventType;

public class EnumUtilFinder {

    private EnumUtilFinder() {
        throw new IllegalStateException("Utility class");
    }

    public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
        if (name == null) return (T) EventType.NONE;
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().compareToIgnoreCase(name) == 0) {
                return constant;
            }
        }
        return (T) EventType.OTHER;
    }

}
