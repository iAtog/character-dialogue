package me.iatog.characterdialogue.loader;

public interface Loader {
    void load();

    default void unload() {
        // Empty method
    }
}
