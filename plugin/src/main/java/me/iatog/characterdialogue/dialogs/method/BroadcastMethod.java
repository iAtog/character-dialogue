package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Bukkit;

public class BroadcastMethod extends DialogMethod<CharacterDialoguePlugin> {

    public BroadcastMethod(CharacterDialoguePlugin main) {
        super("broadcast", main);

        setDescription("Broadcast a message to all players");
    }

    @Override
    public void execute(MethodContext context) {
        getProvider().getAudiences().all().sendMessage(
              AdventureUtil.minimessage(context.getConfiguration().getArgument())
        );
        context.next();
    }

}
