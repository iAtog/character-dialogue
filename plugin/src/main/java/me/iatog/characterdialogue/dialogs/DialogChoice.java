package me.iatog.characterdialogue.dialogs;

import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;

public abstract class DialogChoice {

    private final String id;
    private final boolean requireArgument;

    public DialogChoice(String id, boolean requireArgument) {
        this.id = id;
        this.requireArgument = requireArgument;
    }

    public abstract void onSelect(ChoiceContext context);

    public String getId() {
        return id;
    }

    public boolean isArgumentRequired() {
        return requireArgument;
    }
}
