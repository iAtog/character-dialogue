package me.iatog.characterdialogue.dialogs.method.talk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public record TalkContext(UUID uuid, String text, String npcName) {

    public Player player() {
        return Bukkit.getPlayer(uuid);
    }

}
