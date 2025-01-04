package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod;
import me.iatog.characterdialogue.filter.ConsoleFilter;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;

import java.util.*;

public class PluginLoader implements Loader {

    private final CharacterDialoguePlugin main;
    private final Map<Class<? extends Loader>, Loader> loaders;

    public PluginLoader(CharacterDialoguePlugin main) {
        this.main = main;
        this.loaders = new HashMap<>();
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
              new AdapterLoader(main),
              new UpdateLoader(main),
              new WorldGuardLoader(main)
        );

        AdventureUtil.sendMessage(Bukkit.getConsoleSender(), "<red>[CharacterDialogue] <green>Plugin enabled. <gold>v" + main.getDescription().getVersion());
    }

    @Override
    public void unload() {
        loaders.forEach((clazz, loader) -> {
            loader.unload();
        });
        loaders.clear();
        NPCControlMethod.registries.forEach((_uuid, npcs) -> {
            npcs.clearAll();
        });

        main.getServices().getPlayerDataDatabase().saveAll();

        main.getServices().getFollowingNPC().removeAll();
    }

    public Map<Class<? extends Loader>, Loader> getLoaders() {
        return loaders;
    }

    private void loadLoaders(Loader... loaders) {
        Arrays.asList(loaders).forEach(this::append);
    }

    private void append(Loader loader) {
        loader.load();
        this.loaders.put(loader.getClass(), loader);
    }

}
