package me.iatog.characterdialogue.dialogs.method.npc_control;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.api.events.DialogueFinishEvent;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCControlMethod extends DialogMethod<CharacterDialoguePlugin> implements Listener {

    public static final Map<UUID, ControlRegistry> registries = new HashMap<>();

    private final ControlUtil util;

    // Custom actions | default = start
    // POSSIBILITIES:
    // npc_control{action=start, npcId=18}
    // npc_control{action=start, x=0, y=0, z=0, yaw=0, pitch=0}
    // npc_control{action=follow, npcId=18}
    // npc_control{action=unfollow, npcId=18}
    // npc_control{action=pose, x=0, y=0, z=0, yaw=0, pitch=0, npcId=18}
    // npc_control{action=move_to, x=0, y=0, z=0, npcId=18}
    // npc_control{action=destroy, npcId=18}
    public NPCControlMethod(CharacterDialoguePlugin main) {
        super("npc_control", main);
        this.util = new ControlUtil(main);

        addConfigurationType("action", ConfigurationType.TEXT);
        addConfigurationType("npcId", ConfigurationType.INTEGER);

        addConfigurationType("x", ConfigurationType.FLOAT);
        addConfigurationType("y", ConfigurationType.FLOAT);
        addConfigurationType("z", ConfigurationType.FLOAT);
        addConfigurationType("yaw", ConfigurationType.FLOAT);
        addConfigurationType("pitch", ConfigurationType.FLOAT);
    }

    @Override
    public void execute(MethodContext context) {
        AdaptedNPC npc = context.getNPC();
        Player player = context.getPlayer();
        MethodConfiguration configuration = context.getConfiguration();
        String action = configuration.getString("action", "start").toUpperCase();
        String npcId = configuration.getString("npcId", npc.getId());

        //NPC targetNpc = CitizensAPI.getNPCRegistry().getById(npcId);

        AdaptedNPC targetNpc = getProvider().getAdapter().getById(npcId);

        if(targetNpc == null) {
            getProvider().getLogger().severe("The specified npc has not been found while using npc_control method.");
            context.destroy();
            return;
        }

        context.getSession().sendDebugMessage("Executing follow method with npc: " + targetNpc.getName() + " (" + npcId + ")", "FollowMethod");

        if (!registries.containsKey(player.getUniqueId())) {
            registries.put(player.getUniqueId(), new ControlRegistry(player));
        }

        ControlAction controlAction;

        try {
            controlAction = ControlAction.valueOf(action);
        } catch(Exception ex) {
            getProvider().getLogger().warning("The '" + action +  "' action is not valid for the follow method. L" + context.getSession().getCurrentIndex());
            context.destroy();
            return;
        }

        controlAction.execute(context, util, targetNpc);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (registries.containsKey(player.getUniqueId())) {
            registries.remove(player.getUniqueId()).clearAll();
            //removeRegistered(player);
            return;
        }
    }

    //@EventHandler
    public void onDialogueEnd(DialogueFinishEvent event) {
        Player player = event.getPlayer();
        if (registries.containsKey(player.getUniqueId())) {
            //removeRegistered(player);
            return;
        }
    }

}
