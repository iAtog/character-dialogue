package me.iatog.characterdialogue.integration.adapter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface AdaptedNPC {
    boolean isDestroyed();
    String getName();
    String getId();
    Location getLocation();
    AdaptedNPC copy();

    // Attributes
    void setName(String name);
    void destroy();
    void spawn(Location location);
    void teleport(Location location);
    void faceLocation(Player player);
    //void equip(Player player, EquipmentType type, ItemStack item);

    // Movement
    void sneak(Player player, boolean sneaking);
    void follow(Player player);
    void unfollow(Player player);
    //void followPath(List<RecordLocation> locations, @Nullable Player viewer);

    // View
    void show(Player player);
    void hide(Player player);
    void hideForAll();
}
