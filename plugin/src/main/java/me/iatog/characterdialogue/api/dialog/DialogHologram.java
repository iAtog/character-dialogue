package me.iatog.characterdialogue.api.dialog;

import java.util.List;

public interface DialogHologram {
    boolean isEnabled();

    float getY();

    List<String> getLines();
}