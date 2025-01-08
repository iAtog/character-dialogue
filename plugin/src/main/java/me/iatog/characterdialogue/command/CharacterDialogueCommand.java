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
import me.iatog.characterdialogue.command.object.CSubCommand;
import me.iatog.characterdialogue.gui.GUI;
import me.iatog.characterdialogue.libraries.Cache;
import me.iatog.characterdialogue.player.PlayerData;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Command(names = {
      "characterdialogue", "characterd"
}, permission = "characterdialogue.use",
      desc = "CharacterDialogue main command")
@SubCommandClasses({
      DialogueCommands.class,
      MethodCommands.class,
      GroupsCommands.class,
      RecordCommand.class,
      ItemCommands.class,
      PlayerDataCommands.class
})
public class CharacterDialogueCommand extends CSubCommand implements CommandClass {

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
     * /characterdialogue gui <id>
     */

    private final CharacterDialoguePlugin main;
    private MiniMessage minimessage;
    private BukkitAudiences audiences;

    public CharacterDialogueCommand(CharacterDialoguePlugin main) {
        super();
        this.main = main;
        this.minimessage = MiniMessage.miniMessage();
        this.audiences = BukkitAudiences.create(main);
    }

    public void addCommands() {
        addCommand("characterd reload", "", "Reload the plugin and files");
        addCommand("characterd clear-cache", "<player>", "Clear player info");
        addCommand("characterd dialogue", "", "Use some dialogues.");
        addCommand("characterd method", "", "Try some methods.");
        addCommand("characterd assign", "<npcId>", "Assign a dialogue to a npc");
        addCommand("characterd item", "", "Manage custom items");
        addCommand("characterd gui", "<name>", "Open registered guis");
        addCommand("characterd record", "", "Manage recordings");
    }

    @Command(names = "", desc = "Main command")
    public void mainCommand(CommandSender sender) {
        mainCommandLogic(main, sender);
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
        cache.getRegionalDialogues().clear();

        reloadDialogues(sender, cache);
        main.loadRegionalDialogues();

        main.clearAllChoices();
        main.loadAllChoices();

        AdventureUtil.sendMessage(sender, main.language(true, "loaded-dialogues", cache.getDialogues().size()));
        AdventureUtil.sendMessage(sender, main.language(true, "command.reload.success"));
    }

    //@Command(names = "player-data")
    public void viewData(@Sender CommandSender sender, Player player) {
        if(player == null) {
            return;
        }

        PlayerData data = main.getCache().getPlayerData().get(player.getUniqueId());

        AdventureUtil.sendMessage(sender, "<red>Dialogues<gray>: " + Strings.join(data.getFinishedDialogs(), ','));
        AdventureUtil.sendMessage(sender, "<red>First interactions<gray>: " + Strings.join(data.getFirstInteractions(), ','));
    }

    @Usage("<player>")
    @Command(names = "clear-cache",
          permission = "characterdialogue.clear-cache",
          desc = "Clear a player memory cache")
    public void clearCache(CommandSender sender, Player target) {
        if (target == null || !target.isOnline()) {
            AdventureUtil.sendMessage(sender, main.language("general.offline-player"));
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
            AdventureUtil.sendMessage(sender, main.language(true, "command.clear-cache.no-data"));
        } else {
            AdventureUtil.sendMessage(sender, main.language(true, "command.clear-cache.success"));
        }
    }

    @Usage("<dialogue> [npcId]")
    @Command(
          names = "assign",
          permission = "characterdialogue.assign",
          desc = "Assign a dialogue to npc"
    )
    public void assignNpc(@Sender CommandSender sender, Dialogue dialogue, AdaptedNPC npc) {
        if (dialogue == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.assign.no-dialogue"));
            return;
        }

        if (npc == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.assign.no-npc"));
            return;
        }

        YamlDocument config = main.getFileFactory().getConfig();
        String route = "npc." + npc.getId();

        if(config.contains(route)) {
            String dialogues = config.getString(route);
            config.set("npc." + npc.getId(), dialogues + "," + dialogue.getName());
        } else {
            config.set("npc." + npc.getId(), dialogue.getName());
        }


        try {
            config.save();
        } catch (IOException e) {
            AdventureUtil.sendMessage(sender, main.language("command.assign.error"));
            return;
        }

        AdventureUtil.sendMessage(sender, main.language("command.assign.success", npc.getName(), dialogue.getName()));
    }

    @Usage("<name>")
    @Command(
          names = "gui",
          permission = "characterdialogue.gui"
    )
    public void openGUI(@Sender Player player, GUI gui) {
        if (gui == null) {
            AdventureUtil.sendMessage(player, main.language("command.gui.not-found"));
            return;
        }

        gui.load(player);
        AdventureUtil.sendMessage(player, main.language(true, "command.gui.success", gui.getPath()));
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
