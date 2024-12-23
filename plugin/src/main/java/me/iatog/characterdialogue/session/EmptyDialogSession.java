package me.iatog.characterdialogue.session;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.enums.ClickType;
import org.bukkit.entity.Player;

import java.util.List;

public class EmptyDialogSession extends DialogSession {

    public EmptyDialogSession(CharacterDialoguePlugin main, Player player, List<String> lines, String name, AdaptedNPC npc) {
        super(main, player, lines, ClickType.ALL, npc, name, name);
    }

    @Override
    public void start(int index) {
        // nothing
    }

}
