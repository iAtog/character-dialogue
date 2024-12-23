package me.iatog.characterdialogue.part.method;

import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.part.CommandPart;
import me.iatog.characterdialogue.CharacterDialoguePlugin;

import java.lang.annotation.Annotation;
import java.util.List;

public class MethodPartFactory implements PartFactory {

    private final CharacterDialoguePlugin main;

    public MethodPartFactory(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public CommandPart createPart(String name, List<? extends Annotation> modifiers) {
        return new MethodPart(name, main);
    }
}
