package me.iatog.characterdialogue.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.Group;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Command(
      names = "group"
)
public class GroupsCommands implements CommandClass {

    @Command(
          names = "create",
          permission = "characterdialogue.create"
    )
    public void create(@Sender CommandSender sender, @OptArg String groupName) {
        CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
        if (groupName == null || groupName.isEmpty()) {
            sender.sendMessage(TextUtils.colorize("&cGroup name is empty"));
            return;
        }

        File file = new File(main.getDataFolder() + "/dialogues/" + groupName + ".yml");

        if (file.exists()) {
            sender.sendMessage(TextUtils.colorize("&cThis group already exists."));
            return;
        }

        try {
            YamlDocument newGroup = YamlDocument.create(file, Objects.requireNonNull(main.getResource("dialogue-template.yml")));

            main.getAllDialogues().add(newGroup);
            sender.sendMessage(TextUtils.colorize("&8[&cCD&8] &7Dialog &8'&a" + groupName + "&8'&7 has been created"));
        } catch (IOException e) {
            sender.sendMessage(TextUtils.colorize("Fatal error while creating dialogue file."));
            e.printStackTrace();
        }
    }

    @Command(
          names = "edit",
          permission = "characterdialogue.groups.edit"
    )
    public void edit(@Sender CommandSender sender, Group group) {
        CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
        if (group == null) {
            sender.sendMessage(TextUtils.colorize("&cGroup not found."));
            return;
        }

        sender.sendMessage(TextUtils.colorize("&c" + group.getName()));
/*
        boolean success = false;

        for(YamlDocument groupDocument : main.getAllDialogues()) {
            String name = Objects.requireNonNull(groupDocument.getFile()).getName().split("\\.")[0];

        }*/


    }

}
