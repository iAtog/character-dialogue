package me.iatog.characterdialogue.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.player.PlayerData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.UUID;

public class PaperEventsListener implements Listener {

    private final CharacterDialoguePlugin main;

    public PaperEventsListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        if(hasSession(sender)) {
            event.viewers().clear();
            event.setCancelled(true);
            return;
        }

        for(Audience audience : event.viewers()) {
            Optional<UUID> identity = audience.get(Identity.UUID);

            if(identity.isEmpty()) {
                continue;
            }

            Player receiver = Bukkit.getPlayer(identity.get());

            if(interceptMessage(receiver, event.message())) {
                event.viewers().remove(audience);
            }
        }
    }

    private boolean hasSession(Player player) {
        return main.getCache().getDialogSessions().containsKey(player.getUniqueId());
    }

    private boolean interceptMessage(Player player, Component message) {
        boolean hasSession = hasSession(player);
        PlayerData data = main.getApi().getData(player);

        if(hasSession && data != null) {
            data.addMessage(message);
        }

        return hasSession;
    }

}
