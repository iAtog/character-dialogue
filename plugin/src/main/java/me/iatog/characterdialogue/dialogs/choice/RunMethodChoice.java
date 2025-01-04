package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;

public class RunMethodChoice extends DialogChoice {

    private final CharacterDialoguePlugin main;

    public RunMethodChoice(CharacterDialoguePlugin main) {
        super("run_method", true);
        this.main = main;
    }

    @Override
    public void onSelect(String argument, DialogSession dialogSession, ChoiceSession choiceSession) {
        main.getApi().runDialogueExpression(
              dialogSession.getPlayer(),
              argument,
              dialogSession.getDisplayName(),
              SingleUseConsumer.create((r) -> {
                  dialogSession.startNext();
              }),
              dialogSession,
              dialogSession.getNPC()
        );
    }

}
