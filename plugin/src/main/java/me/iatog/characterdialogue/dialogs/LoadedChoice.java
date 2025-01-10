package me.iatog.characterdialogue.dialogs;

import java.util.List;

public class LoadedChoice {

    private final List<ChoiceInfo> choices;
    private final String message;

    public LoadedChoice(List<ChoiceInfo> choices, String message) {
        this.choices = choices;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public List<ChoiceInfo> getChoices() {
        return choices;
    }
}
