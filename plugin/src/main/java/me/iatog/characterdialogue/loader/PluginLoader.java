package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod;
import me.iatog.characterdialogue.filter.ConsoleFilter;
import me.iatog.characterdialogue.placeholders.CharacterDialogueExpansion;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class PluginLoader implements Loader {

    private final CharacterDialoguePlugin main;
    private final Map<Class<? extends Loader>, Loader> loaders;

    public PluginLoader(CharacterDialoguePlugin main) {
        this.main = main;
        this.loaders = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void load() {
        PluginManager manager = Bukkit.getPluginManager();
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
              new WorldGuardLoader(main),
              new ChoiceLoader(main)
        );


        if(manager.isPluginEnabled("PlaceholderAPI")) {
            new CharacterDialogueExpansion(main).register();
        }

        if(manager.isPluginEnabled("packetevents")) {
            //PacketEvents.getAPI().getEventManager().registerListener(new PlayerPacketListenerManager(main), PacketListenerPriority.NORMAL);
        }

        AdventureUtil.sendMessage(Bukkit.getConsoleSender(), "<gray>[<red>CharacterDialogue<gray>] <green>Plugin has been enabled. <gold>v" + main.getDescription().getVersion());
    }

    @Override
    public void unload() {
        loaders.forEach((clazz, loader) -> loader.unload());
        loaders.clear();

        NPCControlMethod.registries.forEach((_uuid, npcs) -> npcs.clearAll());

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
