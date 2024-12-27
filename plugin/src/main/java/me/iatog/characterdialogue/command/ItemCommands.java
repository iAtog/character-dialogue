package me.iatog.characterdialogue.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.ItemManager;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(
      names = "item"
)
public class ItemCommands implements CommandClass {

    private final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    @Command(
          names = "gui"
    )
    public void seeGui(@Sender Player player) {
        player.sendMessage(TextUtils.colorize("Opening items GUI..."));
        main.getGUIFactory().getGui("items").load(player);
    }

    @Command(
          names = "save"
    )
    public void saveItem(@Sender Player player, String id) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if(itemInHand.getAmount() <= 0 || itemInHand.getType() == Material.AIR) {
            player.sendMessage(TextUtils.colorize("&cYou must have an item in your main hand."));
            return;
        }

        if(id == null || id.isEmpty()) {
            player.sendMessage(TextUtils.colorize("&cYou must enter the item ID."));
            return;
        }

        ItemManager manager = main.getServices().getItemManager();

        if(manager.existsItem(id)) {
            player.sendMessage(TextUtils.colorize("&cThere's already an item with this ID."));
            return;
        }

        manager.saveItem(id, itemInHand);
        player.sendMessage(TextUtils.colorize("&aThe item &7'&8" + id + "&7'&a has been successfully saved."));
    }

    @Command(
          names = "give"
    )
    public void giveItem(@Sender Player player, ItemStack item) {
        ItemManager manager = main.getServices().getItemManager();

        if(item == null) {
            player.sendMessage(TextUtils.colorize("&cNo item was found."));
            return;
        }

        player.getInventory().addItem(item);
        player.sendMessage(TextUtils.colorize("&aYou have obtained the item correctly"));
    }
}
