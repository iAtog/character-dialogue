package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.util.AdventureUtil;

public class SendMethod extends DialogMethod<CharacterDialoguePlugin> {

    public SendMethod() {
        super("send");

        setDescription("Sends a message to the player");
    }

    @Override
    public void execute(MethodContext context) {
        AdventureUtil.sendMessage(
              context.getPlayer(),
              context.getConfiguration().getArgument(),
              AdventureUtil.placeholder("player", context.getPlayer().getName())
        );

        context.next();
    }

}
