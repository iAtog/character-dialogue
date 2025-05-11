package me.iatog.characterdialogue.core.plugin;

import me.iatog.characterdialogue.core.module.PluginModule;
import org.bukkit.plugin.java.JavaPlugin;

public class CorePlugin extends JavaPlugin {

    private PluginModule module;

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

    // Funciones: api, adapter, y ya
}
