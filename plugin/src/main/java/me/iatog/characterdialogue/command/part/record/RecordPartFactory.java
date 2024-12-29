package me.iatog.characterdialogue.command.part.record;

import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.part.CommandPart;
import me.iatog.characterdialogue.CharacterDialoguePlugin;

import java.lang.annotation.Annotation;
import java.util.List;

public class RecordPartFactory implements PartFactory {
    private final CharacterDialoguePlugin main;

    public RecordPartFactory(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public CommandPart createPart(String name, List<? extends Annotation> modifiers) {
        return new RecordPart(name, main);
    }
}
