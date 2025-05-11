package me.iatog.characterdialogue.core.module;

import me.iatog.characterdialogue.core.plugin.CorePlugin;

public class CommandModule implements Module {

    private CorePlugin core;

    public CommandModule(CorePlugin core) {
        this.core = core;
    }

    @Override
    public void load() {

    }
}
