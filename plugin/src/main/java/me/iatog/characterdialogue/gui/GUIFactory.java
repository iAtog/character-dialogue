package me.iatog.characterdialogue.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GUIFactory {

    private final Map<String, GUI> guiMap;

    public GUIFactory() {
        this.guiMap = new HashMap<>();
    }

    public void registerGui(GUI... guis) {
        for (GUI gui : guis) {
            guiMap.put(gui.getPath(), gui);
        }
    }

    public GUI getGui(String path) {
        return guiMap.get(path);
    }

    public boolean existsGUI(String path) {
        return guiMap.containsKey(path);
    }

    public Set<String> getKeys() {
        return guiMap.keySet();
    }

}
