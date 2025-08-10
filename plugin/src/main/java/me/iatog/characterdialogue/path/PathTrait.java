package me.iatog.characterdialogue.path;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class PathTrait extends Trait {

    private int index = 0;
    private List<RecordLocation> paths;
    private final boolean useSneak;

    public PathTrait() {
        super("path_trait");
        useSneak = isVersionAtLeast("1.20.6");
    }

    private boolean isVersionAtLeast(String requiredVersion) {
        String[] requiredVersionArray = requiredVersion.split("\\.");
        for (int i = 0; i < requiredVersionArray.length; i++) {
            int versionNumber = Integer.parseInt(CharacterDialoguePlugin.getInstance().getServerVersion()[i]);
            int requiredVersionNumber = Integer.parseInt(requiredVersionArray[i]);

            if (versionNumber > requiredVersionNumber) {
                return true;
            }
            else if (versionNumber < requiredVersionNumber) {
                return false;
            }
        }
        return true;
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
            if(CharacterDialoguePlugin.getInstance().isPaper() && useSneak) {
                npc.getEntity().setSneaking(path.isSneaking());
            }

            index++;
        }
    }

    public void setPaths(List<RecordLocation> paths) {
        this.index = 0;
        this.paths = paths;
    }
}
