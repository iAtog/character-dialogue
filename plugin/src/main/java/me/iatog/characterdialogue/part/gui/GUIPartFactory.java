package me.iatog.characterdialogue.part.gui;

import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.part.CommandPart;
import me.iatog.characterdialogue.CharacterDialoguePlugin;

import java.lang.annotation.Annotation;
import java.util.List;

public class GUIPartFactory implements PartFactory {
    private final CharacterDialoguePlugin main;

    public GUIPartFactory(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public CommandPart createPart(String s, List<? extends Annotation> list) {
        return new GUIPart(main, s);
    }
}
