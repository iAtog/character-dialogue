package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;

public class MessageChoice extends DialogChoice {

    public MessageChoice() {
        super("message", true);
    }

    @Override
    public void onSelect(String argument, DialogSession dialogSession, ChoiceSession choiceSession) {
        dialogSession.getPlayer().sendMessage(Placeholders.translate(dialogSession.getPlayer(),
              argument.replace("%npc_name%", dialogSession.getDisplayName())));

        dialogSession.startNext();
    }

}
