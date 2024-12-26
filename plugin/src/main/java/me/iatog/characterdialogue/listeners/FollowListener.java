package me.iatog.characterdialogue.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class FollowListener implements Listener {

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();

        if(entity.hasMetadata("characterDialogue-followingPlayer")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPunch(EntityDamageEvent event) {
        if(event.getEntity().hasMetadata("characterDialogue-followingPlayer")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if(event.getEntity().hasMetadata("characterDialogue-followingPlayer")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if(event.getEntity().hasMetadata("characterDialogue-followingPlayer")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        if(event.getEntity().hasMetadata("characterDialogue-followingPlayer")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        String customName = event.getEntity().getCustomName();
        if(customName != null && customName.equalsIgnoreCase("CHARACTERDIALOGUE_HUSK_DELETE")) {
            event.getEntity().remove();
        }
    }

}
