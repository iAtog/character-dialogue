package me.iatog.characterdialogue.dialogs.method.npc_control.follow;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class FollowRunnable extends BukkitRunnable {

    private final FollowingNPC followingNPC;

    private final UUID playerId;
    private final UUID entityId;
    private final AdaptedNPC npc;

    private boolean wait;

    public FollowRunnable(Entity entity, Player player, FollowingNPC followingNPC, AdaptedNPC npc) {
        this.playerId = player.getUniqueId();
        this.entityId = entity.getUniqueId();
        this.followingNPC = followingNPC;
        this.npc = npc;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerId);
        Entity entity = Bukkit.getEntity(entityId);
        Mob mob = ((Mob) entity);

        if(player == null || !player.isOnline() || mob == null || mob.isDead()) {
            this.cancel();
            this.followingNPC.removeEntity(npc.getId());
            if(mob != null && !mob.isDead()) {
                mob.remove();
            }
            return;
        }

        if(wait) {
            return;
        }

        double distance = mob.getLocation().distance(player.getLocation());

        if(distance >= 3 && distance <= 30) {
            mob.getPathfinder().moveTo(player.getLocation());
        } else if(distance > 30) {
            mob.getPathfinder().stopPathfinding();
            wait = true;
            Location behind = behind(player.getLocation());

            entity.teleportAsync(behind, PlayerTeleportEvent.TeleportCause.PLUGIN).thenRunAsync(() -> {
                npc.teleport(behind);
                wait = false;
                player.sendMessage("TELEPORTED CORRECTLY");
            });
            return;
        } else {
            mob.lookAt(player);
            mob.getPathfinder().stopPathfinding();
        }

        npc.teleport(mob.getLocation());
    }

    public Location behind(Location playerLoc) {
        Location nLoc = npc.getStoredLocation();

        Vector swap = playerLoc.toVector().subtract(nLoc.toVector()).normalize();
        Location behind = playerLoc.add(swap);
        behind.setY(playerLoc.getY());

        return behind;
    }
}