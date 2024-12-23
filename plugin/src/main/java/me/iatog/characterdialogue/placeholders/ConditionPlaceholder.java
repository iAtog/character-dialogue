package me.iatog.characterdialogue.placeholders;

import org.bukkit.entity.Player;

// Purpose: add custom placeholders to check for certain very specific things
public interface ConditionPlaceholder {
    String getValue(Player player);

    String getPlaceholderName();
}
