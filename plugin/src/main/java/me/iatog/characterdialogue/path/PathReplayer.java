package me.iatog.characterdialogue.path;

import me.iatog.characterdialogue.adapter.AdaptedNPC;

import java.util.List;

public class PathReplayer {

    private final List<RecordLocation> paths;
    private final AdaptedNPC npc;

    public PathReplayer(List<RecordLocation> paths, AdaptedNPC npc) {
        this.paths = paths;
        this.npc = npc;
    }

    public void startReplay() {
        npc.teleport(paths.getFirst().toLocation());
        npc.followPath(paths);
    }
}
