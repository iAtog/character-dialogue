package me.iatog.characterdialogue.api.events;

import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.enums.ClickType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ExecuteMethodEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final DialogMethod<? extends JavaPlugin> method;
    private final ClickType clickType;
    private final int npcId;
    private final String dialogName;
    private boolean cancelled;

    public ExecuteMethodEvent(Player player, DialogMethod<? extends JavaPlugin> method, ClickType clickType, int npcId, String dialogName) {
        super(player);
        this.method = method;
        this.clickType = clickType;
        this.npcId = npcId;
        this.dialogName = dialogName;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * get the method when it is used
     *
     * @return the method
     */
    public DialogMethod<? extends JavaPlugin> getMethod() {
        return method;
    }

    public ClickType getClick() {
        return clickType;
    }

    public int getNPCId() {
        return npcId;
    }

    public String getDialogName() {
        return dialogName;
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
