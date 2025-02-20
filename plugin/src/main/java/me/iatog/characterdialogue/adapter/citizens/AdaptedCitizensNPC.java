package me.iatog.characterdialogue.adapter.citizens;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.dialogs.method.npc_control.trait.FollowPlayerTrait;
import me.iatog.characterdialogue.enums.EquipmentType;
import me.iatog.characterdialogue.path.PathTrait;
import me.iatog.characterdialogue.path.RecordLocation;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdaptedCitizensNPC implements AdaptedNPC {

    private final NPC npc;
    private boolean destroyed;

    public AdaptedCitizensNPC(NPC npc) {
        this.npc = npc;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public String getName() {
        return npc.getName();
    }

    @Override
    public String getId() {
        return String.valueOf(npc.getId());
    }

    @Override
    public void setName(String name) {
        npc.setName(name);
    }

    @Override
    public Location getStoredLocation() {
        return npc.getStoredLocation();
    }

    @Override
    public AdaptedNPC copy() {
        return new AdaptedCitizensNPC(npc.copy());
    }

    @Override
    public void destroy() {
        npc.destroy();
        this.destroyed = true;
    }

    @Override
    public void spawn(Location location) {
        npc.spawn(location, SpawnReason.PLUGIN);
    }

    @Override
    public void teleport(Location location) {
        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public void faceLocation(Player player) {
        npc.faceLocation(player.getLocation());
    }

    @Override
    public void equip(Player player, EquipmentType type, ItemStack item) {
        // not implemented
    }

    @Override
    public void sneak(Player player, boolean sneaking) {
        npc.setSneaking(sneaking);
    }

    @Override
    public void follow(Player player) {
        FollowPlayerTrait trait = npc.getOrAddTrait(FollowPlayerTrait.class);
        trait.setTarget(player);
    }

    @Override
    public void unfollow(Player player) {
        FollowPlayerTrait trait = npc.getTraitNullable(FollowPlayerTrait.class);

        if(trait != null) {
            trait.setTarget(null);
            npc.removeTrait(FollowPlayerTrait.class);
        }
    }

    @Override
    public void followPath(List<RecordLocation> locations, @Nullable Player viewer) {
        PathTrait trait = npc.getOrAddTrait(PathTrait.class);
        trait.setPaths(locations);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void show(Player player) {
        player.showEntity(CharacterDialoguePlugin.getInstance(), npc.getEntity());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void hide(Player player) {
        player.hideEntity(CharacterDialoguePlugin.getInstance(), npc.getEntity());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void hideForAll() {
        npc.getEntity().setVisibleByDefault(false);
    }
}
