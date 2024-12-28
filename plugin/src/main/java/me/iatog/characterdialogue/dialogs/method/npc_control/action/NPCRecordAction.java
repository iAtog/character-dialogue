package me.iatog.characterdialogue.dialogs.method.npc_control.action;

import me.iatog.characterdialogue.dialogs.method.npc_control.ControlRegistry;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlAction;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ControlData;
import me.iatog.characterdialogue.path.PathReplayer;
import me.iatog.characterdialogue.path.RecordLocation;

import java.util.List;

import static me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod.registries;

public class NPCRecordAction implements NPCControlAction {
    @Override
    public void execute(ActionData ctx) {
        String recordName = ctx.context().getConfiguration().getString("record");
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData data = registry.get(ctx.targetNPC().getId());

        if(recordName == null || recordName.isEmpty()) {
            ctx.plugin().getLogger().warning("No record name specified in record action.");
            ctx.context().next();
            return;
        }

        List<RecordLocation> locations = ctx.plugin().getPathStorage().getPath(recordName);

        if(locations == null) {
            ctx.plugin().getLogger().warning("Record '" + recordName + "' not found.");
            ctx.context().next();
            return;
        }

        if(data != null) {
            data.getCopy().unfollow(ctx.player());
            PathReplayer replayer = new PathReplayer(locations, data.getCopy());
            replayer.startReplay();
        } else {
            ctx.plugin().getLogger().warning("An attempt was made to play a recording but the cloned npc was not found.");
        }

        ctx.context().next();
    }

    @Override
    public String getName() {
        return "record";
    }
}
