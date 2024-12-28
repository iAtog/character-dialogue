package me.iatog.characterdialogue.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.SubCommandClasses;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.DialogueImpl;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.gui.GUI;
import me.iatog.characterdialogue.libraries.Cache;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.iatog.characterdialogue.util.TextUtils.colorize;

@Command(names = {
      "characterdialogue", "characterd"
}, permission = "characterdialogue.use",
      desc = "CharacterDialogue main command")
@SubCommandClasses({
      DialogueCommands.class,
      MethodCommands.class,
      GroupsCommands.class,
      RecordCommand.class,
      ItemCommands.class
})
public class CharacterDialogueCommand implements CommandClass {

    /*
     * /characterdialogue
     * /characterdialogue reload
     * /characterdialogue clear-cache
     * /characterdialogue dialogue view <name>
     * /characterdialogue dialogue start <name> [player]
     * /characterdialogue dialogue list
     * /characterdialogue assign <npc>
     * /characterdialogue item gui
     * /characterdialogue item save <id>
     * /characterdialogue item give <id>
     * /characterdialogue gui
     */

    private final CharacterDialoguePlugin main;

    public CharacterDialogueCommand(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Command(names = "", desc = "Main command")
    public void mainCommand(CommandSender sender) {
        sender.sendMessage(main.languageList("help-message"));
    }

    @Command(names = "reload",
          permission = "characterdialogue.reload",
          desc = "Reload the plugin")
    public void reloadCommand(CommandSender sender) {
        Cache cache = main.getCache();

        try {
            main.getFileFactory().reload();
        } catch (IOException e) {
            main.getLogger().severe(main.language("command.reload.error"));
            e.printStackTrace();
            sender.sendMessage(main.language(true, "command.reload.error"));
        }

        main.getApi().reloadHolograms();

        cache.getDialogues().clear();

        reloadDialogues(sender, cache);

        sender.sendMessage(main.language(true, "loaded-dialogues", cache.getDialogues().size()));
        sender.sendMessage(main.language(true, "command.reload.success"));
    }

    @Command(names = "clear-cache",
          permission = "characterdialogue.clear-cache",
          desc = "Clear a player memory cache")
    public void clearCache(CommandSender sender, Player target) {
        if (target == null || !target.isOnline()) {
            sender.sendMessage(main.language("general.offline-player"));
            return;
        }

        Map<UUID, DialogSession> dialogSessions = main.getCache().getDialogSessions();
        Map<UUID, ChoiceSession> choiceSessions = main.getCache().getChoiceSessions();
        boolean done = false;

        if (dialogSessions.containsKey(target.getUniqueId())) {
            dialogSessions.get(target.getUniqueId()).destroy();
            done = true;
        }

        if (choiceSessions.containsKey(target.getUniqueId())) {
            choiceSessions.get(target.getUniqueId()).destroy();
            done = true;
        }

        if (!done) {
            sender.sendMessage(main.language(true, "command.clear-cache.no-data"));
        } else {
            sender.sendMessage(main.language(true, "command.clear-cache.success"));
        }
    }

    @Command(
          names = "assign",
          permission = "characterdialogue.assign",
          desc = "Assign a dialogue to npc"
    )
    @Usage("<dialogue> [npcId]")
    public void assignNpc(@Sender CommandSender sender, Dialogue dialogue, AdaptedNPC npc) {
        if (dialogue == null) {
            sender.sendMessage(main.language(true, "command.assign.no-dialogue"));
            return;
        }

        if (npc == null) {
            sender.sendMessage(main.language(true, "command.assign.no-npc"));
            return;
        }

        YamlDocument config = main.getFileFactory().getConfig();
        config.set("npc." + npc.getId(), dialogue.getName());
        try {
            config.save();
        } catch (IOException e) {
            sender.sendMessage(main.language("command.assign.error"));
            return;
        }
        sender.sendMessage(main.language("command.assign.success", npc.getName(), dialogue.getName()));
    }

    @Command(
          names = "gui",
          permission = "characterdialogue.gui"
    )
    public void openGUI(@Sender Player player, GUI gui) {
        if (gui == null) {
            player.sendMessage(main.language("command.gui.not-found"));
            return;
        }

        gui.load(player);
        player.sendMessage(main.language(true, "command.gui.success", gui.getPath()));
    }


    private void reloadDialogues(CommandSender sender, Cache cache) {
        try {
            main.loadAllDialogues();

            for (YamlDocument dialogueFile : main.getAllDialogues()) {
                if (dialogueFile == null) continue;
                Section section = dialogueFile.getSection("dialogue");

                if (section != null) {
                    section.getRoutesAsStrings(false).forEach(name -> {
                        cache.getDialogues().put(name, new DialogueImpl(main, name, dialogueFile));
                    });
                }
            }

        } catch (IOException exception) {
            sender.sendMessage(main.language("command.reload.dialogue-error"));
            exception.printStackTrace();
            return;
        }
    }

}
