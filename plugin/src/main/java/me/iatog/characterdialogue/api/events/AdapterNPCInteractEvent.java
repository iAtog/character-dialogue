package me.iatog.characterdialogue.api.events;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.enums.ClickType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AdapterNPCInteractEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final AdaptedNPC npc;
    private final ClickType clickType;
    private boolean cancelled;

    public AdapterNPCInteractEvent(Player player, AdaptedNPC npc, ClickType clickType) {
        super(player);
        this.npc = npc;
        this.clickType = clickType;
    }

    public AdaptedNPC getNPC() {
        return npc;
    }

    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
