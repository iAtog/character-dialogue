package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.dialogs.ChoiceContext;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.entity.Player;

public class DestroyChoice extends DialogChoice {

    public DestroyChoice() {
        super("destroy", false);
    }

    @Override
    public void onSelect(ChoiceContext context) {
        Player player = context.getPlayer();
        DialogSession dialogSession = context.getDialogSession();
        String argument = context.getArgument();

        if (!argument.isEmpty()) {
            AdventureUtil.sendMessage(player,
                  Placeholders.translate(player, argument, dialogSession.getDisplayName()),
                  AdventureUtil.placeholder("player", player.getName())
            );
        }

        dialogSession.destroy();
    }

}
