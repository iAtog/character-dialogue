package me.iatog.characterdialogue.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.ConsumeAll;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.TestDialogueImpl;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.part.method.DialogMethodArgument;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Command(
      names = "method",
      desc = "Method related commands",
      permission = "characterdialogue.command.method"
)
public class MethodCommands implements CommandClass {

    @Command(names = "")
    public void list(@Sender CommandSender sender) {
        sender.sendMessage("hola");
    }

    @Command(names = "execute")
    @Usage("<method> <arguments>")
    public void execute(@Sender Player sender, DialogMethodArgument method, @ConsumeAll List<String> args) {
        if (method == null) {
            sender.sendMessage(TextUtils.colorize("&cMethod not found"));
            return;
        }

        if (args == null || args.isEmpty()) {
            sender.sendMessage(TextUtils.colorize("&cInvalid arguments provided."));
            return;
        }

        StringBuilder arguments = new StringBuilder();
        for (String arg : args)
            arguments.append(" ").append(arg);

        CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
        Map<UUID, DialogSession> sessions = main.getCache().getDialogSessions();

        if (sessions.containsKey(sender.getUniqueId())) {
            sender.sendMessage(TextUtils.colorize("&cYou cant run this command while a dialogue session."));
            return;
        }

        Dialogue dialogue = new TestDialogueImpl();
        DialogSession session = new DialogSession(main, sender, dialogue);
        sessions.put(sender.getUniqueId(), session);
        MethodConfiguration config = new MethodConfiguration(arguments.toString().trim(), "");

        MethodContext context = new MethodContext(sender, session, config, SingleUseConsumer.create((res) -> {
            sessions.remove(sender.getUniqueId());
            sender.sendMessage(TextUtils.colorize("&8[&eCharacterDialogue&8] &aMethod &8'&7" + method.getName() + "&8' &aexecuted correctly with result: " + res));
        }), null);

        method.getMethod().execute(context);
    }

}
