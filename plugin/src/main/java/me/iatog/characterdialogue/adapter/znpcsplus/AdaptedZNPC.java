package me.iatog.characterdialogue.adapter.znpcsplus;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.entity.EntityProperty;
import lol.pyr.znpcsplus.api.interaction.InteractionAction;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.util.NpcLocation;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.path.PathRunnable;
import me.iatog.characterdialogue.path.RecordLocation;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class AdaptedZNPC implements AdaptedNPC {

    private final NpcEntry npc;
    private final ZNPCsAdapter adapter;

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
     * @return
     */
    @Override
    public AdaptedNPC copy() {
        if(npc == null) {
            return null;
        }

        NpcEntry newNpc = NpcApiProvider.get().getNpcRegistry().create(
              npc.getId() + "_cloned",
              npc.getNpc().getWorld(),
              npc.getNpc().getType(),
              npc.getNpc().getLocation()
        );

        newNpc.setSave(false);
        newNpc.setProcessed(false);
        newNpc.setAllowCommandModification(false);

        for (EntityProperty<?> property : npc.getNpc().getAppliedProperties()) {
            setProperty(newNpc, property, npc.getNpc().getProperty(property));
        }

        for (InteractionAction action : npc.getNpc().getActions()) {
            newNpc.getNpc().addAction(action);
        }

        return adapter.adapt(newNpc);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void destroy() {
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
    public void faceLocation(Location location) {
        npc.getNpc().setLocation(npc.getNpc().getLocation().lookingAt(location));
    }

    @Override
    public void follow(Player player) {
        player.sendMessage(TextUtils.colorize("&cfollow action is not available in zNPCs"));
    }

    @Override
    public void unfollow(Player player) {
        player.sendMessage(TextUtils.colorize("&cunfollow action is not available in zNPCs"));
    }

    @Override
    public void followPath(final List<RecordLocation> locations) {
        new PathRunnable(locations, this)
              .runTaskTimer(CharacterDialoguePlugin.getInstance(), 0, 1);
    }

    @Override
    public void show(Player player) {
        npc.getNpc().show(player);
    }

    @Override
    public void hide(Player player) {
        npc.getNpc().hide(player);
    }

    @Override
    public void hideForAll() {
        // HANDLING THIS WITH NpcSpawnEvent
    }

    @SuppressWarnings("unchecked")
    private <T> void setProperty(NpcEntry npcProvided, EntityProperty<?> property, Object value) {
        npcProvided.getNpc().setProperty((EntityProperty<T>) property, (T) value);
    }
}
