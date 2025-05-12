package me.iatog.characterdialogue.integration.adapter;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class AdapterRegistry {
    private final Map<String, AdapterFactory> adapterFactories = new HashMap<>();
    private final Map<String, NPCAdapter<?>> loadedAdapters = new HashMap<>();

    public void registerAdapterFactory(String pluginName, AdapterFactory factory) {
        adapterFactories.put(pluginName, factory);
    }

    public void loadAvailableAdapters() {
        for (Map.Entry<String, AdapterFactory> entry : adapterFactories.entrySet()) {
            String pluginName = entry.getKey();
            if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                try {
                    NPCAdapter<?> adapter = entry.getValue().createAdapter();
                    loadedAdapters.put(pluginName, adapter);
                } catch (Exception e) {

                }
            }
        }
    }

    public NPCAdapter<?> getPreferredAdapter() {
        if (loadedAdapters.isEmpty()) {
            return null;
        }
        return loadedAdapters.values().iterator().next();
    }
}
