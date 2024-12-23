package me.iatog.characterdialogue.loader;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.DialogueImpl;
import me.iatog.characterdialogue.libraries.Cache;
import me.iatog.characterdialogue.util.TextUtils;

public class DialogLoader implements Loader {

    private final CharacterDialoguePlugin main;

    public DialogLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        Cache cache = main.getCache();

        for (YamlDocument dialogueFile : main.getAllDialogues()) {
            Section section = dialogueFile.getSection("dialogue");

            if (section != null) {
                section.getRoutesAsStrings(false).forEach(name -> {
                    cache.getDialogues().put(name, new DialogueImpl(main, name, dialogueFile));
                });
            }
        }

        main.getLogger().info(TextUtils.colorize("Successfully loaded " + cache.getDialogues().size() + " dialogues."));
    }

}
