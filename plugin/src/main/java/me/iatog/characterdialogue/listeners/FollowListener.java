package me.iatog.characterdialogue.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class FollowListener implements Listener {

    private final String metadataKey = "characterDialogue-followingPlayer";
    private final String entityName = "CHARACTERDIALOGUE_HUSK_DELETE";

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();

        if(entity.hasMetadata(metadataKey)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPunch(EntityDamageEvent event) {
        if(event.getEntity().hasMetadata(metadataKey)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if(event.getEntity().hasMetadata(metadataKey)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if(event.getEntity().hasMetadata(metadataKey)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        String customName = event.getEntity().getCustomName();
        if(customName != null && customName.equalsIgnoreCase(entityName)) {
            event.getEntity().remove();
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        String customName = damager.getCustomName();

        if(customName != null &&
              customName.equalsIgnoreCase(entityName) && damager.getType() == EntityType.HUSK) {
            damager.remove();
            event.setCancelled(true);
        }
    }
}
