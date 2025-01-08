package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.ChoiceContext;
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
    public void onSelect(ChoiceContext context) {
        DialogSession dialogSession = context.getDialogSession();

        main.getApi().runDialogueExpression(
              context.getPlayer(),
              context.getArgument(),
              dialogSession.getDisplayName(),
              SingleUseConsumer.create((r) -> {
                  dialogSession.startNext();
              }),
              dialogSession,
              dialogSession.getNPC()
        );
    }

}
