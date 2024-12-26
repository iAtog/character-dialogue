package me.iatog.characterdialogue.adapter;

import me.iatog.characterdialogue.enums.EquipmentType;
import me.iatog.characterdialogue.path.RecordLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface AdaptedNPC {

    String getName();
    String getId();
    //Entity getEntity();
    Location getStoredLocation();
    AdaptedNPC copy();

    void setName(String name);
    void destroy();
    void spawn(Location location);
    void teleport(Location location);
    void faceLocation(Player player);
    void equip(Player player, EquipmentType type, ItemStack item);

    void follow(Player player);
    void unfollow(Player player);
    void followPath(List<RecordLocation> locations);
    void show(Player player);
    void hide(Player player);
    void hideForAll();

    default String generateId(int length) {
        StringBuilder str = new StringBuilder();
        String a = "abcdefghijkmnopqrstuvwxyz1234567890";

        for (int i=0;i<length;i++) {
            str.append(a.charAt(i));
        }

        return str.toString();
    }

}
