package me.iatog.characterdialogue.loader;

import com.sk89q.worldguard.WorldGuard;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.libraries.WGHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class WorldGuardLoader implements Loader {

    private final CharacterDialoguePlugin main;

    public WorldGuardLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if(plugin == null) {
            return;
        }

        String version = WorldGuard.getVersion();

        if(!version.startsWith("7.")) {
            main.getLogger().warning("Consider updating WorldGuard to version 7.x");
        }

        if(!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WGHandler.factory, null)) {
            main.getLogger().severe("Could not register the WorldGuard handler...");
        }
    }
}
