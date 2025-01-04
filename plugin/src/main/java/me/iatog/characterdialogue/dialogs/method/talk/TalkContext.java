package me.iatog.characterdialogue.dialogs.method.talk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public record TalkContext(UUID uuid, String text, String npcName, String color) {

    public Player player() {
        return Bukkit.getPlayer(uuid);
    }

}
