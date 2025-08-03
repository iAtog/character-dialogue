package me.iatog.characterdialogue.libraries;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import me.iatog.characterdialogue.api.events.RegionEnterEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

public class WGHandler extends Handler {
    private final PluginManager pm = Bukkit.getPluginManager();
    public static final Factory factory = new Factory();

    public static class Factory extends Handler.Factory<WGHandler> {
        @Override
        public WGHandler create(Session session) {
            return new WGHandler(session);
        }
    }

    public WGHandler(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        for(ProtectedRegion r : entered) {
            RegionEnterEvent enteredRegion = new RegionEnterEvent(player.getUniqueId(), r);
            pm.callEvent(enteredRegion);

            if(enteredRegion.isCancelled()) {
                return false;
            }
        }

        return true;
    }

    public static class Util {
        public static boolean registerHandler() {
            return WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WGHandler.factory, null);
        }

        public static String version() {
            return WorldGuard.getVersion();
        }
    }
}
