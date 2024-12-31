package me.iatog.characterdialogue.dialogs.method.npc_control.action;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.method.npc_control.ControlRegistry;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlAction;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ControlData;
import org.bukkit.Location;

import static me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod.registries;

public class NPCTeleportAction implements NPCControlAction {
    @Override
    public void execute(ActionData ctx) {
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData data = registry.get(ctx.targetNPC().getId());
        MethodConfiguration configuration = ctx.context().getConfiguration();

        if(data != null) {
            AdaptedNPC clone = data.getCopy();
            Location newLocation = ctx.util().getConfigLocation(configuration, clone.getStoredLocation());

            if(newLocation == null) {
                ctx.plugin().getLogger().warning("Invalid coordinates specified in teleport action.");
            } else {
                clone.unfollow(ctx.player());
                clone.teleport(newLocation);

                if(configuration.getBoolean("lookPlayer", false)) {
                    clone.faceLocation(ctx.player());
                }
            }
        }

        ctx.context().next();
    }

    @Override
    public String getName() {
        return "teleport";
    }
}
