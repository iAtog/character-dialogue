package me.iatog.characterdialogue.adapter;

import me.iatog.characterdialogue.adapter.citizens.CitizensAdapter;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.fancynpcs.FancyNPCsAdapter;
import me.iatog.characterdialogue.adapter.znpcsplus.ZNPCsAdapter;
import me.iatog.characterdialogue.api.events.AdapterNPCInteractEvent;
import me.iatog.characterdialogue.api.events.AdapterNPCSpawnEvent;
import me.iatog.characterdialogue.enums.ClickType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AdapterManager {

    private final CharacterDialoguePlugin main;
    private NPCAdapter<?> adapter;

    public AdapterManager(CharacterDialoguePlugin main) {
        this.main = main;
    }

    public void setAdapter(NPCAdapter<?> adapter) {
        this.adapter = adapter;
    }

    public NPCAdapter<?> getAdapter() {
        return adapter;
    }

    public void detectAdapter() {
        if(Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            this.adapter = new CitizensAdapter();
        } else if(Bukkit.getPluginManager().isPluginEnabled("ZNPCsPlus")) {
            this.adapter = new ZNPCsAdapter();
        } else if(Bukkit.getPluginManager().isPluginEnabled("FancyNpcs")) {
            this.adapter = new FancyNPCsAdapter();
        }

        if(this.adapter != null) {
            this.adapter.registerEvents(main);
            this.adapter.loadNPCs();
            main.getLogger().info("Using " + this.adapter.getName() + "!");
        }
    }

    public void handleInteractEvent(Player player, AdaptedNPC npc, ClickType clickType) {
        Bukkit.getScheduler().runTask(main, () -> {
            AdapterNPCInteractEvent adapterEvent = new AdapterNPCInteractEvent(player, npc, clickType);

            Bukkit.getPluginManager().callEvent(adapterEvent);
        });
    }

    public void handleSpawnEvent(AdaptedNPC npc, Location location) {
        Bukkit.getScheduler().runTask(main, () -> {
            AdapterNPCSpawnEvent spawnEvent = new AdapterNPCSpawnEvent(
                  npc,
                  location
            );

            Bukkit.getPluginManager().callEvent(spawnEvent);
        });
    }

}
