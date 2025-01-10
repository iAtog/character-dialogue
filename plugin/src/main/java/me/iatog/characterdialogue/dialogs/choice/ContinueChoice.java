package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.dialogs.ChoiceContext;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.entity.Player;

public class ContinueChoice extends DialogChoice {

    public ContinueChoice() {
        super("continue", false);
    }

    @Override
    public void onSelect(ChoiceContext context) {
        Player player = context.getPlayer();
        DialogSession dialogSession = context.getDialogSession();

        if (!context.getArgument().isEmpty()) {
            AdventureUtil.sendMessage(player,
                  Placeholders.translate(player, context.getArgument().replace("%npc_name%", dialogSession.getDisplayName())),
                  AdventureUtil.placeholder("player", player.getName())
            );
        }

        dialogSession.startNext();
    }
}
