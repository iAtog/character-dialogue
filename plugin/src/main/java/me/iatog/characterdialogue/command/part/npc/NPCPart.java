package me.iatog.characterdialogue.command.part.npc;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPCPart implements ArgumentPart {

    private final String name;
    private final CharacterDialoguePlugin main;

    public NPCPart(CharacterDialoguePlugin main, String name) {
        this.name = name;
        this.main = main;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack) {
        String next = stack.hasNext() ? stack.next() : null;

        if (next == null) {
            return Collections.emptyList();
        }

        List<String> suggest = new ArrayList<>();
        for (String npc : main.getAdapter().getNPCs()) {
            if(npc.toLowerCase().startsWith(next.toLowerCase()) || next.isEmpty()) {
                suggest.add(npc);
            }
        }

        return suggest;
    }

    @Override
    public List<AdaptedNPC> parseValue(CommandContext context, ArgumentStack stack, CommandPart caller) throws ArgumentParseException {
        String possibleNpc = stack.next();
        AdaptedNPC npc = main.getAdapter().getById(possibleNpc);

        if (npc == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(npc);
    }
}
