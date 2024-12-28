package me.iatog.characterdialogue.dialogs.method.npc_control;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ControlData;
import me.iatog.characterdialogue.libraries.HologramLibrary;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ControlUtil {

    private final CharacterDialoguePlugin main;
    private final HologramLibrary hologramLibrary;

    public ControlUtil(CharacterDialoguePlugin main) {
        this.main = main;
        this.hologramLibrary = main.getApi().getHologramLibrary();
    }

    public void createClone(@NotNull MethodContext context, @NotNull AdaptedNPC npc) {
        Player player = context.getPlayer();
        MethodConfiguration configuration = context.getConfiguration();
        ControlRegistry registry = NPCControlMethod.registries.get(player.getUniqueId());
        String npcId = npc.getId();

        if(registry.isOnRegistry(npcId)) {
            context.getSession().sendDebugMessage("NPC (" +npcId+ ") is already cloned.", "FollowMethod");
            return;
        }

        Location spawnLocation;
        Location configLocation = getConfigLocation(configuration, player.getLocation());

        if(configLocation == null) {
            spawnLocation = npc.getStoredLocation();
        } else {
            spawnLocation = configLocation;
        }

        AdaptedNPC clone = npc.copy();

        clone.spawn(spawnLocation);
        ControlData controlData = registry.addNPC(npc, clone);
        controlData.setHideOriginal(true);
        hologramLibrary.hideHologram(player, npc.getId());

        clone.hideForAll();
        clone.show(player);

        npc.hide(player);

        context.getSession().sendDebugMessage("Now clone of npc is following the player", "FollowMethod");
    }

    public void removeRegistered(@NotNull Player player, AdaptedNPC original) {
        ControlRegistry registry = NPCControlMethod.registries.get(player.getUniqueId());
        ControlData data = registry.removeNPC(original);

        if(data == null) {
            return;
        }
        data.setHideOriginal(false);
        data.getOriginal().show(player);
        data.getCopy().destroy();
        hologramLibrary.showHologram(player, data.getOriginal().getId());
    }

    @Nullable
    public Location getConfigLocation(@NotNull MethodConfiguration configuration, @NotNull Location defLoc) {
        if(!configuration.contains("x") || !configuration.contains("y") || !configuration.contains("z")) {
            return null;
        }

        float x = configuration.getFloat("x");
        float y = configuration.getFloat("y");
        float z = configuration.getFloat("z");
        float yaw = configuration.getFloat("yaw", defLoc.getYaw());
        float pitch = configuration.getFloat("pitch", defLoc.getPitch());

        return new Location(defLoc.getWorld(), x, y, z, yaw, pitch);
    }

}
