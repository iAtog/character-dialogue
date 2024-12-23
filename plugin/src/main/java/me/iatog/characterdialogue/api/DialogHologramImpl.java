package me.iatog.characterdialogue.api;

import me.iatog.characterdialogue.api.dialog.DialogHologram;

import java.util.List;

public class DialogHologramImpl implements DialogHologram {

    private final boolean enabled;
    private final float y;
    private final List<String> lines;

    public DialogHologramImpl(boolean enabled, float y, List<String> lines) {
        this.enabled = enabled;
        this.y = y;
        this.lines = lines;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

}
