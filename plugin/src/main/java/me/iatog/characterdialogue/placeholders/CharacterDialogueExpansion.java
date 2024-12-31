package me.iatog.characterdialogue.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.ItemManager;
import me.iatog.characterdialogue.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CharacterDialogueExpansion extends PlaceholderExpansion {

    private final CharacterDialoguePlugin main;

    public CharacterDialogueExpansion(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "characterdialogue";
    }

    @Override
    public @NotNull String getAuthor() {
        return "iAtog";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    /*
     * %characterdialogue_readed:<dialogueName>%
     * %characterdialogue_readedFirstInteraction:<dialogueName>%
     *
     * %characterdialogue_readed_size:<finished/firstInteractions>%
     * %characterdialogue_readed_size:finished%
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        PlayerData data = main.getCache().getPlayerData().get(player.getUniqueId());

        if(params.startsWith("readed") || params.startsWith("readedFirstInteraction")) {
            if(params.split(":").length != 2)
                return "undefined";
            String name = params.split(":")[1];

            return main.getApi().wasReadedBy(player, name, params.startsWith("readedFirstInteraction")) ? "yes" : "no";
        } else if(params.startsWith("readed_size")) {
            if(params.split(":").length != 2)
                return "0";
            List<String> list = null;
            String param = params.split(":")[1];

            if(param.equals("finished")) {
                list = data.getFinishedDialogs();
            } else if(param.equals("firstInteractions")) {
                list = data.getFirstInteractions();
            } else {
                return "0";
            }

            return String.valueOf(list.size());
        } else if(params.startsWith("has_item")) { // %characterdialogue_has_item:itemName:amount%
            String[] split = params.split(":");
            ItemManager manager = main.getServices().getItemManager();

            if(split.length != 3 || !manager.existsItem(split[1]))
                return "undefined";

            String itemName = split[1];
            int amount = parseInt(split[2]);

            return hasItemAmount(player, manager.getItem(itemName), amount) ? "yes" : "no";
        }

        return null;
    }

    private boolean hasItemAmount(Player player, ItemStack itemStack, int amount) {
        int totalAmount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(itemStack)) {
                totalAmount += item.getAmount();
                if (totalAmount >= amount) {
                    return true;
                }
            }
        }

        return false;
    }

    private int parseInt(String integer) {
        try {
            return Integer.parseInt(integer);
        } catch(NumberFormatException ex) {
            return 1;
        }
    }

}
