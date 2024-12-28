package me.iatog.characterdialogue.dialogs.method.npc_control.action;

import me.iatog.characterdialogue.dialogs.method.npc_control.ControlRegistry;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlAction;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ControlData;

import static me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod.registries;

public class NPCUnFollowAction implements NPCControlAction {
    @Override
    public void execute(ActionData ctx) {
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData playerRegistry = registry.get(ctx.targetNPC().getId());

        if(playerRegistry != null) {
            playerRegistry.getCopy().unfollow(ctx.player());
        }

        ctx.context().next();
    }

    @Override
    public String getName() {
        return "unfollow";
    }
}
