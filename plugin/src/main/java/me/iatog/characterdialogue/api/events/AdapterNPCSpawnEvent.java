package me.iatog.characterdialogue.api.events;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AdapterNPCSpawnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final AdaptedNPC npc;
    private final Location location;
    private boolean cancelled;
    private final Player player;

    public AdapterNPCSpawnEvent(@NotNull AdaptedNPC npc, @NotNull Location location, @Nullable Player player) {
        this.player = player;
        this.npc = npc;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public AdaptedNPC getNPC() {
        return npc;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
