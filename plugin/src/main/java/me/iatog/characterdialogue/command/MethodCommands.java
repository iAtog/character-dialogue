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
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.part.method.DialogMethodArgument;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(
      names = "method",
      desc = "Method related commands",
      permission = "characterdialogue.command.method"
)
public class MethodCommands implements CommandClass {

    private CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    @Command(names = "")
    public void list(@Sender CommandSender sender) {
        sender.sendMessage(main.language("command.method.list-title"));
        main.getCache().getMethods().forEach((id, method) -> {
            String description = method.getDescription();
            String line = main.language("command.method.list-line", id.toLowerCase(), description);
            sender.sendMessage(line);
        });
    }

    @Command(names = "execute")
    @Usage("<methodLine>")
    public void execute(@Sender Player sender, @ConsumeAll List<String> args) {
        if (args == null || args.isEmpty()) {
            sender.sendMessage(main.language(true, "command.method.no-args"));
            return;
        }

        StringBuilder arguments = new StringBuilder();
        for (String arg : args)
            arguments.append(" ").append(arg);

        Matcher matcher = main.getApi().getLineRegex().matcher(arguments.toString());

        if(!matcher.find()) {
            sender.sendMessage(main.language(true, "command.method.invalid-line"));
            return;
        }

        String methodName = matcher.group(1).toLowerCase().trim();
        if (!main.getCache().getMethods().containsKey(methodName)) {
            sender.sendMessage(main.language(true, "command.method.invalid-method", methodName));
            return;
        }

        Map<UUID, DialogSession> sessions = main.getCache().getDialogSessions();

        if (sessions.containsKey(sender.getUniqueId())) {
            sender.sendMessage(main.language(true, "command.method.on-session"));
            return;
        }

        Dialogue dialogue = new TestDialogueImpl();
        DialogSession session = new DialogSession(main, sender, dialogue);
        sessions.put(sender.getUniqueId(), session);

        SingleUseConsumer<CompletedType> onComplete = SingleUseConsumer.create((res) -> {
                  sessions.remove(sender.getUniqueId());
                  sender.sendMessage(main.language(true, "command.method.executed",
                        methodName, res.toString().toLowerCase()));
        });

        main.getApi().runDialogueExpression(
              sender,
              arguments.toString(),
              "MethodCommand",
              onComplete,
              session,
              null
        );
    }

}
