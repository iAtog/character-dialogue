package me.iatog.characterdialogue.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Switch;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.iatog.characterdialogue.util.TextUtils.colorize;

@Command(names = "dialogue",
      desc = "Blah",
      permission = "characterdialogue.command.dialogue")
public class DialogueCommands implements CommandClass {

    private final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
    private final List<CommandInfo> info;

    public DialogueCommands() {
        this.info = new ArrayList<>();
        addCommands();
    }

    private void addCommands() {
        addCommand("characterd dialogue start", "<dialogue> [player] [-debug]", "Start a dialogue");
        addCommand("characterd dialogue info", "<dialogue>", "See dialogue info");
    }

    private void addCommand(String name, String usage, String description) {
        info.add(new CommandInfo(name, usage, description));
    }

    @Command(names = "", desc = "Main command")
    public void mainCommand(CommandSender sender) {
        String input = main.language("command-info");
        sender.sendMessage(colorize("&c&l>> &7[  &6CharacterDialogue  &7]&m&7&l          "));

        for(CommandInfo cmd : info) {
            sender.sendMessage(
                  input.replace("%command%", cmd.name())
                        .replace("%usage%", (cmd.usage().isEmpty() ? "" : cmd.usage()+" "))
                        .replace("%description%", cmd.desc())
            );
        }
    }

    @Usage("<dialogue> [player] [-debug]")
    @Command(
          names = "start",
          desc = "Run a dialogue",
          permission = "characterdialogue.command.dialogue.start"
    )
    public void dialoguesCommand(
          @Sender CommandSender sender,
          Dialogue dialogue,
          @OptArg Player playerOpt,
          @Switch("debug") boolean debug
    ) {
        Player target;
        if (dialogue == null) {
            sender.sendMessage(main.language(true, "command.dialogue.not-found"));
            return;
        }

        if (playerOpt == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(main.language(true, "command.dialogue.console"));
                return;
            }

            target = (Player) sender;
        } else {
            target = playerOpt;
        }

        sender.sendMessage(main.language(true, "command.dialogue.success",
              dialogue.getName(), target.getName()));
        dialogue.start(target, debug, null);
    }

    @Usage("<dialogue>")
    @Command(
          names = "info",
          permission = "characterdialogue.command.dialogue.info"
    )
    public void info(@Sender CommandSender sender, Dialogue dialogue) {
        if (dialogue == null) {
            sender.sendMessage(main.language(true, "command.dialogue.not-found"));
            return;
        }

        sender.sendMessage(main.language("command.dialogue.info.title"));
        sender.sendMessage(main.language("command.dialogue.info.id", dialogue.getName()));
        sender.sendMessage(main.language("command.dialogue.info.display-name", dialogue.getDisplayName()));
        sender.sendMessage(main.language("command.dialogue.info.lines", ""+dialogue.getLines().size()));
        sender.sendMessage(main.language("command.dialogue.info.file", dialogue.getDocument().getName()));
        sender.sendMessage(main.language("command.dialogue.info.click-type", dialogue.getClickType().toString().toLowerCase()));

        if(dialogue.isPersistent()) {
            String persistenceText = dialogue.isPersistent() ? "yes" : "no";
            sender.sendMessage(main.language("command.dialogue.info.persistence.persistent", persistenceText));
            if(dialogue.getPersistentLines() != null && !dialogue.getPersistentLines().isEmpty()) {
                sender.sendMessage(main.language("command.dialogue.info.persistence.total", ""+dialogue.getPersistentLines().size()));
            }
        }

        if(dialogue.isFirstInteractionEnabled() && !dialogue.getFirstInteractionLines().isEmpty()) {
            sender.sendMessage(main.language("command.dialogue.info.first-interaction.lines",
                  dialogue.getFirstInteractionLines().size()+""));
        }

        String name = dialogue.getName();
        int lines = dialogue.getLines().size();

    }

}