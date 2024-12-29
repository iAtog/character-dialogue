package me.iatog.characterdialogue.command.part.method;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MethodPart implements ArgumentPart {

    private final String name;
    private final CharacterDialoguePlugin main;

    public MethodPart(String name, CharacterDialoguePlugin main) {
        this.name = name;
        this.main = main;
    }

    @Override
    public List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack) {
        String next = stack.hasNext() ? stack.next() : null;

        if (next == null) {
            return Collections.emptyList();
        }

        List<String> suggest = new ArrayList<>();

        for (String name : main.getCache().getMethods().keySet()) {
            if (next.isEmpty() || name.toLowerCase().startsWith(next.toLowerCase())) {
                suggest.add(name.toLowerCase());
            }
        }

        return suggest;
    }

    @Override
    public List<DialogMethodArgument> parseValue(CommandContext context, ArgumentStack stack, CommandPart caller) throws ArgumentParseException {
        String possibleMethod = stack.next();
        Map<String, DialogMethod<? extends JavaPlugin>> methods = main.getCache().getMethods();

        if (! methods.containsKey(possibleMethod)) {
            return Collections.emptyList();
        }

        DialogMethod<? extends JavaPlugin> method = methods.get(possibleMethod);
        return Collections.singletonList(new DialogMethodArgument(method.getID(), method));
    }

    @Override
    public String getName() {
        return name;
    }

}
