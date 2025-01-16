package me.iatog.characterdialogue.path;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PathRunnable extends BukkitRunnable {
    private int index = 0;
    private final List<RecordLocation> locations;
    private final AdaptedNPC npc;
    private UUID viewer;

    public PathRunnable(List<RecordLocation> locations, AdaptedNPC npc, @Nullable Player viewer) {
        this.locations = locations;
        this.npc = npc;

        if(viewer != null) {
            this.viewer = viewer.getUniqueId();
        }
    }

    @Override
    public void run() {
        if (index == locations.size() || npc.isDestroyed()) {
            cancel();
            return;
        }

        RecordLocation path = locations.get(index);
        Location location = path.toLocation();

        npc.teleport(location);

        if(viewer != null) {
            npc.sneak(Bukkit.getPlayer(viewer), path.isSneaking());
        }

        index++;
    }
}
