package me.iatog.characterdialogue.libraries;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.interfaces.FileFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileFactoryImpl implements FileFactory {

    private YamlDocument config;
    private YamlDocument lang;
    private YamlDocument playerCache;
    private YamlDocument choices;
    private YamlDocument items;

    private final CharacterDialoguePlugin main;

    public FileFactoryImpl(CharacterDialoguePlugin main) {
        this.main = main;
        try {
            YamlVersions.ConfigVersion configVersion = new YamlVersions.ConfigVersion();
            YamlVersions.LanguageVersion languageVersion = new YamlVersions.LanguageVersion();

            this.config = createYamlDocument("config.yml",
                  configVersion.getLoaderSettings(), configVersion.getUpdaterSettings());
            this.lang = createYamlDocument("language.yml",
                  languageVersion.getLoaderSettings(), languageVersion.getUpdaterSettings());
            this.playerCache = createYamlDocument("player-cache.yml");
            this.choices = createYamlDocument("choices.yml");
            this.items = YamlDocument.create(getFile("items.yml"),
                  getResource("items.yml", main), GeneralSettings.builder()
                  .setSerializer(SpigotSerializer.getInstance()).build());
        } catch (IOException e) {
            main.getLogger().severe("Plugin folder files load failed: " + e.getMessage());
        }
    }

    private YamlDocument createYamlDocument(String name, LoaderSettings loaderSettings, UpdaterSettings updaterSettings) throws IOException {
        return YamlDocument.create(getFile(name), getResource(name, main),
              GeneralSettings.DEFAULT, loaderSettings, DumperSettings.DEFAULT,
              updaterSettings);
    }

    private YamlDocument createYamlDocument(String filename) throws IOException {
        return YamlDocument.create(getFile(filename), getResource(filename, main));
    }

    private File getFile(String name) {
        return new File(main.getDataFolder() + "/" + name);
    }

    private InputStream getResource(String name, CharacterDialoguePlugin main) {
        return Objects.requireNonNull(main.getResource(name));
    }

    @Override
    public YamlDocument getConfig() {
        return config;
    }

    public YamlDocument getChoicesFile() {
        return choices;
    }

    @Override
    public YamlDocument getLanguage() {
        return lang;
    }

    @Override
    public YamlDocument getPlayerCache() {
        return playerCache;
    }

    public YamlDocument getItems() {
        return items;
    }


    @Override
    public void reload() throws IOException {
        config.reload();
        lang.reload();
        playerCache.reload();
        choices.reload();
        items.reload();
        //main.getAllDialogues().forEach(YamlFile::reload);
    }
}
