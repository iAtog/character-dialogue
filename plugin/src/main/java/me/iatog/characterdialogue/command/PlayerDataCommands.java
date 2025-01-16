package me.iatog.characterdialogue.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.*;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.command.object.CSubCommand;
import me.iatog.characterdialogue.player.PlayerData;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(
      names = "player-data",
      permission = "characterdialogue.command.player-data"
)
public class PlayerDataCommands extends CSubCommand implements CommandClass {

    private static final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    @Override
    public void addCommands() {
        addCommand("characterd player-data <player> add", "<dialogue> [-f]", "Add a readed dialogue");
        addCommand("characterd player-data <player> remove", "<dialogue> [-f]", "Remove a readed dialogue");
        addCommand("characterd player-data <player> view", "", "View all readed dialogues");
    }

    @Command(
          names = ""
    )
    public void mainCommand(CommandSender sender, @Named("player") Player player) {
        mainCommandLogic(main, sender);
    }

    @Usage("<dialogue> [-f]")
    @Command(
          names = "remove"
    )
    public void remove(CommandSender sender, @ParentArg @Named("player") Player target, String dialogue, @Switch("f") boolean firstInteraction) {
        if(target == null || dialogue == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.player-data.invalid"));
            return;
        }

        PlayerData data = main.getCache().getPlayerData().get(target.getUniqueId());
        List<String> dialogues = data.getDialogues(firstInteraction);

        if(!dialogues.contains(dialogue)) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.player-data.removed.not-present", dialogue));
            return;
        }

        dialogues.remove(dialogue);
        AdventureUtil.sendMessage(sender, main.language(true, "command.player-data.removed." + name(firstInteraction), dialogue));
    }

    @Usage("<dialogue> [-f]")
    @Command(
          names = "add"
    )
    public void add(CommandSender sender, @ParentArg @Named("player") Player target, String dialogue, @Switch("f") boolean firstInteraction) {
        if(target == null || dialogue == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.player-data.invalid"));
            return;
        }

        PlayerData data = main.getCache().getPlayerData().get(target.getUniqueId());
        List<String> dialogues = data.getDialogues(firstInteraction);

        if(dialogues.contains(dialogue)) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.player-data.added.already", dialogue));
            return;
        }

        dialogues.add(dialogue);
        AdventureUtil.sendMessage(sender, main.language(true, "command.player-data.added." + name(firstInteraction), dialogue));
    }

    @Command(
          names = "view"
    )
    public void view(CommandSender sender, @ParentArg @Named("player") Player target) {
        if(target == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.player-data.invalid"));
            return;
        }

        PlayerData data = main.getCache().getPlayerData().get(target.getUniqueId());

        AdventureUtil.sendMessage(sender,
              main.language("command.player-data.view.title"),
              AdventureUtil.placeholder("player", target.getName())
        );
        AdventureUtil.sendMessage(sender,
              main.language("command.player-data.view.dialogues"),
              AdventureUtil.placeholder("dialogues", Strings.join(data.getFinishedDialogs(), ','))
        );
        AdventureUtil.sendMessage(sender,
              main.language("command.player-data.view.first-interactions"),
              AdventureUtil.placeholder("dialogues", Strings.join(data.getFirstInteractions(), ','))
        );
    }

    private String name(boolean firstInteraction) {
        return firstInteraction ? "firstInteraction" : "dialogue";
    }
}
