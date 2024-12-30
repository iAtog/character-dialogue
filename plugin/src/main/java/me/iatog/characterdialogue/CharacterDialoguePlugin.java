package me.iatog.characterdialogue;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.adapter.AdapterManager;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import me.iatog.characterdialogue.api.CharacterDialogueAPI;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.gui.GUIFactory;
import me.iatog.characterdialogue.api.interfaces.FileFactory;
import me.iatog.characterdialogue.libraries.ApiImplementation;
import me.iatog.characterdialogue.libraries.Cache;
import me.iatog.characterdialogue.libraries.Services;
import me.iatog.characterdialogue.loader.PluginLoader;
import me.iatog.characterdialogue.path.PathStorage;
import me.iatog.characterdialogue.util.TextUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.iatog.characterdialogue.util.TextUtils.colorize;

public class CharacterDialoguePlugin extends JavaPlugin {

    private static CharacterDialoguePlugin instance;
    private PluginLoader loader;
    private FileFactory fileFactory;
    private Cache cache;
    private CharacterDialogueAPI api;
    private String defaultChannel;
    private long startup;
    private Metrics metrics;
    private GUIFactory guiFactory;
    private List<YamlDocument> dialogues;
    private PathStorage pathStorage;
    private AdapterManager adapterManager;

    private Services services;

    /**
     * I only set this method for third party plugins, I do not use this method and
     * even less abuse it. <br>
     * <br>
     * Get the main class of the CharacterDialogue.
     *
     * @return the plugin main class
     */
    public static CharacterDialoguePlugin getInstance() {
        return instance;
    }

    public void onLoad() {
        this.defaultChannel = "BungeeCord";
        this.startup = System.currentTimeMillis();
        this.dialogues = new ArrayList<>();
        this.guiFactory = new GUIFactory();
    }

    @Override
    public void onEnable() {
        instance = this;
        this.metrics = new Metrics(this, 24112);

        try {
            loadAllDialogues();
        } catch (IOException e) {
            getLogger().severe("Error loading dialogues directory");
            return;
        }

        Messenger messenger = getServer().getMessenger();
        if (! messenger.isOutgoingChannelRegistered(this, defaultChannel)) {
            messenger.registerOutgoingPluginChannel(this, defaultChannel);
        }

        this.cache = new Cache();
        this.loader = new PluginLoader(this);
        this.api = new ApiImplementation(this);
        this.pathStorage = new PathStorage(this);
        this.adapterManager = new AdapterManager(this);

        loader.load();

        this.services = new Services(this);
        getLogger().info(TextUtils.colorize("&aLoaded in " + (System.currentTimeMillis() - startup) + "ms"));
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

    public PathStorage  getPathStorage() {
        return pathStorage;
    }

    public List<YamlDocument> getAllDialogues() {
        return dialogues;
    }

    /**
     * Load all files from the folder CharacterDialogue/dialogues/
     */
    public void loadAllDialogues() throws IOException {
        String folderName = "dialogues";
        File folder = new File(this.getDataFolder() + "/" + folderName);

        if (! folder.exists()) {
            folder.mkdir();
            YamlDocument defaultDialogues = YamlDocument.create(new File(getDataFolder() + "/" + folderName + "/examples.yml"), Objects.requireNonNull(getResource("dialogues/dialogs.yml")));
            dialogues.add(defaultDialogues);
            return;
        }

        if (folder.isDirectory()) {
            clearAllDialogues();
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    if (! file.isFile()) continue;
                    YamlDocument yamlDocument = YamlDocument.create(file);

                    dialogues.add(yamlDocument);
                }
            }
        }
    }

    public void clearAllDialogues() {
        dialogues.clear();
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public GUIFactory getGUIFactory() {
        return guiFactory;
    }

    public String language(boolean prefix, String route, Object ...format) {
        YamlDocument lang = getFileFactory().getLanguage();
        String prefixed = prefix ? "&8[&cCD&8] " : "";
        String result;

        if(format.length == 0) {
            result = lang.getString(route, route);
        } else {
            result = lang.getString(route, route).formatted(format);
        }

        return TextUtils.colorize(prefixed + result);
    }

    public String language(String route, Object ...format) {
        return language(false, route, format);
    }

    public String[] languageList(String route, Object ...format) {
        YamlDocument lang = getFileFactory().getLanguage();
        return translateList(lang.getStringList(route), format).toArray(String[]::new);
    }

    @SuppressWarnings("unchecked")
    public <T> T getLoader(Class<T> loader) {
        return (T) this.loader.getLoaders().get(loader);
    }

    private List<String> translateList(List<String> list, Object ...format) {
        List<String> newList = new ArrayList<>();
        list.forEach((line) -> {
            newList.add(colorize(line.formatted(format)));
        });
        return newList;
    }

}