package me.iatog.characterdialogue.hook;

import me.iatog.characterdialogue.hook.papi.PAPIHook;
import org.bukkit.Bukkit;

public class Hooks {

    private PAPIHook papiHook;

    public Hooks() {
        if (isPlaceHolderAPIEnabled()) {
            this.papiHook = new PAPIHook();
        }
    }

    public PAPIHook getPlaceHolderAPIHook() {
        return papiHook;
    }

    public boolean isPlaceHolderAPIEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI");
    }

}
