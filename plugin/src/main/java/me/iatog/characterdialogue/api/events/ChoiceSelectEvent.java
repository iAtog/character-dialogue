package me.iatog.characterdialogue.api.events;

import me.iatog.characterdialogue.dialogs.Choice;
import me.iatog.characterdialogue.session.ChoiceSession;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChoiceSelectEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    /*private DialogChoice choice;*/
    private UUID uuid;
    private ChoiceSession session;
    private Choice choice;

    public ChoiceSelectEvent(Player player, UUID uuid, Choice choice, ChoiceSession session) {
        super(player);
        this.uuid = uuid;
        this.choice = choice;
        this.session = session;
    }
	/*
	public DialogChoice getChoice() {
		return choice;
	}*/

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UUID getChoiceUniqueId() {
        return uuid;
    }

    public Choice getChoice() {
        return choice;
    }

    public ChoiceSession getSession() {
        return session;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @NotNull
    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }


    /**
     * Check if the event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set event cancelled
     */
    @Override
    public void setCancelled(boolean arg0) {
        this.cancelled = arg0;
    }
}
