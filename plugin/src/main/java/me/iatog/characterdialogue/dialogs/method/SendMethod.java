package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.entity.Player;

public class SendMethod extends DialogMethod<CharacterDialoguePlugin> {

    public SendMethod() {
        super("send");

        setDescription("Sends a message to the player");
    }

    @Override
    public void execute(MethodContext context) {
        Player player = context.getPlayer();

        AdventureUtil.sendMessage(
              context.getPlayer(),
              Placeholders.translate(player, context.getConfiguration().getArgument()),
              AdventureUtil.placeholder("player", player.getName())
        );

        context.next();
    }

}
