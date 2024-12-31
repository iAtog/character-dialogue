package me.iatog.characterdialogue.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        /*
         * %characterdialogue_readed_<dialogueName>%
         */

        if(params.startsWith("readed")) {
            PlayerData data = main.getCache().getPlayerData().get(player.getUniqueId());

            if(params.split("_").length != 2)
                return "undefined";

            return data.getReadedDialogs().contains(params.split("_")[1]) ? "yes" : "no";
        }

        return null;
    }

}
