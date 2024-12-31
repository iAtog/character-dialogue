package me.iatog.characterdialogue.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final List<String> readedDialogs;

    private boolean removeEffect;
    private float lastSpeed;

    public PlayerData(UUID uuid, List<String> readedDialogs, boolean removeEffect, float lastSpeed) {
        this.uuid = uuid;
        this.readedDialogs = readedDialogs;
        this.removeEffect = removeEffect;
        this.lastSpeed = lastSpeed;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean getRemoveEffect() {
        return removeEffect;
    }

    public void setRemoveEffect(boolean remove) {
        this.removeEffect = remove;
    }

    public double getLastSpeed() {
        return lastSpeed;
    }

    public void setLastSpeed(float speed) {
        this.lastSpeed = speed;
    }

    public List<String> getReadedDialogs() {
        return readedDialogs;
    }

}