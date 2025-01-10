package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.CharacterDialogueAPI;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.dialogs.ChoiceContext;
import me.iatog.characterdialogue.dialogs.DialogChoice;

public class StartDialogChoice extends DialogChoice {

    private final CharacterDialoguePlugin main;

    public StartDialogChoice(CharacterDialoguePlugin main) {
        super("start_dialogue", true);
        this.main = main;
    }

    @Override
    public void onSelect(ChoiceContext context) {
        CharacterDialogueAPI api = main.getApi();
        Dialogue dialogue = api.getDialogue(context.getArgument());
        context.getDialogSession().destroy();

        if (dialogue != null) {
            api.runDialogue(context.getPlayer(), dialogue,
                  context.getDialogSession().isOnDebugMode(), context.getDialogSession().getNPC());
        } else {
            main.getLogger().severe("The dialogue \"" + context.getArgument() + "\" doesn't exists");
        }
    }

}
