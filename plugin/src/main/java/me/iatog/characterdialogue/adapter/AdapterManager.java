package me.iatog.characterdialogue.adapter;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.adapter.citizens.CitizensAdapter;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.fancynpcs.FancyNPCsAdapter;
import me.iatog.characterdialogue.adapter.znpcsplus.ZNPCsAdapter;
import me.iatog.characterdialogue.api.events.AdapterNPCInteractEvent;
import me.iatog.characterdialogue.api.events.AdapterNPCSpawnEvent;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.method.conditional.ConditionalMethod;
import me.iatog.characterdialogue.enums.ClickType;
import me.iatog.characterdialogue.enums.ConditionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

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

    public void handleSpawnEvent(AdaptedNPC npc, Location location, Player player) {
        Bukkit.getScheduler().runTask(main, () -> {
            AdapterNPCSpawnEvent spawnEvent = new AdapterNPCSpawnEvent(
                  npc,
                  location,
                  player
            );

            Bukkit.getPluginManager().callEvent(spawnEvent);
        });
    }

    public boolean handleHideNPCs(AdaptedNPC npc, Player player) {
        YamlDocument config = main.getFileFactory().getConfig();
        String route = "hidden-npcs." + npc.getId();

        if(player == null || !config.contains(route + ".conditions")) {
            return false;
        }

        String typeName = config.getString(route + ".type", "all");

        if(!typeName.equalsIgnoreCase("all") && !typeName.equalsIgnoreCase("one")) {
            return false;
        }

        List<String> conditions = config.getStringList(route + ".conditions");
        ConditionType type = ConditionType.to(typeName);

        return main.getApi().evaluateConditions(player, conditions, type);
    }

}
