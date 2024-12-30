package me.iatog.characterdialogue.dialogs.method.npc_control.follow;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class FollowingNPC {
    // String = NPC Id
    // UUID 2 = Player UUID
    private final CharacterDialoguePlugin main;
    private final Map<String, FollowData> followingEntities;

    public FollowingNPC(CharacterDialoguePlugin main) {
        this.followingEntities = new HashMap<>();
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    public void scheduleNPCMovement(AdaptedNPC npc, Entity entity, Player player) {
        if(followingEntities.containsKey(npc.getId())) {
            main.getLogger().warning(npc.getName() + " is already following a player.");
            return;
        }
        
        entity.setCustomName("CHARACTERDIALOGUE_HUSK_DELETE");

        FollowRunnable followRunnable = new FollowRunnable(entity, player, this, npc);
        followRunnable.runTaskTimer(main, 0, 10);
        followingEntities.put(npc.getId(), new FollowData(player.getUniqueId(), entity.getUniqueId(), followRunnable));
    }

    public void removeEntity(String npcId) {
        getFollowingEntities().remove(npcId);
    }

    public void stopAndRemoveEntity(String npcId) {
        FollowData data = getFollowingEntities().get(npcId);

        if(data != null) {
            data.runnable().cancel();
            Entity entity = Bukkit.getEntity(data.entityId());

            if(entity != null) {
                entity.remove();
            }

            removeEntity(npcId);
        }
    }

    public Entity createFollowingEntity(Player player, Location location) {
        Entity entity = player.getWorld().spawnEntity(location, EntityType.HUSK);
        Husk mob = ((Husk) entity);

        mob.setArmsRaised(false);
        mob.setCanBreakDoors(false);
        mob.setCollidable(false);
        mob.setAdult();
        mob.setCanPickupItems(false);
        mob.setInvulnerable(true);
        mob.setInvisible(true);
        mob.setSilent(true);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);
        mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, true));
        // Protecting the entity with events
        mob.setMetadata("characterDialogue-followingPlayer", new FixedMetadataValue(main, true));
        return mob;
    }

    public void removeAll() {
        NPCAdapter<?> adapter = main.getAdapter();
        getFollowingEntities().forEach((id, data) -> {
            AdaptedNPC npc = adapter.getById(id);
            Entity entity = Bukkit.getEntity(data.entityId());

            if(npc == null) return;

            if(data.runnable() != null && !data.runnable().isCancelled()) {
                data.runnable().cancel();
            }

            if(entity != null) {
                entity.remove();
            }
        });

        getFollowingEntities().clear();
    }

    public Map<String, FollowData> getFollowingEntities() {
        return followingEntities;
    }

}
