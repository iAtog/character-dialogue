package me.iatog.characterdialogue.adapter;

import me.iatog.characterdialogue.path.RecordLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    void faceLocation(Location location);

    void follow(Player player);
    void unfollow(Player player);
    void followPath(List<RecordLocation> locations);
    void show(Player player);
    void hide(Player player);
    void hideForAll();

}
