package me.iatog.characterdialogue.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.ConsumeAll;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.TestDialogueImpl;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.command.object.CSubCommand;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

@Command(
      names = "method",
      desc = "Method related commands",
      permission = "characterdialogue.command.method"
)
public class MethodCommands extends CSubCommand implements CommandClass {

    private final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    public MethodCommands() {
        super();
    }

    public void addCommands() {
        addCommand("characterd method list", "", "List all registered methods");
        addCommand("characterd method execute", "<methodLine>", "Execute dialogue line");
    }

    @Command(names = "", desc = "Main command")
    public void mainCommand(CommandSender sender) {
        mainCommandLogic(main, sender);
    }

    @Command(names = "list")
    public void list(@Sender CommandSender sender) {
        AdventureUtil.sendMessage(sender, main.language("command.method.list-title"));

        main.getCache().getMethods().forEach((id, method) -> {
            String description = method.getDescription();
            String line = main.language("command.method.list-line", id.toLowerCase());
            AdventureUtil.sendMessage(sender, "");
            AdventureUtil.sendMessage(sender, line);
            AdventureUtil.sendMessage(sender, description);
        });
    }

    @Usage("<methodLine>")
    @Command(names = "execute")
    public void execute(@Sender Player sender, @ConsumeAll List<String> args) {
        if (args == null || args.isEmpty()) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.method.no-args"));
            return;
        }

        StringBuilder arguments = new StringBuilder();
        for (String arg : args)
            arguments.append(arg).append(" ");

        Matcher matcher = main.getApi().getLineRegex().matcher(arguments.toString().trim());

        if(!matcher.find()) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.method.invalid-line"));
            return;
        }

        String methodName = matcher.group(1).toLowerCase().trim();
        if (!main.getCache().getMethods().containsKey(methodName)) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.method.invalid-method", methodName));
            return;
        }

        Map<UUID, DialogSession> sessions = main.getCache().getDialogSessions();

        if (sessions.containsKey(sender.getUniqueId())) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.method.on-session"));
            return;
        }

        Dialogue dialogue = new TestDialogueImpl();
        DialogSession session = new DialogSession(main, sender, dialogue);
        sessions.put(sender.getUniqueId(), session);

        SingleUseConsumer<CompletedType> onComplete = SingleUseConsumer.create((res) -> {
                  sessions.remove(sender.getUniqueId());
                  AdventureUtil.sendMessage(sender, main.language(true, "command.method.executed",
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
