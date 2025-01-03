package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.entity.Player;

public class ActionBarMethod extends DialogMethod<CharacterDialoguePlugin> {
    public ActionBarMethod() {
        super("actionbar");
        setDescription("Displays an actionbar to the player");
    }

    @Override
    public void execute(MethodContext context) {
        Player player = context.getPlayer();
        String argument = context.getConfiguration().getArgument();

        if(!argument.isEmpty()) {
            String translated = Placeholders.translate(player, argument);
            AdventureUtil.sendActionBar(player, translated);
        }

        context.next();
    }
}
