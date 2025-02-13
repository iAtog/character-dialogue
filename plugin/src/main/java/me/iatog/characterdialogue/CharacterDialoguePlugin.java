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
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Server version: " + Bukkit.getVersion());

        String serverVersion = Bukkit.getVersion().split("\\(MC: ")[1].split("\\)")[0];
        serverVersionArray = serverVersion.split("\\.");

        this.audiences = BukkitAudiences.create(this);
        try {
            loadAllDialogues();
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
        String folderName = getDataFolder() + "/" + "dialogues";
        File folder = new File(folderName);

        boolean res = loadDefault(
              folder,
              folderName,
              "dialogues/examples.yml",
              "examples.yml",
              (document) -> dialogues.add(document)
        );

        if(!res) {
            clearAllDialogues();
            load(folder, (document) -> dialogues.add(document));
        }
    }

    public void loadAllChoices() {
        String folderName = getDataFolder() + "/" + "choices";
        File folder = new File(folderName);

        boolean res = loadDefault(
              folder,
              folderName,
              "choice.yml",
              "choice-example.yml",
              this::loadChoices
        );

        if(!res) {
            load(folder, this::loadChoices);
        }
    }

    private boolean loadDefault(File folder, String folderName, String defName, String finalName, Consumer<YamlDocument> action) {
        if(!folder.exists()) {
            folder.mkdir();
            try {
                YamlDocument defFile = YamlDocument.create(new File(folderName + "/" + finalName), Objects.requireNonNull(getResource(defName)));
                action.accept(defFile);
            } catch (IOException e) {
                getLogger().warning("Error loading default file: " + e.getMessage());
            }

            return true;
        }

        return false;
    }

    private void load(File folder, Consumer<YamlDocument> action) {
        if(folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    if (!file.isFile()) continue;

                    try {
                        YamlDocument yamlDocument = YamlDocument.create(file);

                        action.accept(yamlDocument);
                    } catch(IOException e) {
                        getLogger().severe("Error loading folder: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void clearAllChoices() {
        getCache().getLoadedChoices().clear();
    }

    private void loadChoices(YamlDocument document) {
        Map<String, LoadedChoice> loadedChoices = getCache().getLoadedChoices();

        for(String choiceName : document.getSection("choices").getRoutesAsStrings(false)) {
            List<ChoiceInfo> choices = new ArrayList<>();
            String msg = document.getString("choices." + choiceName + ".message", "");

            for(String option : document.getSection("choices." + choiceName).getRoutesAsStrings(false)) {
                if(option.equals("message")) {
                    continue;
                }

                Section section = document.getSection("choices." + choiceName + "." + option);
                String type = section.getString("type");
                String message = section.getString("message", "no message specified");
                String argument = section.getString("argument", "");
                ChoiceInfo info = new ChoiceInfo(option, type, message, argument);
                choices.add(info);
            }

            loadedChoices.put(choiceName, new LoadedChoice(choices, msg));
        }
    }

    public void loadRegionalDialogues() {
        YamlDocument config = getFileFactory().getConfig();

        for (String regionName : config.getSection("regional-dialogues").getRoutesAsStrings(false)) {
            Section section = config.getSection("regional-dialogues." + regionName);
            List<String> conditions = section.getStringList("conditions");
            String conditionType = section.getString("conditions-type", "all");

            if(!conditionType.equalsIgnoreCase("all") && !conditionType.equalsIgnoreCase("one")) {
                return;
            }

            ConditionType type = ConditionType.to(conditionType);
            String dialogueName = section.getString("dialogue");
            RegionalDialogue dialogue = new RegionalDialogue(type, conditions, dialogueName, regionName);

            getCache().getRegionalDialogues().put(regionName, dialogue);
        }
    }

    public void clearAllDialogues() {
        dialogues.clear();
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

    public boolean isVersionAtLeast(String requiredVersion) {
        String[] requiredVersionArray = requiredVersion.split("\\.");
        for (int i = 0; i < requiredVersionArray.length; i++) {
            int versionNumber = Integer.parseInt(serverVersionArray[i]);
            int requiredVersionNumber = Integer.parseInt(requiredVersionArray[i]);

            if (versionNumber > requiredVersionNumber) {
                return true;
            }
            else if (versionNumber < requiredVersionNumber) {
                return false;
            }
        }
        return true;
    }

}