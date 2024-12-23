package me.iatog.characterdialogue.enums;

public enum Operator {
    LESS_THAN_EQUAL("<="),
    MORE_THAN_EQUAL(">="),
    NOT_EQUAL_TO("!="),
    LESS_THAN("<"),
    MORE_THAN(">"),
    EQUAL_TO("==");


    private final String text;

    Operator(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}