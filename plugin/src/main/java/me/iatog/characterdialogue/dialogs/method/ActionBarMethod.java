package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.placeholders.Placeholders;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarMethod extends DialogMethod<CharacterDialoguePlugin> {
    public ActionBarMethod() {
        super("actionbar");
        setDescription("Displays an actionbar to the player");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(MethodContext context) {
        Player player = context.getPlayer();
        String argument = context.getConfiguration().getArgument();

        if(!argument.isEmpty()) {
            String translated = Placeholders.translate(player, argument);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(translated));
        }

        context.next();
    }
}
