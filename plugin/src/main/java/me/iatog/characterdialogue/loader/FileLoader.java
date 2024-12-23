package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.FileFactoryImpl;

public class FileLoader implements Loader {

    private CharacterDialoguePlugin main;

    public FileLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        main.setDefaultFileFactory(new FileFactoryImpl(main));
    }

}
