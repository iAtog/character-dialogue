package me.iatog.characterdialogue.api.events;

import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class DialogueStartEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final DialogSession session;

    public DialogueStartEvent(Player player, DialogSession session) {
        super(player);
        this.session = session;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public DialogSession getSession() {
        return session;
    }

    public final @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
