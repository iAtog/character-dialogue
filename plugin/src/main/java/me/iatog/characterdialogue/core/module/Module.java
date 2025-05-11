package me.iatog.characterdialogue.core.module;

public interface Module {

    void load();

    default void unload() {
        // Not implemented
    }
}
