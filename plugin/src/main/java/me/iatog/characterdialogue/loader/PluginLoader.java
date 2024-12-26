package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod;
import me.iatog.characterdialogue.filter.ConsoleFilter;
import me.iatog.characterdialogue.util.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginLoader implements Loader {

    private final CharacterDialoguePlugin main;
    private final List<Loader> loaders;

    public PluginLoader(CharacterDialoguePlugin main) {
        this.main = main;
        this.loaders = new ArrayList<>();
    }

    @Override
    public void load() {
        ((Logger) LogManager.getRootLogger()).addFilter(new ConsoleFilter());

        loadLoaders(
              new ListenerLoader(main),
              new FileLoader(main),
              new CacheLoader(main),
              new CommandLoader(main),
              new DialogLoader(main),
              new GUILoader(main),
              new AdapterLoader(main)
        );

        main.getLogger().info(TextUtils.colorize("&a" + main.getDescription().getName() + " enabled. &7" + main.getDescription().getVersion()));
    }

    @Override
    public void unload() {
        loaders.forEach(Loader::unload);
        loaders.clear();
        NPCControlMethod.registries.forEach((_uuid, npcs) -> {
            npcs.clearAll();
        });

        main.getServices().getFollowingNPC().removeAll();
    }

    public List<Loader> getLoaders() {
        return loaders;
    }

    private void loadLoaders(Loader... loaders) {
        Arrays.asList(loaders).forEach(this::append);
    }

    private void append(Loader loader) {
        loader.load();
        this.loaders.add(loader);
        //main.getLogger().info("Loaded: " + loader.getClass().getSimpleName());
    }

}
