package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Bukkit;

public class BroadcastMethod extends DialogMethod<CharacterDialoguePlugin> {

    public BroadcastMethod() {
        super("broadcast");

        setDescription("Broadcast a message to all players");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(MethodContext context) {
        Bukkit.broadcastMessage(TextUtils.colorize(context.getConfiguration().getArgument()));
        context.next();
    }

}
