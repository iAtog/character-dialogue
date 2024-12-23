package me.iatog.characterdialogue.path;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RecordLocation {

    private double x, y, z;
    private float yaw, pitch;
    private String worldName;
    private boolean sneaking;

    public RecordLocation(Location location, boolean sneaking) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.worldName = location.getWorld().getName();
        this.sneaking = sneaking;
    }

    public RecordLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
    }

    public RecordLocation() {

    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public boolean isSneaking() {
        return sneaking;
    }
}
