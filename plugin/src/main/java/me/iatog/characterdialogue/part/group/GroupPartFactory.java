package me.iatog.characterdialogue.part.group;

import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.part.CommandPart;
import me.iatog.characterdialogue.CharacterDialoguePlugin;

import java.lang.annotation.Annotation;
import java.util.List;

public class GroupPartFactory implements PartFactory {
    private final CharacterDialoguePlugin main;

    public GroupPartFactory(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public CommandPart createPart(String s, List<? extends Annotation> list) {
        return new GroupPart(main, s);
    }
}
