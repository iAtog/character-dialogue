package me.iatog.characterdialogue.dialogs.method.npc_control.trait;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FollowPlayerTrait extends Trait {
    private UUID uuid;
    private boolean defined;

    private int sneakTicks;
    private int onAirTries;

    public FollowPlayerTrait() {
        super("followPlayer");
    }

    @Override
    public void run() {
        Navigator navigator = npc.getNavigator();
        NavigatorParameters params = navigator.getDefaultParameters();

        if(!defined) {
            params.baseSpeed(15);
            params.range(30);

            params.stuckAction((npc, nav) -> {
                teleportBehind();
                return true;
            });

            npc.setProtected(true);
            npc.setFlyable(false);
            defined = true;
        }

        if (getPlayer() != null && getPlayer().isOnline()) {
            navigator.getPathStrategy();

            boolean sneaking = getPlayer().isSneaking();
            if (sneaking) {
                if(sneakTicks >= 15) {
                    npc.setSneaking(sneaking);
                } else {
                    sneakTicks++;
                }
            } else {
                sneakTicks = 0;
                npc.setSneaking(false);
            }

            npc.faceLocation(getPlayer().getLocation());
            int distance = (int) getPlayer().getLocation().distance(npc.getEntity().getLocation());
            Location npcLoc = npc.getEntity().getLocation();
            Location blockBelow = new Location(npcLoc.getWorld(), npcLoc.getX(),(int)npcLoc.getY() - 1, npcLoc.getZ());

            if(blockBelow.getBlock().getType() == Material.AIR) {
                onAirTries++;
                if(onAirTries >= 45) {
                    teleportBehind();
                    onAirTries = 0;
                }
            } else if(onAirTries > 0) {
                onAirTries = 0;
            }

            if(distance >= 3 && distance < 50) {
                navigator.setPaused(false);
                navigator.setTarget(getPlayer().getLocation());
            } else if(distance >= 50) {
                teleportBehind();
            } else {
                navigator.setPaused(true);
            }
        }
    }

    public void setTarget(@Nullable Player player) {
        if(player == null) {
            this.uuid = null;
            npc.getNavigator().cancelNavigation();
            return;
        }
        this.uuid = player.getUniqueId();
    }

    public Player getPlayer() {
        if(uuid == null) return null;
        return Bukkit.getPlayer(uuid);
    }

    public void teleportBehind() {
        Location tLoc = getPlayer().getLocation();
        Location nLoc = npc.getEntity().getLocation();

        Vector swap = tLoc.toVector().subtract(nLoc.toVector()).normalize();
        Location behind = tLoc.clone().add(swap);
        behind.setY(tLoc.getY());
        npc.teleport(behind, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

}
