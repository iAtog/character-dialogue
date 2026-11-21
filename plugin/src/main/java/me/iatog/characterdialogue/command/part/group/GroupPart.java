package me.iatog.characterdialogue.command.part.group;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.Group;

import java.util.*;

public class GroupPart implements ArgumentPart {
    private final CharacterDialoguePlugin main;
    private final String name;

    public GroupPart(CharacterDialoguePlugin main, String name) {
        this.main = main;
        this.name = name;
    }

    @Override
    public List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack) {
        String next = stack.hasNext() ? stack.next() : null;

        if (next == null) {
            return Collections.emptyList();
        }

        List<String> suggest = new ArrayList<>();

        for (YamlDocument document : main.getAllDialogues()) {
            String name = Objects.requireNonNull(document.getFile()).getName().split("\\.")[0].toLowerCase(Locale.ROOT);
            if (next.isEmpty() || name.toLowerCase(Locale.ROOT).startsWith(next.toLowerCase(Locale.ROOT))) {
                suggest.add(name.toLowerCase(Locale.ROOT));
            }
        }

        return suggest;
    }

    @Override
    public List<Group> parseValue(CommandContext context, ArgumentStack stack, CommandPart caller) throws ArgumentParseException {
        String group = stack.next().toLowerCase(Locale.ROOT);

        YamlDocument doc = null;

        for (YamlDocument document : main.getAllDialogues()) {
            String name = Objects.requireNonNull(document.getFile()).getName().split("\\.")[0].toLowerCase(Locale.ROOT);
            if (name.toLowerCase(Locale.ROOT).startsWith(group.toLowerCase(Locale.ROOT))) {
                doc = document;
                break;
            }
        }

        if (doc == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new Group(main, doc));

    }

    @Override
    public String getName() {
        return name;
    }
}
