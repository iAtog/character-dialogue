package me.iatog.characterdialogue.listeners;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

}
