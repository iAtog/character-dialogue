package me.iatog.characterdialogue.api.interfaces;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.IOException;

public interface FileFactory {
    YamlDocument getConfig();

    YamlDocument getLanguage();

    YamlDocument getItems();

    void reload() throws IOException;
}
