package me.iatog.characterdialogue.dialogs;

public class ChoiceInfo {

    private final String id;
    private final String type;
    private final String message;
    private final String argument;

    public ChoiceInfo(String id, String type, String message, String argument) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.argument = argument;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getArgument() {
        return argument;
    }
}
