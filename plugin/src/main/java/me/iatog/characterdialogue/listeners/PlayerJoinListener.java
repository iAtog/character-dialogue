package me.iatog.characterdialogue.listeners;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.database.DialogPersistence;
import me.iatog.characterdialogue.database.PlayerDataDatabase;
import me.iatog.characterdialogue.player.PlayerData;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.session.InitializeSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final CharacterDialoguePlugin main;

    public PlayerJoinListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<UUID> cache = main.getCache().getFrozenPlayers();

        cache.remove(player.getUniqueId());

        if (main.getApi().canEnableMovement(player)) {
            main.getApi().enableMovement(player);
        }


        if (player.hasPotionEffect(PotionEffectType.SLOW)) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.SLOW);

            if (effect != null && effect.getAmplifier() == 4) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoinPersistence(PlayerJoinEvent event) {
        DialogPersistence persistence = main.getServices().getDialogPersistence();
        Player player = event.getPlayer();

        if(persistence.hasStoredSession(player)) {
            DialogSession session = persistence.createSession(player);

            if(session == null) {
                main.getLogger().severe("Error while restoring dialogue session: session is null");
                return;
            }
            persistence.deleteSession(player);

            Dialogue dialogue = session.getDialogue();

            if(!dialogue.isMovementAllowed()) {
                main.getApi().disableMovement(player);
            }

            player.sendMessage("Restoring session:");
            List<String> lines = dialogue.getPersistentLines();
            main.getCache().getDialogSessions().put(player.getUniqueId(), session);

            if(lines != null && !lines.isEmpty()) {
                InitializeSession initializeSession = new InitializeSession(main, lines, player, session);

                initializeSession.start((x) -> {
                    session.start();
                }, 0);
            } else {
                session.start();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void addData(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataDatabase database = main.getServices().getPlayerDataDatabase();
        PlayerData data = database.get(player);

        if(data == null) {
            data = new PlayerData(
                  player.getUniqueId(),
                  new ArrayList<>(),
                  new ArrayList<>(),
                  false,
                  player.getWalkSpeed()
            );
        }

        main.getCache().getPlayerData().put(player.getUniqueId(), data);
    }

}
