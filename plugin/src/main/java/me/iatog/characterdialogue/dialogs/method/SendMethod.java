package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;

public class SendMethod extends DialogMethod<CharacterDialoguePlugin> {

    public SendMethod() {
        super("send");
    }

    @Override
    public void execute(MethodContext context) {
        context.getPlayer().sendMessage(context.getConfiguration().getArgument());
        context.next();
    }

}
