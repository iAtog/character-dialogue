package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.entity.Player;

public class MessageChoice extends DialogChoice {

    public MessageChoice() {
        super("message", true);
    }

    @Override
    public void onSelect(String argument, DialogSession session, ChoiceSession choiceSession) {
        Player player = session.getPlayer();

        AdventureUtil.sendMessage(player,
              Placeholders.translate(player, argument.replace("%npc_name%", session.getDisplayName())),
              AdventureUtil.placeholder("player", player.getName())
        );

        session.startNext();
    }

}
