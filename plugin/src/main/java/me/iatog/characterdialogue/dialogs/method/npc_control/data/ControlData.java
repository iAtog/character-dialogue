package me.iatog.characterdialogue.dialogs.method.npc_control.data;

import me.iatog.characterdialogue.adapter.AdaptedNPC;

public class ControlData {
    private final AdaptedNPC original;
    private final AdaptedNPC copy;

    private boolean hideOriginal;

    public ControlData(AdaptedNPC original, AdaptedNPC copy) {
        this.original = original;
        this.copy = copy;
    }


    public AdaptedNPC getOriginal() {
        return original;
    }

    public AdaptedNPC getCopy() {
        return copy;
    }

    public void setHideOriginal(boolean hideOriginal) {
        this.hideOriginal = hideOriginal;
    }

    public boolean isHideOriginal() {
        return hideOriginal;
    }
}