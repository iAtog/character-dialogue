package me.iatog.characterdialogue.dialogs.method.npc_control.action;

import me.iatog.characterdialogue.dialogs.method.npc_control.ControlRegistry;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlAction;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;

import static me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod.registries;

public class NPCDestroyAction implements NPCControlAction {
    @Override
    public void execute(ActionData ctx) {
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        if (registry != null) {
            ctx.util().removeRegistered(ctx.player(), ctx.targetNPC());

            if(registry.size() == 0) {
                registries.remove(ctx.player().getUniqueId());
            }

            ctx.context().next();
        }
    }

    @Override
    public String getName() {
        return "destroy";
    }
}
