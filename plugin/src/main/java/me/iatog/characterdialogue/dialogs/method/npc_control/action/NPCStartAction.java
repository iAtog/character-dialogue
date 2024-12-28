package me.iatog.characterdialogue.dialogs.method.npc_control.action;

import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlAction;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;

public class NPCStartAction implements NPCControlAction {

    @Override
    public void execute(ActionData ctx) {
        ctx.util().createClone(ctx.context(), ctx.targetNPC());
        ctx.context().next();
    }

    @Override
    public String getName() {
        return "start";
    }
}
