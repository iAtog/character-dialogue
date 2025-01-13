package me.iatog.characterdialogue.listeners;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.events.DialogueFinishEvent;
import me.iatog.characterdialogue.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final CharacterDialoguePlugin main;

    public PlayerChatListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation") //@EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!main.isPaper()) {
            if(hasSession(player)) {
                event.getRecipients().clear();
                event.setCancelled(true);
                return;
            }

            event.getRecipients().removeIf(recipient -> receiveMessage(recipient, event.getMessage()));
        }
    }

    @EventHandler
    public void sessionEnd(DialogueFinishEvent event) {
        PlayerData data = main.getApi().getData(event.getPlayer());

        data.sendMessages();
    }

    private boolean hasSession(Player player) {
        return main.getCache().getDialogSessions().containsKey(player.getUniqueId());
    }

    private boolean receiveMessage(Player player, String message) {
        boolean hasSession = hasSession(player);

        if(hasSession) {
            PlayerData data = main.getApi().getData(player);
            data.addMessage(message);
        }

        return hasSession;
    }

}
