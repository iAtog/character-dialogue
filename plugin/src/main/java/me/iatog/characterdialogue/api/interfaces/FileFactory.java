package me.iatog.characterdialogue.api.interfaces;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.IOException;

public interface FileFactory {
    YamlDocument getConfig();

    //YamlFile getDialogs();
    YamlDocument getLanguage();

    YamlDocument getPlayerCache();

    YamlDocument getChoicesFile();

    YamlDocument getItems();

    void reload() throws IOException;
}
