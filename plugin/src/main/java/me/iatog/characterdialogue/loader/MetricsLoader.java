package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import org.bstats.bukkit.Metrics;

public class MetricsLoader implements Loader {

    public Metrics metrics;
    private final CharacterDialoguePlugin main;

    public MetricsLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        this.metrics = new Metrics(main, 24112);
    }

    public void unload() {
        metrics.shutdown();
    }
}
