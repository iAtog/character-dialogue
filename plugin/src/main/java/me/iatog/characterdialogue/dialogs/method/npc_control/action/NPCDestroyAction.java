package me.iatog.characterdialogue.dialogs.method.npc_control.action;

import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlAction;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;

import static me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod.registries;

public class NPCDestroyAction implements NPCControlAction {
    @Override
    public void execute(ActionData ctx) {
        if (registries.containsKey(ctx.player().getUniqueId())) {
            ctx.util().removeRegistered(ctx.player(), ctx.targetNPC());
            ctx.context().next();
        }
    }

    @Override
    public String getName() {
        return "destroy";
    }
}
