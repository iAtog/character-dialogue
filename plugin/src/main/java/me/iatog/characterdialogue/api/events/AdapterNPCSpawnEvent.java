package me.iatog.characterdialogue.api.events;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AdapterNPCSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final AdaptedNPC npc;
    private final Location location;

    public AdapterNPCSpawnEvent(AdaptedNPC npc, Location location) {
        this.npc = npc;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public AdaptedNPC getNPC() {
        return npc;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
