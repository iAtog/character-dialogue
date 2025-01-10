package me.iatog.characterdialogue.dialogs.method.choice.listener;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.events.ChoiceSelectEvent;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceUtil;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Map;
import java.util.UUID;

import static me.iatog.characterdialogue.dialogs.method.choice.ChoiceMethod.COMMAND_NAME;

public class ChoiceChatTypeListener implements Listener {

    private final CharacterDialoguePlugin main;
    private final Map<UUID, ChoiceSession> sessions;

    public ChoiceChatTypeListener(CharacterDialoguePlugin main) {
        this.main = main;
        this.sessions = main.getCache().getChoiceSessions();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Map<UUID, DialogSession> dialogSessionMap = main.getCache().getDialogSessions();

        // /<cmd> <choice-uuid> <selected-option>
        // Command args: /<cmd> 4a8d7587-38f3-35e7-b29c-88c715aa6ba8 1

        String[] args = command.split(" ");
        if (!command.startsWith(COMMAND_NAME)) {
            return;
        }

        event.setCancelled(true);

        if (!sessions.containsKey(playerId) || args.length != 3) {
            return;
        }

        DialogSession dialogSession = dialogSessionMap.get(playerId);
        ChoiceSession session = sessions.get(playerId);
        UUID uuid = UUID.fromString(args[1]);
        int choice = Integer.parseInt(args[2]);

        if (!uuid.toString().equals(session.getUniqueId().toString())) {
            dialogSession.sendDebugMessage("UUID's dont match", "ChoiceMethod:onCommand");
            return;
        }

        ChoiceUtil.runChoice(player, choice);
    }

    @EventHandler
    public void selectChoice(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if(main.getCache().getChoiceSessions().containsKey(player.getUniqueId())) {
            ChoiceSession session = main.getCache().getChoiceSessions().get(player.getUniqueId());
            if(session.isUseChat()) {
                ChoiceUtil.runChoice(player, session.getSelected());
            }
        }
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if(sessions.containsKey(player.getUniqueId())) {
            ChoiceSession session = sessions.get(player.getUniqueId());

            if(!session.isUseChat()) {
                return;
            }

            int previousSlot = event.getPreviousSlot();
            int newSlot = event.getNewSlot();
            int direction = (newSlot > previousSlot) ? 1 : -1;
            int selected = session.getSelected();
            int size = session.getChoices().size();
            int newSelected = (selected + direction);

            if (newSelected < 0) {
                newSelected = size - 1;
            }
            if(newSelected >= size) {
                newSelected = 0;
            }

            session.setSelected(newSelected);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playSound(ChoiceSelectEvent event) {
        Player player = event.getPlayer();

        if(!event.isCancelled()) {
            if(event.getSession().isUseChat()) {
                for(int i = 0; i < 20; i++) {
                    player.sendMessage(" ");
                }
            }

            YamlDocument config = main.getFileFactory().getConfig();
            String soundName = config.getString("choice.select-sound");
            float volume = config.getFloat("choice.select-sound-volume", 1f);
            float pitch = config.getFloat("choice.select-sound-pitch", 1f);

            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch(Exception e) {
                main.getLogger().warning("Invalid choice select sound provided: " + soundName);
            }
        }
    }
}
