package me.iatog.characterdialogue.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.ItemManager;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.iatog.characterdialogue.util.TextUtils.colorize;

@Command(
      names = "item"
)
public class ItemCommands implements CommandClass {

    private final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
    private final List<CommandInfo> info;

    public ItemCommands() {
        this.info = new ArrayList<>();
        addCommands();
    }

    private void addCommands() {
        addCommand("characterd item gui", "", "Open the items gui");
        addCommand("characterd item save", "<newName>", "Save the item in your main hand");
        addCommand("characterd item give", "<name>", "Get an item");

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

    @Command(
          names = "gui",
          permission = "characterdialogue.command.item.gui"
    )
    public void seeGui(@Sender Player player) {
        player.sendMessage(main.language("command.gui.success", "items"));
        main.getGUIFactory().getGui("items").load(player);
    }

    @Usage("<id>")
    @Command(
          names = "save",
          permission = "characterdialogue.command.item.save"
    )
    public void saveItem(@Sender Player player, @OptArg("") String id) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if(itemInHand.getAmount() <= 0 || itemInHand.getType() == Material.AIR) {
            player.sendMessage(main.language(true, "command.item.no-hand"));
            return;
        }

        if(id == null || id.isEmpty()) {
            player.sendMessage(main.language(true, "command.item.no-id"));
            return;
        }

        ItemManager manager = main.getServices().getItemManager();

        if(manager.existsItem(id)) {
            player.sendMessage(main.language(true, "command.item.already-exists"));
            return;
        }

        manager.saveItem(id, itemInHand);
        player.sendMessage(main.language(true, "command.item.success"));
    }

    @Usage("<id> [player]")
    @Command(
          names = "give",
          permission = "characterdialogue.command.item.give"
    )
    public void giveItem(CommandSender sender, ItemStack item, @OptArg Player playerOpt) {
        if(item == null) {
            sender.sendMessage(main.language(true, "command.item.not-found"));
            return;
        }

        Player target = null;

        if (playerOpt == null) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(main.language(true, "command.dialogue.console"));
                return;
            }

            target = player;
        } else {
            target = playerOpt;
        }

        target.getInventory().addItem(item);
        sender.sendMessage(main.language(true, "command.item.give-success"));
    }
}
