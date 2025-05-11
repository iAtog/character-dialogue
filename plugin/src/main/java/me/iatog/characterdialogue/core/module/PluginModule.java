package me.iatog.characterdialogue.core.module;

import me.iatog.characterdialogue.core.plugin.CorePlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginModule implements Module {

    private final List<Module> modules = new ArrayList<>();

    private final CorePlugin core;

    public PluginModule(CorePlugin core) {
        this.core = core;
    }

    @Override
    public void load() {
        Collections.addAll(modules,
              new CommandModule(core));

        modules.forEach(Module::load);
    }

    @Override
    public void unload() {
        modules.forEach(Module::unload);
        modules.clear();
    }
}
