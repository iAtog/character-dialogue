package me.iatog.characterdialogue.dialogs.method.conditional;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.session.DialogSession;

public class ConditionData {

    private final DialogSession session;
    private final CharacterDialoguePlugin main;
    private final String expression;

    public ConditionData(DialogSession session, CharacterDialoguePlugin main, String expression) {
        this.session = session;
        this.main = main;
        this.expression = expression;
    }

    public DialogSession getSession() {
        return session;
    }

    public String getExpression() {
        return expression;
    }

    public CharacterDialoguePlugin getMain() {
        return main;
    }
}