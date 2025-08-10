package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;

public class ChoiceLoader implements Loader {

    private final CharacterDialoguePlugin main;

    public ChoiceLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        main.getInitializer().loadAllChoices();
    }

    @Override
    public void unload() {
        main.getInitializer().clearAllChoices();
    }
}
