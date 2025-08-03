package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.WGHandler;
import org.bukkit.Bukkit;

public class WorldGuardLoader implements Loader {

    private final CharacterDialoguePlugin main;

    public WorldGuardLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        if(!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            return;
        }

        try {
            Class.forName("com.sk89q.worldguard.protection.regions.ProtectedRegion");
        } catch(Exception ex) {
            return;
        }

        String version = WGHandler.Util.version();

        if(!version.startsWith("7.")) {
            main.getLogger().warning("Consider updating WorldGuard to version 7.x");
        }

        if(!WGHandler.Util.registerHandler()) {
            main.getLogger().severe("Could not register the WorldGuard handler...");
        }
    }
}
