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

@Command(names = "dialogue",
      desc = "Blah",
      permission = "characterdialogue.command.dialogue")
public class DialogueCommands implements CommandClass {

    private final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    @Usage("<dialogueName> [player] [-debug]")
    @Command(names = "start", desc = "Run a dialogue")
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

}