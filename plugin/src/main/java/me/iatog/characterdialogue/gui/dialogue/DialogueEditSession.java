package me.iatog.characterdialogue.gui.dialogue;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.DialogueLine;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.gui.GUI;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.iatog.characterdialogue.util.TextUtils.colorizeComponent;

public class DialogueEditSession extends GUI {

    private final Dialogue dialogue;
    private BaseGui gui;
    private final CharacterDialoguePlugin main;

    public DialogueEditSession(CharacterDialoguePlugin main, Dialogue dialogue) {
        super("unregistered_dialogue_edition");
        this.main = main;
        this.dialogue = dialogue;
    }

    // Main Dialogue gui
    public void load(Player player) {
        if(!player.getName().equals("aatog")) {
            player.sendMessage("Gui not available");
            return;
        }
        recreateGui();
        for(int i = 0; i < dialogue.getLines().size(); i++) {
            String line = dialogue.getLines().get(i);
            int currentLine = (i + 1);
            DialogueLine dialogueLine = new DialogueLine(main, line);
            if(dialogueLine.isAnnotation()) {
                ItemBuilder builder = ItemBuilder.from(Material.ACACIA_SIGN)
                      .name(colorizeComponent("&bAnnotation"))
                      .lore(buildLore("&7", "&a" + line));
                gui.addItem(builder.asGuiItem());
                continue;
            }

            MethodConfiguration config = dialogueLine.getConfiguration();
            ItemBuilder builder = ItemBuilder.from(Material.PAPER)
                  .name(colorizeComponent("&6Dialogue line: &c" + currentLine));
            List<String> configurations = new ArrayList<>();

            if(!dialogueLine.getConfigurationTypes().isEmpty()) {
                configurations.add("&7");
                configurations.add("&8&l> &6Configuration&8:");
                dialogueLine.getConfigurationTypes().forEach((key, valueType) -> {
                    configurations.add("&6" + key + "&8: &f" + config.get(key, "&c&ndefault"));
                });
            }

            configurations.add("&7");
            configurations.add("&cRight click to delete");
            configurations.add("&aLeft click to edit");

            builder.lore(buildLore(configurations,
                  "&7",
                  "&bMethod&8: &7" + dialogueLine.getMethodName(),
                  "&bArgument&8: &7" + config.getArgument(),
                  "&bConfig Size&8: &c" + config.map().size()
            ));

            gui.addItem(builder.asGuiItem());
        }

        gui.setItem(5, 5, ItemBuilder.skull()
              .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM0MGQ1MGQ3ZDEyOTNiYTE2ZDIzYzZkMDdhYjA2NmNkYzE1NzVjNjhiY2E2OWU5NmYwYmI2ZDFjZTFiZjFiYSJ9fX0=")
              .name(colorizeComponent("&c&lInformation"))
              .lore(buildLore(
                    "&7",
                    "&bDisplay name&8: &7" + dialogue.getDisplayName(),
                    "&bClick type&8: &7" + dialogue.getClickType()
              ))
              .asGuiItem(event -> {
                  if(dialogue.getDocument() == null) {
                      return;
                  }
                  dialogue.getSection().set("testdos", "Hello guys");
                  try {
                      dialogue.getDocument().save();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }));

        gui.open(player);
    }


    public void loadLineEdition(Player player) {
        Gui lineGui = Gui.gui()
              .rows(3)
              .title(colorizeComponent("&bEditing line &8| &cCharacterDialogue"))
              .enableAllInteractions()
              .create();
    }

    public void recreateGui() {
        PaginatedGui gui = Gui.paginated()
              .rows(5)
              .title(colorizeComponent("&c"+ dialogue.getName() + " &8| &cCharacterDialogue"))
              .disableAllInteractions()
              .create();

        List<GuiItem> items = new ArrayList<>();

        items.add(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(colorizeComponent("&7"))
              .asGuiItem());
        items.add(ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).name(colorizeComponent("&7"))
              .asGuiItem());

        gui.getFiller().fillBottom(items);

        gui.setItem(5, 1, GUI.previousItem
              .asGuiItem(event -> gui.previous()));

        gui.setItem(5, 2, GUI.nextItem
              .asGuiItem(event -> gui.next()));

        gui.setItem(5, 9, ItemBuilder.firework()
              .color(Color.PURPLE)
              .name(colorizeComponent("&aAdd line"))
              .enchant(Enchantment.RIPTIDE)
              .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE)
              .asGuiItem());

        this.gui = gui;
    }
}
