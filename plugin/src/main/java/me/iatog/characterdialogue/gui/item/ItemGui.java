package me.iatog.characterdialogue.gui.item;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.gui.GUI;
import me.iatog.characterdialogue.libraries.ItemManager;
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemGui extends GUI {

    private final CharacterDialoguePlugin main;

    public ItemGui(CharacterDialoguePlugin main) {
        super("items");
        this.main = main;
    }

    @Override
    public void load(Player player) {
        ItemManager manager = main.getServices().getItemManager();
        PaginatedGui gui = Gui.paginated()
              .disableAllInteractions()
              .rows(6)
              .title(TextUtils.colorizeComponent("&e&lItems"))
              .create();

        gui.getFiller().fillBottom(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
              .name(TextUtils.colorizeComponent("&7").asComponent()).asGuiItem());

        gui.setItem(6, 2, GUI.previousItem
              .asGuiItem(event -> gui.previous()));

        gui.setItem(6, 3, GUI.nextItem
              .asGuiItem(event -> gui.next()));

        Map<String, ItemStack> items = manager.getItems();

        items.forEach((id, item) -> gui.addItem(ItemBuilder.from(item).asGuiItem((event) ->
              AdventureUtil.sendMessage(event.getWhoClicked(), "<green>Clicked item ID: <gray>" + id))));

        gui.open(player);
    }
}
