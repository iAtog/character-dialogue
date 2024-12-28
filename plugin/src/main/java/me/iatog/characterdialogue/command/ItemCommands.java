package me.iatog.characterdialogue.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
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
          names = "gui",
          permission = "characterdialogue.command.item.gui"
    )
    public void seeGui(@Sender Player player) {
        player.sendMessage(main.language("command.gui.success", "items"));
        main.getGUIFactory().getGui("items").load(player);
    }

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

    @Command(
          names = "give",
          permission = "characterdialogue.command.item.give"
    )
    public void giveItem(@Sender Player player, ItemStack item) {
        if(item == null) {
            player.sendMessage(main.language(true, "command.item.not-found"));
            return;
        }

        player.getInventory().addItem(item);
        player.sendMessage(main.language(true, "command.item.give-success"));
    }
}
