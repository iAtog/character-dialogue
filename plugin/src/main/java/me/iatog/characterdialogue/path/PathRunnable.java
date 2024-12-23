package me.iatog.characterdialogue.path;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PathRunnable extends BukkitRunnable {
    private int index = 0;
    private final List<RecordLocation> locations;
    private final AdaptedNPC npc;

    public PathRunnable(List<RecordLocation> locations, AdaptedNPC npc) {
        this.locations = locations;
        this.npc = npc;
    }

    @Override
    public void run() {
        if (index == locations.size()) {
            cancel();
            return;
        }

        RecordLocation path = locations.get(index);
        Location location = path.toLocation();

        npc.teleport(location);
        index++;
    }
}
