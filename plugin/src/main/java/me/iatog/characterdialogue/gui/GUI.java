package me.iatog.characterdialogue.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.iatog.characterdialogue.util.TextUtils.colorizeComponent;

public abstract class GUI {

    private final String path;
    private final List<UUID> observers;

    public static final SkullBuilder previousItem = ItemBuilder.skull()
          .name(colorizeComponent("&aPrevious"))
          .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTFmMDU5Yzk5ZThlOTIyZjhjYjdmYThlNjA0YTcwMTQ3ZmExMTg0OWIzYjNkYmJmZjIyNjU3OTEyMTRlMjM5MSJ9fX0=");

    public static final SkullBuilder nextItem = ItemBuilder.skull()
          .name(colorizeComponent("&aNext"))
          .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFjMmUyOTE5MWNiYmZhZWM0OWYyYzY0MWE0MGFhODA4ZDQ5ODQ2YjcwNWRmM2M5ZjAyYjIzOTZkYThlMTE2In19fQ==");

    public GUI(String path) {
        this.path = path;
        this.observers = new ArrayList<>();
    }

    public abstract void load(Player player);
    //public abstract Gui getGui();

    public String getPath() {
        return path;
    }

    public void removeObserver(Player player) {
        observers.remove(player.getUniqueId());
        //player.closeInventory();
    }

    public void addObserver(Player player) {
        observers.add(player.getUniqueId());
    }

    public void clearObservers() {
        for (UUID uuid : observers) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.closeInventory();
            }

            observers.remove(uuid);
        }
    }

    public boolean hasObserver(UUID uuid) {
        return observers.contains(uuid);
    }

    protected void setupObservers(BaseGui gui) {
        gui.setCloseGuiAction((action) ->
              this.removeObserver((Player) action.getPlayer())
        );

        gui.setOpenGuiAction((action) ->
              this.addObserver((Player) action.getPlayer())
        );
    }

    protected Component[] buildLore(List<String> extra, String... lore) {
        List<Component> components = new ArrayList<>();

        for(String line : lore) {
            components.add(colorizeComponent(line));
        }

        for (String s : extra) {
            components.add(colorizeComponent(s));
        }

        return components.toArray(new Component[0]);
    }

    protected Component[] buildLore(String... lore) {
        return buildLore(new ArrayList<>(), lore);
    }

}
