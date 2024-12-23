package me.iatog.characterdialogue.dialogs;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.Dialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group {
    private final YamlDocument document;
    private final String name;
    private final List<Dialogue> dialogues;

    public Group(CharacterDialoguePlugin main, YamlDocument document) {
        Section section = document.getSection("dialogue");

        this.document = document;
        this.dialogues = new ArrayList<>();
        this.name = Objects.requireNonNull(document.getFile()).getName().split("\\.")[0];

        if (section != null) {
            for (String name : section.getRoutesAsStrings(false)) {
                this.dialogues.add(main.getCache().getDialogues().get(name));
            }
        }
    }

    public YamlDocument getDocument() {
        return document;
    }

    public String getName() {
        return name;
    }

    public List<Dialogue> getDialogues() {
        return dialogues;
    }
}
