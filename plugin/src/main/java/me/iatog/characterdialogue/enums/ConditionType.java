package me.iatog.characterdialogue.enums;

import java.util.Locale;

public enum ConditionType {
    ALL,
    ONE;

    public static ConditionType to(String string) {
        return ConditionType.valueOf(string.toUpperCase(Locale.ROOT));
    }
}
