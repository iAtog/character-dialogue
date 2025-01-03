package me.iatog.characterdialogue.command.object;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static me.iatog.characterdialogue.util.TextUtils.colorize;

public abstract class CSubCommand {

    private final List<CommandInfo> info;

    public CSubCommand() {
        info = new ArrayList<>();
        addCommands();
    }

    public abstract void addCommands();

    protected void addCommand(String name, String usage, String description) {
        info.add(new CommandInfo(name, usage, description));
    }

    protected void mainCommandLogic(CharacterDialoguePlugin main, CommandSender sender) {
        String input = main.language("command-info");
        AdventureUtil.sendMessage(sender, Component.empty());
        AdventureUtil.sendMessage(sender, "<red><bold>>> <reset><gray>[  <gold>CharacterDialogue  <gray>] <strikethrough>                  ");

        for(CommandInfo cmd : info) {
            AdventureUtil.sendMessage(sender,
                  input.replace("%command%", cmd.name())
                        .replace("%usage%", (cmd.usage().isEmpty() ? "" : cmd.usage()+" "))
                        .replace("%description%", cmd.desc())
            );
        }
    }

    public List<CommandInfo> getCommands() {
        return info;
    }

}
