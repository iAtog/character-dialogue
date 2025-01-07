package me.iatog.characterdialogue.dialogs.method.choice;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.events.ChoiceSelectEvent;
import me.iatog.characterdialogue.dialogs.Choice;
import me.iatog.characterdialogue.dialogs.ChoiceInfo;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChoiceUtil {

    private static final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    public static DialogChoice getByClassName(Class<? extends DialogChoice> clazz) {
        for (DialogChoice target : CharacterDialoguePlugin.getInstance().getCache().getChoices().values()) {
            if (target.getClass() == clazz) {
                return target;
            }
        }

        return null;
    }

    public static boolean isContextValid(MethodContext context) {
        CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
        String choice = context.getConfiguration().getArgument();


        if (main.getCache().getChoiceSessions().containsKey(context.getPlayer().getUniqueId())) {
            context.getSession().sendDebugMessage("Player already in a choice session.", "ChoiceMethod");
            return false;
        }

        if (choice.isEmpty()) {
            context.getSession().sendDebugMessage("No choice specified, cancelling.", "ChoiceMethod");
            return false;
        }

        if (!main.getCache().getLoadedChoices().containsKey(choice)) {
            String msg = "The choice \"" + choice + "\" doesn't exists.";
            main.getLogger().warning(msg);
            context.getSession().sendDebugMessage(msg, "ChoiceMethod");
            return false;
        }

        return true;
    }

    public static void addChoices(ChoiceSession choiceSession, String choiceName) {
        CharacterDialoguePlugin provider = CharacterDialoguePlugin.getInstance();
        Map<String, List<ChoiceInfo>> loadedChoices = main.getCache().getLoadedChoices();

        if(!loadedChoices.containsKey(choiceName)) {
            return;
        }

        for (ChoiceInfo choice : loadedChoices.get(choiceName)) {
            String type = choice.getType();
            String message = choice.getMessage();
            String argument = choice.getArgument();

            if (type == null || !provider.getCache().getChoices().containsKey(type)) {
                provider.getLogger().warning("The type of choice '" + choice + "' in " + choiceName + " isn't valid");
                continue;
            }

            DialogChoice choiceObject = provider.getCache().getChoices().get(type);

            if (argument.isEmpty() && choiceObject.isArgumentRequired()) {
                provider.getLogger().severe("The argument in the choice \"" + choice + "\" is missing");
                continue;
            }

            choiceSession.addChoice(message, choiceObject.getClass(), argument);
        }
    }

    public static void manageCooldown(ChoiceData data, int secondsCooldown, Consumer<ChoiceData> onClose) {
        Player player = data.getPlayer();
        ChoiceSession choiceSession = data.getChoiceSession();
        DialogSession session = data.getDialogSession();

        BukkitTask task = Bukkit.getScheduler().runTaskLater(CharacterDialoguePlugin.getInstance(), () -> {
            UUID uuid = player.getUniqueId();

            choiceSession.destroy();
            session.destroy();
            ChoiceMethod.taskList.remove(uuid);

            if (player != null && player.isOnline()) {
                onClose.accept(data);
                AdventureUtil.sendActionBar(player, main.language("choice-timeout"));
            }
        }, 20L * secondsCooldown);

        ChoiceMethod.taskList.put(player.getUniqueId(), task);
    }

    public static void runChoice(Player player, int choice) {
        Map<UUID, ChoiceSession> sessions = CharacterDialoguePlugin.getInstance().getCache().getChoiceSessions();
        UUID uuid = player.getUniqueId();

        if (! sessions.containsKey(uuid)) {
            return;
        }

        ChoiceSession session = sessions.get(uuid);
        Choice choiceObject = session.getChoice(choice);

        if (choiceObject == null || session.isDestroyed()) {
            return;
        }

        ChoiceSelectEvent choiceEvent = new ChoiceSelectEvent(player, uuid, choiceObject, session);
        Bukkit.getPluginManager().callEvent(choiceEvent);

        if (choiceEvent.isCancelled()) {
            return;
        }

        DialogChoice choiceTarget = ChoiceUtil.getByClassName(choiceObject.getChoiceClass());
        DialogSession dialogSession = CharacterDialoguePlugin.getInstance().getCache().getDialogSessions().get(uuid);

        if (dialogSession == null || choiceTarget == null) {
            sessions.remove(uuid);
            return;
        }

        choiceTarget.onSelect(choiceObject.getArgument(), dialogSession, session);
        sessions.remove(uuid);
        removeTaskIfPresent(uuid);
    }

    public static void removeTaskIfPresent(UUID uuid) {
        if (ChoiceMethod.taskList.get(uuid) != null) {
            ChoiceMethod.taskList.remove(uuid).cancel();
        }
    }

}
