package me.iatog.characterdialogue.api.events;

import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DialogueFinishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final DialogSession session;
    private final Player player;

    public DialogueFinishEvent(Player player, DialogSession session) {
        this.player = player;
        this.session = session;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public DialogSession getSession() {
        return session;
    }

    public Player getPlayer() {
        return player;
    }

    @NotNull
    public final HandlerList getHandlers() {
        return handlers;
    }
}
