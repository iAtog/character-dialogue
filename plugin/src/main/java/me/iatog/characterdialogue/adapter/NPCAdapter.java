package me.iatog.characterdialogue.adapter;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class NPCAdapter<T> {

    private List<String> npcs;

    public abstract AdaptedNPC adapt(T npc);

    public abstract AdaptedNPC getById(String id);

    public abstract List<String> getNPCs();

    public abstract void registerEvents(JavaPlugin main);

    public abstract String getName();

    protected void registerListener(JavaPlugin main, Listener ...listeners) {
        for(Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, main);
        }
    }

    public void loadNPCs() {
        npcs = getNPCs();
    }

    public List<String> getInMemoryNPCs() {
        return npcs;
    }

}