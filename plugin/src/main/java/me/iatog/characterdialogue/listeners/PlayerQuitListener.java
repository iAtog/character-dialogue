package me.iatog.characterdialogue.listeners;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.database.DialogPersistence;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final CharacterDialoguePlugin main;
    private final Map<UUID, ChoiceSession> choiceSessions;
    private final Map<UUID, DialogSession> dialogSessions;

    public PlayerQuitListener(CharacterDialoguePlugin main) {
        this.main = main;
        this.dialogSessions = main.getCache().getDialogSessions();
        this.choiceSessions = main.getCache().getChoiceSessions();
    }

    @EventHandler
    public void cancelDialogue(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!dialogSessions.containsKey(uuid)) {
            return;
        }

        DialogSession session = dialogSessions.remove(uuid);
        DialogPersistence persistence = main.getServices().getDialogPersistence();

        if(session.isPersistent()) {
            persistence.saveSession(session);
        }

        session.destroy();
    }

    @EventHandler
    public void cancelChoice(PlayerQuitEvent event) {
        choiceSessions.remove(event.getPlayer().getUniqueId());
    }

}
