package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;

public class ContinueChoice extends DialogChoice {

    public ContinueChoice() {
        super("continue", false);
    }

    @Override
    public void onSelect(String argument, DialogSession dialogSession, ChoiceSession choiceSession) {
        if (! argument.isEmpty()) {
            dialogSession.getPlayer().sendMessage(Placeholders.translate(dialogSession.getPlayer(),
                  argument.replace("%npc_name%", dialogSession.getDisplayName())));
        }

        dialogSession.startNext();
    }
}
