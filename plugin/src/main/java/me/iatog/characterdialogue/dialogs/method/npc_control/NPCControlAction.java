package me.iatog.characterdialogue.dialogs.method.npc_control;

import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;

public interface NPCControlAction {
    void execute(ActionData ctx);

    String getName();
}
