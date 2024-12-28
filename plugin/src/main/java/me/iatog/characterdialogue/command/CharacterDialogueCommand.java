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
    private final YamlDocument language;

    public CharacterDialogueCommand(CharacterDialoguePlugin main) {
        this.main = main;
        this.language = this.main.getFileFactory().getLanguage();
    }

    @Command(names = "", desc = "Main command")
    public void mainCommand(CommandSender sender) {
        sender.sendMessage(translateList(language.getStringList("help-message")).toArray(String[]::new));
    }

    @Command(names = "reload",
          permission = "characterdialogue.reload",
          desc = "Reload the plugin")
    public void reloadCommand(CommandSender sender) {
        Cache cache = main.getCache();

        try {
            main.getFileFactory().reload();
        } catch (IOException e) {
            main.getLogger().severe("Error reloading CharacterDialogue files.");
        }

        main.getApi().reloadHolograms();

        cache.getDialogues().clear();

        reloadDialogues(sender, cache);

        sender.sendMessage(colorize("&aLoaded " + cache.getDialogues().size() + " dialogues."));

        sender.sendMessage(colorize(language.getString("reload-message")));
    }

    @Command(names = "clear-cache",
          permission = "characterdialogue.clear-cache",
          desc = "Clear a player memory cache")
    public void clearCache(CommandSender sender, Player target) {
        if (target == null || ! target.isOnline()) {
            sender.sendMessage(colorize("&cThe player isn't online."));
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

        if (! done) {
            sender.sendMessage(colorize("&cThat player doesn't have any data in the memory cache."));
        } else {
            sender.sendMessage(colorize("&aCleared " + target.getName() + "'s cache"));
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
            sender.sendMessage(colorize("&8[&cCD&8] &cThe dialogue was not found."));
            return;
        }

        if (npc == null) {
            sender.sendMessage(colorize("&8[&cCD&8] &cNpc not found."));
            return;
        }

        YamlDocument config = main.getFileFactory().getConfig();
        config.set("npc." + npc.getId(), dialogue.getName());
        try {
            config.save();
        } catch (IOException e) {
            sender.sendMessage(colorize("&8[&cCD&8] &cError saving data."));
            return;
        }
        sender.sendMessage(colorize(String.format("&8[&cCD&8] &aThe npc '%s' was assigned to '%s' dialogue.",
              npc.getName(), dialogue.getName())));
    }

    @Command(
          names = "gui",
          permission = "characterdialogue.gui"
    )
    public void openGUI(@Sender Player player, GUI gui) {
        if (gui == null) {
            return;
        }

        gui.load(player);
        player.sendMessage(colorize("&aLoaded the gui!"));
    }

    private List<String> translateList(List<String> list) {
        List<String> newList = new ArrayList<>();
        list.forEach((line) -> {
            newList.add(colorize(line));
        });
        return newList;
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
            sender.sendMessage("Error loading all dialogues");
            exception.printStackTrace();
            return;
        }
    }

}
