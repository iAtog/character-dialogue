package me.iatog.characterdialogue.dialogs;

import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.entity.Player;

public class ChoiceContext {
    private final String argument;
    private final DialogSession dialogSession;
    private final ChoiceSession choiceSession;

    public ChoiceContext(String argument, DialogSession dialogSession, ChoiceSession choiceSession) {
        this.argument = argument;
        this.dialogSession = dialogSession;
        this.choiceSession = choiceSession;
    }

    public ChoiceSession getChoiceSession() {
        return choiceSession;
    }

    public DialogSession getDialogSession() {
        return dialogSession;
    }

    public String getArgument() {
        return argument;
    }

    public Player getPlayer() {
        return dialogSession.getPlayer();
    }
}
