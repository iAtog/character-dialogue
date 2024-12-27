package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.gui.MainGUI;
import me.iatog.characterdialogue.gui.dialogue.DialoguesGUI;
import me.iatog.characterdialogue.gui.item.ItemGui;

public class GUILoader implements Loader {

    private final CharacterDialoguePlugin main;

    public GUILoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        main.getGUIFactory().registerGui(
              new MainGUI(main),
              new DialoguesGUI(main),
              new ItemGui(main)
        );
    }
}
