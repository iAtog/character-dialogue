package me.iatog.characterdialogue.path;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PathReplayer {

    private final List<RecordLocation> paths;
    private final AdaptedNPC npc;

    public PathReplayer(List<RecordLocation> paths, AdaptedNPC npc) {
        this.paths = paths;
        this.npc = npc;
    }

    public void startReplay(@Nullable Player viewer) {
        npc.teleport(paths.getFirst().toLocation());
        npc.followPath(paths, viewer);
    }
}
