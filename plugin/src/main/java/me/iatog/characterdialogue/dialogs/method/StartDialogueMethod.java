package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;

public class StartDialogueMethod extends DialogMethod<CharacterDialoguePlugin> {

    public StartDialogueMethod(CharacterDialoguePlugin provider) {
        super("start_dialogue", provider);
        setDescription("Starts another dialog from the beginning");
    }

    @Override
    public void execute(MethodContext context) {
        String dialogueName = context.getConfiguration().getArgument().trim();
        Dialogue dialogue = getProvider().getApi().getDialogue(dialogueName);

        context.destroy();

        if (dialogue == null) {
            getProvider().getLogger().warning("The dialogue '" + dialogueName + "' was not found.");
            return;
        }


        if (dialogue.isFirstInteractionEnabled() && ! getProvider().getApi().wasReadedBy(context.getPlayer(), dialogue)) {
            dialogue.startFirstInteraction(context.getPlayer(), true, context.getNPC());
        } else {
            dialogue.start(context.getPlayer(), context.getNPC());
        }


    }
}
