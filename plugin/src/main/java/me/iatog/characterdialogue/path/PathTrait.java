package me.iatog.characterdialogue.path;

import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class PathTrait extends Trait {

    private int index = 0;
    private List<RecordLocation> paths;

    public PathTrait() {
        super("path_trait");
    }

    @Override
    public void run() {
        if(paths != null) {
            if (index == paths.size()) {
                paths = null;
                index = 0;
                return;
            }

            RecordLocation path = paths.get(index);
            Location location = path.toLocation();

            npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            npc.getEntity().setSneaking(path.isSneaking());
            index++;
        }
    }

    public void setPaths(List<RecordLocation> paths) {
        this.index = 0;
        this.paths = paths;
    }
}
