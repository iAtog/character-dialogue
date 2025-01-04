package me.iatog.characterdialogue.enums;

public enum ConditionType {
    ALL,
    ONE;

    public static ConditionType to(String string) {
        return ConditionType.valueOf(string.toUpperCase());
    }
}
