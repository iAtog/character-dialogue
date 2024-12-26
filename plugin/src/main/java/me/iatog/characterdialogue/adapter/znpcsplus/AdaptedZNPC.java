package me.iatog.characterdialogue.adapter.znpcsplus;

import lol.pyr.znpcsplus.hologram.HologramImpl;
import lol.pyr.znpcsplus.npc.NpcImpl;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.entity.EntityProperty;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.util.NpcLocation;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.follow.FollowingNPC;
import me.iatog.characterdialogue.path.PathRunnable;
import me.iatog.characterdialogue.path.RecordLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class AdaptedZNPC implements AdaptedNPC {

    private static final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    private final NpcEntry npc;
    private final ZNPCsAdapter adapter;

    private BukkitTask task;

    public AdaptedZNPC(NpcEntry npc, ZNPCsAdapter adapter) {
        this.npc = npc;
        this.adapter = adapter;
    }

    @Override
    public String getName() {
        return npc.getId();
    }

    @Override
    public String getId() {
        return npc.getId();
    }


    @Override
    public Location getStoredLocation() {
        return npc.getNpc().getLocation().toBukkitLocation(npc.getNpc().getWorld());
    }

    /**
     * <a href="https://github.com/Pyrbu/ZNPCsPlus/blob/2.X/plugin/src/main/java/lol/pyr/znpcsplus/npc/NpcRegistryImpl.java#L161">Source</a>
     * <br><br>
     * Replicated since the original method is not accessible from the api.
     * @return the clone of the npc
     */
    //@Override
    public AdaptedNPC copy() {
        if(npc == null) {
            return null;
        }

        NpcEntry newNpc = NpcApiProvider.get().getNpcRegistry().create(
              npc.getId() + "_" + generateId(5) + "_cloned",
              npc.getNpc().getWorld(),
              npc.getNpc().getType(),
              npc.getNpc().getLocation()
        );

        newNpc.setSave(false);
        newNpc.setProcessed(true);
        newNpc.setAllowCommandModification(false);

        for (EntityProperty<?> property : npc.getNpc().getAppliedProperties()) {
            if(!property.getName().equalsIgnoreCase("look")) {
                setProperty(newNpc, property, npc.getNpc().getProperty(property));
            }
        }

        return adapter.adapt(newNpc);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void destroy() {
        if(this.npc == null) {
            return;
        }

        NpcApiProvider.get().getNpcRegistry().delete(npc.getId());
    }

    @Override
    public void spawn(Location location) {
        //teleport(location);
    }

    @Override
    public void teleport(Location location) {
        npc.getNpc().setLocation(new NpcLocation(location));
    }

    @Override
    public void faceLocation(Player player) {
        npc.getNpc().setLocation(npc.getNpc().getLocation().lookingAt(player.getLocation()));
    }

    @Override
    public void follow(Player player) {
        if(task != null) {
            task.cancel();
        }

        FollowingNPC following = main.getServices().getFollowingNPC();
        Entity followingEntity = following.createFollowingEntity(player, getStoredLocation());

        following.scheduleNPCMovement(this, followingEntity, player);
    }

    @Override
    public void unfollow(Player player) {
        FollowingNPC following = CharacterDialoguePlugin.getInstance().getServices().getFollowingNPC();
        following.stopAndRemoveEntity(getId());
    }

    @Override
    public void followPath(final List<RecordLocation> locations) {
        if(task != null) {
            task.cancel();
        }

        this.task = new PathRunnable(locations, this)
              .runTaskTimer(CharacterDialoguePlugin.getInstance(), 0, 1);
    }

    @Override
    public void show(Player player) {
        //npc.getNpc().show(player);
        //npc.getNpc().respawn(player);
        NpcImpl impl = (NpcImpl) npc.getNpc();
        HologramImpl hologram = impl.getHologram();
        if(hologram != null && !hologram.isVisibleTo(player)) {
            hologram.show(player);
        }
        impl.getEntity().spawn(player);
    }

    @Override
    public void hide(Player player) {
        //npc.getNpc().hide(player);
        NpcImpl impl = (NpcImpl) npc.getNpc();
        HologramImpl hologram = impl.getHologram();
        if(hologram != null && hologram.isVisibleTo(player)) {
            hologram.hide(player);
        }

        impl.getEntity().despawn(player);
    }

    @Override
    public void hideForAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            hide(player);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void setProperty(NpcEntry npcProvided, EntityProperty<?> property, Object value) {
        npcProvided.getNpc().setProperty((EntityProperty<T>) property, (T) value);
    }
}