package me.iatog.characterdialogue.command.part.item;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.ItemManager;
import me.iatog.characterdialogue.util.CustomItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomItemPart implements ArgumentPart {

    private final CharacterDialoguePlugin main;
    private final String name;

    public CustomItemPart(CharacterDialoguePlugin main, String name) {
        this.name = name;
        this.main = main;
    }

    @Override
    public List<CustomItem> parseValue(CommandContext context, ArgumentStack stack, CommandPart caller) throws ArgumentParseException {
        String itemName = stack.next().toLowerCase();
        ItemManager manager = main.getServices().getItemManager();

        if(!manager.existsItem(itemName)) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new CustomItem(itemName, manager.getItem(itemName)));
    }

    @Override
    public List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack) {
        String next = stack.hasNext() ? stack.next() : null;

        if (next == null) {
            return Collections.emptyList();
        }

        List<String> suggest = new ArrayList<>();
        ItemManager manager = main.getServices().getItemManager();

        for(String c : manager.getItems().keySet()) {
            if (next.isEmpty() || c.startsWith(next)) {
                suggest.add(c);
            }
        }

        return suggest;
    }

    @Override
    public String getName() {
        return name;
    }
}
