package me.iatog.characterdialogue;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.iatog.characterdialogue.adapter.AdapterManager;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import me.iatog.characterdialogue.api.CharacterDialogueAPI;
import me.iatog.characterdialogue.api.dialog.RegionalDialogue;
import me.iatog.characterdialogue.dialogs.ChoiceInfo;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.LoadedChoice;
import me.iatog.characterdialogue.enums.ConditionType;
import me.iatog.characterdialogue.gui.GUIFactory;
import me.iatog.characterdialogue.api.interfaces.FileFactory;
import me.iatog.characterdialogue.libraries.ApiImplementation;
import me.iatog.characterdialogue.libraries.Cache;
import me.iatog.characterdialogue.libraries.Services;
import me.iatog.characterdialogue.loader.PluginLoader;
import me.iatog.characterdialogue.path.PathStorage;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class CharacterDialoguePlugin extends JavaPlugin {

    private Initializer initializer;

    private static CharacterDialoguePlugin instance;
    private PluginLoader loader;
    private FileFactory fileFactory;
    private Cache cache;
    private CharacterDialogueAPI api;
    private long startup;
    private GUIFactory guiFactory;
    private List<YamlDocument> dialogues;
    private PathStorage pathStorage;
    private AdapterManager adapterManager;
    private Services services;
    private BukkitAudiences audiences;

    private boolean isPaper;
    private String[] serverVersionArray;

    /**
     * bruh
     * <br>
     * Get the main class of character-dialogue.
     *
     * @return the plugin main class
     */
    public static CharacterDialoguePlugin getInstance() {
        return instance;
    }

    public void onLoad() {
        this.startup = System.currentTimeMillis();
        this.dialogues = new ArrayList<>();
        this.guiFactory = new GUIFactory();
        this.initializer = new Initializer(this);
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Server version: " + Bukkit.getVersion());

        String serverVersion = Bukkit.getVersion().split("\\(MC: ")[1].split("\\)")[0];
        serverVersionArray = serverVersion.split("\\.");

        this.audiences = BukkitAudiences.create(this);
        try {
            initializer.loadAllDialogues();
        } catch (IOException e) {
            getLogger().severe("Error loading dialogues directory");
            return;
        }

        this.cache = new Cache();
        this.loader = new PluginLoader(this);
        this.api = new ApiImplementation(this);
        this.pathStorage = new PathStorage(this);
        this.adapterManager = new AdapterManager(this);

        loader.load();

        this.services = new Services(this);

        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {}

        getLogger().info("Loaded in " + (System.currentTimeMillis() - startup) + "ms");
    }

    @Override
    public void onDisable() {
        loader.unload();
    }

    public FileFactory getFileFactory() {
        return fileFactory;
    }

    public Cache getCache() {
        return cache;
    }

    public CharacterDialogueAPI getApi() {
        return api;
    }

    public void setDefaultFileFactory(FileFactory factory) {
        this.fileFactory = factory;
    }

    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    @SuppressWarnings("unchecked")
    public <T> NPCAdapter<T> getAdapter() {
        return (NPCAdapter<T>) adapterManager.getAdapter();
    }

    public Services getServices() {
        return services;
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    /**
     * Register your own methods
     *
     * @param methods methods to register
     */
    @SafeVarargs
    public final void registerMethods(DialogMethod<? extends JavaPlugin>... methods) {
        Map<String, DialogMethod<? extends JavaPlugin>> mapMethods = getCache().getMethods();

        for (DialogMethod<? extends JavaPlugin> method : methods) {
            if (mapMethods.containsKey(method.getID().toLowerCase())) {
                continue;
            }

            mapMethods.put(method.getID().toLowerCase(), method);
            if (method instanceof Listener) {
                PluginManager pluginManager = Bukkit.getPluginManager();
                JavaPlugin provider = method.getProvider() == null ? this : method.getProvider();

                pluginManager.registerEvents((Listener) method, provider);
            }
        }
    }

    public void registerChoices(DialogChoice... choices) {
        Map<String, DialogChoice> choiceCache = getCache().getChoices();

        for (DialogChoice choice : choices) {
            if (choiceCache.containsKey(choice.getId())) {
                continue;
            }

            choiceCache.put(choice.getId(), choice);
        }
    }

    public PathStorage getPathStorage() {
        return pathStorage;
    }

    public List<YamlDocument> getAllDialogues() {
        return dialogues;
    }

    public Initializer getInitializer() {
        return initializer;
    }

    public GUIFactory getGUIFactory() {
        return guiFactory;
    }

    public String language(boolean prefix, String route, Object ...format) {
        YamlDocument lang = getFileFactory().getLanguage();
        String prefixed = prefix ? "<gray>[<red>CD<gray>] " : "";
        String result;

        if(format.length == 0) {
            result = lang.getString(route, route);
        } else {
            result = lang.getString(route, route).formatted(format);
        }

        return prefixed + result;
    }

    public String language(String route, Object ...format) {
        return language(false, route, format);
    }

    public boolean isPaper() {
        return isPaper;
    }
    public String[] getServerVersion() {
        return serverVersionArray;
    }
}