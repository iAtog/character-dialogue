package me.iatog.characterdialogue.command.part.gui;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.gui.GUI;
import me.iatog.characterdialogue.gui.GUIFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUIPart implements ArgumentPart {
    private final CharacterDialoguePlugin main;
    private final String name;

    public GUIPart(CharacterDialoguePlugin main, String name) {
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
        GUIFactory factory = main.getGUIFactory();

        for (String c : factory.getKeys()) {
            if (next.isEmpty() || c.startsWith(next)) {
                suggest.add(c);
            }
        }

        return suggest;
    }

    @Override
    public List<GUI> parseValue(CommandContext context, ArgumentStack stack, CommandPart caller) throws ArgumentParseException {
        String gui = stack.next().toLowerCase();
        GUIFactory factory = main.getGUIFactory();

        if (! factory.existsGUI(gui)) {
            return Collections.emptyList();
        }

        return Collections.singletonList(factory.getGui(gui));

    }

    @Override
    public String getName() {
        return name;
    }
}
