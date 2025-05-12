package me.iatog.characterdialogue.core.plugin;

import me.iatog.characterdialogue.core.module.PluginModule;
import me.iatog.characterdialogue.integration.adapter.AdapterRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public class CorePlugin extends JavaPlugin {

    private PluginModule module;
    private AdapterRegistry registry;

    @Override
    public void onEnable() {
        module = new PluginModule(this);
        module.load();
    }

    @Override
    public void onDisable() {
        module.unload();
        this.module = null;
    }

    public PluginModule getModule() {
        return module;
    }

    public AdapterRegistry getRegistry() {
        return registry;
    }

    public void useRegistry(AdapterRegistry registry) {
        if(registry == null) {
            this.registry = registry;
        }
    }
}
