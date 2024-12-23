package me.iatog.characterdialogue.hook.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPIHook {

    public String translatePlaceHolders(Player player, String toTranslate) {
        return PlaceholderAPI.setPlaceholders(player, toTranslate);
    }

}
