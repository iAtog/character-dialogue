package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.UpdateChecker;

import java.util.Optional;

public class UpdateLoader implements Loader {

    private final CharacterDialoguePlugin main;

    public UpdateLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void load() {
        UpdateChecker checker = new UpdateChecker(main, "https://raw.githubusercontent.com/iAtog/character-dialogue/master/gradle.properties");
        Optional<String> version = checker.checkForUpdates(main.getDescription().getVersion());

        if(version.isPresent()) {
            main.getLogger().warning("New version of CharacterDialogue is available: v" + version.get());
            main.getLogger().warning("Download it: https://github.com/iAtog/character-dialogue/releases");
        } else {
            main.getLogger().info("Plugin is up-to-date!");
        }
    }
}
