package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandMethod extends DialogMethod<CharacterDialoguePlugin> {
    // command{sender=console}: gamemode creative %player_name%
    // command{sender=player}: kit newbie
    public CommandMethod(CharacterDialoguePlugin main) {
        super("command", main);

        addConfigurationType("sender", ConfigurationType.TEXT);
        setDescription("Execute a command, choose whether it is for the player or the console.");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        DialogSession session = context.getSession();

        String command = configuration.getArgument();
        String configSender = configuration.getString("sender", "console");
        CommandSender sender;

        if (configSender.equalsIgnoreCase("console")) {
            sender = Bukkit.getConsoleSender();
        } else if (configSender.equalsIgnoreCase("player")) {
            sender = context.getPlayer();
        } else {
            String msg = "Invalid sender specified '" + configSender + "' in dialogue: " + session.getDialogue().getName();
            getProvider().getLogger().warning(msg);
            session.sendDebugMessage(msg, "CommandMethod");
            context.destroy();
            return;
        }

        Bukkit.dispatchCommand(sender, command);
        context.next();
    }

}
