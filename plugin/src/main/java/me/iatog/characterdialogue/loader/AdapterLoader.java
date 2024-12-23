package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdapterManager;

public class AdapterLoader implements Loader {

    private final CharacterDialoguePlugin main;

    public AdapterLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        AdapterManager manager = main.getAdapterManager();
        manager.detectAdapter();

        if(manager.getAdapter() == null) {
            main.getLogger().severe("A compatible NPC plugin has not been found, this may cause problems.");
            return;
        }
    }
}
