package me.iatog.characterdialogue.command.part.record;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.path.PathStorage;
import me.iatog.characterdialogue.path.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordPart implements ArgumentPart {
    private final String name;
    private final PathStorage storage;

    public RecordPart(String name, CharacterDialoguePlugin main) {
        this.name = name;
        this.storage = main.getPathStorage();
    }

    @Override
    public List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack) {
        String next = stack.hasNext() ? stack.next() : null;

        if (next == null) {
            return Collections.emptyList();
        }

        List<String> suggest = new ArrayList<>();

        for(String name : storage.getAllPaths().keySet()) {
            if(next.isEmpty() || name.toLowerCase().startsWith(next.toLowerCase())) {
                suggest.add(name.toLowerCase());
            }
        }

        return suggest;
    }

    @Override
    public List<Record> parseValue(CommandContext context, ArgumentStack stack, CommandPart caller) throws ArgumentParseException {
        String possible = stack.next();

        if (storage.getPath(possible) == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new Record(possible, storage.getPath(possible)));
    }

    @Override
    public String getName() {
        return name;
    }
}
