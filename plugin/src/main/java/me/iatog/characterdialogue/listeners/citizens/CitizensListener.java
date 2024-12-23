package me.iatog.characterdialogue.listeners.citizens;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdapterManager;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import me.iatog.characterdialogue.enums.ClickType;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {

    private final CharacterDialoguePlugin main;

    public CitizensListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    public void callEvent(NPCClickEvent event, ClickType clickType) {
        AdapterManager manager = main.getAdapterManager();
        NPCAdapter<NPC> adapter = main.getAdapter();

        manager.handleInteractEvent(event.getClicker(), adapter.adapt(event.getNPC()), clickType);

        //event.setCancelled(cancelled);
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        callEvent(event, ClickType.RIGHT);
    }

    @EventHandler
    public void onNPCLeftClick(NPCLeftClickEvent event) {
        callEvent(event, ClickType.LEFT);
    }

    @EventHandler
    public void onNPCSpawn(NPCSpawnEvent event) {
        NPCAdapter<NPC> adapter = main.getAdapter();
        AdapterManager manager = main.getAdapterManager();

        manager.handleSpawnEvent(adapter.adapt(event.getNPC()), event.getLocation());
    }
}
