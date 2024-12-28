package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WaitMethod extends DialogMethod<CharacterDialoguePlugin> {

    public WaitMethod(CharacterDialoguePlugin main) {
        super("wait", main);
        setDescription("Wait a few seconds before proceeding");
    }

    @Override
    public void execute(MethodContext context) {
        Player player = context.getPlayer();
        DialogSession session = context.getSession();

        double seconds;
        String arg = context.getConfiguration().getArgument();
        try {
            seconds = Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            getProvider().getLogger().warning("The argument '" + arg + "' in " + session.getDisplayName() + " isn't valid. Line: " + session.getCurrentIndex());
            context.destroy();
            return;
        }

        if (seconds < 0.1) {
            seconds = 1;
        }

        int next = session.getCurrentIndex() + 1;
        //completed.accept(CompletedType.PAUSE);

        Bukkit.getScheduler().runTaskLater(getProvider(), () -> {
            if (next < session.getLines().size() && (player != null && player.isOnline())) {
                context.next();
            } else {
                context.destroy();
            }
        }, (long) (20 * seconds));
    }
}
