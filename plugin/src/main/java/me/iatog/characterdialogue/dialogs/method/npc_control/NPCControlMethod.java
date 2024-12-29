package me.iatog.characterdialogue.dialogs.method.npc_control;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.dialogs.method.npc_control.action.*;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCControlMethod extends DialogMethod<CharacterDialoguePlugin> implements Listener {

    public static final Map<UUID, ControlRegistry> registries = new HashMap<>();
    private final Map<String, NPCControlAction> actions;
    private final ControlUtil util;

    // Custom actions | default = start
    // POSSIBILITIES:
    // npc_control{action=start, npcId=18}
    // npc_control{action=start, x=0, y=0, z=0, yaw=0, pitch=0}
    // npc_control{action=follow, npcId=18}
    // npc_control{action=unfollow, npcId=18}
    // npc_control{action=teleport, x=0, y=0, z=0, yaw=0, pitch=0, npcId=18}
    // npc_control{action=destroy, npcId=18}
    public NPCControlMethod(CharacterDialoguePlugin main) {
        super("npc_control", main);
        this.util = new ControlUtil(main);
        this.actions = new HashMap<>();

        addConfigurationType("action", ConfigurationType.TEXT);
        addConfigurationType("npcId", ConfigurationType.INTEGER);

        addConfigurationType("x", ConfigurationType.FLOAT);
        addConfigurationType("y", ConfigurationType.FLOAT);
        addConfigurationType("z", ConfigurationType.FLOAT);
        addConfigurationType("yaw", ConfigurationType.FLOAT);
        addConfigurationType("pitch", ConfigurationType.FLOAT);


        addConfigurationType("lookPlayer", ConfigurationType.BOOLEAN);
        addConfigurationType("item", ConfigurationType.TEXT);
        addConfigurationType("material", ConfigurationType.TEXT);

        setDescription("Control an NPC by creating a clone");

        // Registering actions
        registerActions(
              new NPCStartAction(),
              new NPCDestroyAction(),
              new NPCFollowAction(),
              new NPCUnFollowAction(),
              new NPCTeleportAction(),
              new NPCRecordAction(),
              new NPCEquipAction()
        );
    }

    @Override
    public void execute(MethodContext context) {
        AdaptedNPC npc = context.getNPC();
        Player player = context.getPlayer();
        MethodConfiguration configuration = context.getConfiguration();
        String action = configuration.getString("action");
        String npcId = configuration.getString("npcId");

        if(action == null) {
            getProvider().getLogger().severe("Error while executing npc_control method: no action specified.");
            context.destroy();
            return;
        }

        if(!actions.containsKey(action.toLowerCase())) {
            getProvider().getLogger().severe("Error while executing npc_control method: action '" + action + "' not found.");
            context.destroy();
            return;
        }

        if(npcId == null && npc != null) {
            npcId = npc.getId();
        }

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

        NPCControlAction controlAction = actions.get(action.toLowerCase());
        ActionData data = new ActionData(context, util, targetNpc);

        controlAction.execute(data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (registries.containsKey(player.getUniqueId())) {
            registries.remove(player.getUniqueId()).clearAll();
            return;
        }
    }

    public Map<String, NPCControlAction> getActions() {
        return Collections.unmodifiableMap(actions);
    }

    public void registerAction(NPCControlAction action) {
        actions.put(action.getName().toLowerCase(), action);
    }

    public void registerActions(NPCControlAction ...actions) {
        for(NPCControlAction action : actions) {
            registerAction(action);
        }
    }

    public void unregisterAction(String name) {
        actions.remove(name.toLowerCase());
    }
}
