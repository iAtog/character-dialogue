package me.iatog.characterdialogue.listeners.znpcsplus;

import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import lol.pyr.znpcsplus.api.event.NpcSpawnEvent;
import lol.pyr.znpcsplus.api.interaction.InteractionType;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.adapter.AdapterManager;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import me.iatog.characterdialogue.api.events.AdapterNPCSpawnEvent;
import me.iatog.characterdialogue.dialogs.method.npc_control.ControlRegistry;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod;
import me.iatog.characterdialogue.enums.ClickType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

public class ZNPCsPlusListener implements Listener {

    private final CharacterDialoguePlugin main;

    public ZNPCsPlusListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(NpcInteractEvent event) {
        AdapterManager manager = main.getAdapterManager();
        NPCAdapter<NpcEntry> adapter = main.getAdapter();
        AdaptedNPC npc = adapter.adapt(event.getEntry());

        manager.handleInteractEvent(
              event.getPlayer(),
              npc,
              parseInteractionType(event.getClickType())
        );

        //event.setCancelled(cancelled);

    }

    // HANDLING 'npc_control' CLONES
    @EventHandler
    public void onSpawn(NpcSpawnEvent event) {
        NpcEntry npc = event.getEntry();
        Player player = event.getPlayer();
        Map<UUID, ControlRegistry> registries = NPCControlMethod.registries;

        if(npc.getId().endsWith("_cloned")) {
            ControlRegistry registry = registries.get(player.getUniqueId());
            if(registry != null && registry.findCopy(npc.getId()) != null) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handleSpawn(NpcSpawnEvent event) {
        AdaptedNPC npc = main.getAdapter().adapt(event.getNpc());
        boolean result = main.getAdapterManager().handleHideNPCs(npc, event.getPlayer());

        if(result) {
            event.setCancelled(true);
        }
    }

    private ClickType parseInteractionType(InteractionType type) {
        if(type == InteractionType.RIGHT_CLICK) {
            return ClickType.RIGHT;
        } else if(type == InteractionType.LEFT_CLICK) {
            return ClickType.LEFT;
        } else {
            return ClickType.ALL;
        }
    }

}
