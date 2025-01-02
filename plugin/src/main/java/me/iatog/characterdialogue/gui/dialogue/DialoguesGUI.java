package me.iatog.characterdialogue.gui.dialogue;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.gui.GUI;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.iatog.characterdialogue.util.TextUtils.colorizeComponent;

public class DialoguesGUI extends GUI {

    private final CharacterDialoguePlugin main;

    public DialoguesGUI(CharacterDialoguePlugin main) {
        super("dialogues");
        this.main = main;
    }

    @Override
    public void load(Player player) {
        if(!player.getName().equals("aatog")) {
            player.sendMessage("Gui not available");
            return;
        }
        PaginatedGui gui = Gui.paginated()
              .rows(5)
              .title(colorizeComponent("&cDialogues | CharacterDialogue"))
              .disableAllInteractions()
              .create();
        gui.getFiller().fillBottom(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());

        gui.setItem(5, 1, ItemBuilder.from(Material.ARROW)
              .name(colorizeComponent("&cGo back"))
              .lore(buildLore("&7Go back to main menu"))
              .asGuiItem(event -> {
                  main.getGUIFactory().getGui("main").load((Player) event.getWhoClicked());
              }));

        gui.setItem(5, 2, GUI.previousItem
              .asGuiItem(event -> gui.previous()));

        gui.setItem(5, 3, GUI.nextItem
              .asGuiItem(event -> gui.next()));

        gui.setItem(5, 9, ItemBuilder.from(Material.WRITTEN_BOOK)
              .name(colorizeComponent("&aCreate"))
              .lore(buildLore(
                    TextUtils.wrapText("Create a new dialog group (.yml file), with the ability to edit it and see how everything works.", 40, "&7").toArray(new String[0])
              ))
              .asGuiItem());

        main.getCache().getDialogues().forEach((id, dialogue) -> {
            Dialogue.DialoguePermission permissions = dialogue.getPermissions();
            GuiItem item = ItemBuilder.from(Material.PAPER)
                  .name(colorizeComponent("&c" + id))
                  .lore(buildLore(
                        "&7",
                        "&bDisplay name&8: &7" + dialogue.getDisplayName(),
                        "&bClick type&8: &7" + dialogue.getClickType().toString().toLowerCase(),
                        "&bLines&8: &7" + dialogue.getLines().size(),
                        "&bPermission&8: &7" + (permissions == null ? "&eeveryone" : permissions.getPermission())
                  ))
                  .asGuiItem(action -> {
                      DialogueEditSession editGUI = new DialogueEditSession(main, dialogue);
                      editGUI.load((Player) action.getWhoClicked());
                  });
            gui.addItem(item);
        });

        setupObservers(gui);

        gui.open(player);
    }

}
