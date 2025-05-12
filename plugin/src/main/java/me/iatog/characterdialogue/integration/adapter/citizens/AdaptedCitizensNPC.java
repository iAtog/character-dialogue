package me.iatog.characterdialogue.integration.adapter.citizens;

import me.iatog.characterdialogue.integration.adapter.AdaptedNPC;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

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
    public Location getLocation() {
        return npc.getStoredLocation();
    }

    @Override
    public AdaptedNPC copy() {
        return new AdaptedCitizensNPC(npc.copy());
    }

    @Override
    public void setName(String name) {
        npc.setName(name);
    }

    @Override
    public void destroy() {
        npc.destroy();
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
    public void sneak(Player player, boolean sneaking) {
        npc.setSneaking(sneaking);
    }

    @Override
    public void follow(Player player) {

    }

    @Override
    public void unfollow(Player player) {

    }

    @Override
    public void show(Player player) {

    }

    @Override
    public void hide(Player player) {

    }

    @Override
    public void hideForAll() {

    }
}
