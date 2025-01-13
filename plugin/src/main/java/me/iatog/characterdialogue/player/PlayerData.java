package me.iatog.characterdialogue.player;

import me.iatog.characterdialogue.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final List<String> finishedDialogs;
    private final List<String> firstInteractions;
    private final List<Component> cachedMessages;

    private boolean removeEffect;
    private float lastSpeed;

    public PlayerData(UUID uuid, List<String> finishedDialogs, List<String> firstInteractions, boolean removeEffect, float lastSpeed) {
        this.uuid = uuid;
        this.finishedDialogs = finishedDialogs;
        this.firstInteractions = firstInteractions;
        this.removeEffect = removeEffect;
        this.lastSpeed = lastSpeed;
        this.cachedMessages = new ArrayList<>();
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

    public List<String> getFinishedDialogs() {
        return finishedDialogs;
    }

    public List<String> getFirstInteractions() {
        return firstInteractions;
    }

    public List<String> getDialogues(boolean firstInteraction) {
        if(firstInteraction) {
            return getFirstInteractions();
        } else {
            return getFinishedDialogs();
        }
    }

    public void addMessage(Component message) {
        cachedMessages.add(message);
    }

    public void addMessage(String text) {
        cachedMessages.add(Component.text(text));
    }

    public boolean hasMessages() {
        return !cachedMessages.isEmpty();
    }

    public void sendMessages() {
        if(!hasMessages()) return;

        for (Component cachedMessage : cachedMessages) {
            AdventureUtil.sendMessage(getPlayer(), cachedMessage);
        }

        cachedMessages.clear();
    }

}
