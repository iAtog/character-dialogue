package me.iatog.characterdialogue.libraries;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.dejvokep.boostedyaml.YamlDocument;
import eu.decentsoftware.holograms.api.DHAPI;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HologramLibrary {

    private final CharacterDialoguePlugin main;
    private final String name = "CharacterDialogue_";

    private boolean holographicDisplays;
    private final boolean decentHolograms;

    private final List<String> decentHologramsList;

    public HologramLibrary(CharacterDialoguePlugin main) {
        this.main = main;

        this.holographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        this.decentHolograms = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");

        this.decentHologramsList = new ArrayList<>();
    }

    public boolean hasHologram(String npcId) {
        if(decentHolograms) {
            return decentHologramsList.contains(npcId);
        } else if(holographicDisplays) {
            return false;
        }

        return false;
    }

    public void addHologram(List<String> lines, Location location, String npcName, String npcId) {
        if(holographicDisplays && decentHolograms) {
            this.holographicDisplays = false;
        }

        if (holographicDisplays) {
            Hologram holo = HologramsAPI.createHologram(main, location);

            for (String line : lines) {
                holo.appendTextLine(TextUtils.colorize(line.replace("%npc_name%", npcName)));
            }
        } else if (decentHolograms) {
            List<String> formattedLines = new ArrayList<>();

            for (String line : lines) {
                formattedLines.add(TextUtils.colorize(line.replace("%npc_name%", npcName)));
            }

            DHAPI.createHologram(name + npcId, location, formattedLines);
            decentHologramsList.add(npcId);
        }
    }

    public void reloadHolograms() {
        YamlDocument config = main.getFileFactory().getConfig();
        if (holographicDisplays) {
            for (Hologram hologram : HologramsAPI.getHolograms(main)) {
                hologram.delete();
            }

            config.getSection("npc").getRoutesAsStrings(false).forEach((id) -> {
                main.getApi().loadHologram(id);
            });
        } else if (decentHolograms) {
            for (String id : decentHologramsList) {
                eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram(name + id);

                if (hologram == null) {
                    continue;
                }

                hologram.delete();
            }

            decentHologramsList.clear();

            config.getSection("npc").getRoutesAsStrings(false).forEach((id) -> {
                main.getApi().loadHologram(id);
            });
        }
    }

    public void hideHologram(Player player, String npcId) {
        if(decentHolograms) {
            eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram(name + npcId);

            if(hologram == null) {
                return;
            }
            
            hologram.setHidePlayer(player);
        } else if(holographicDisplays) {
            for(Hologram hologram : HologramsAPI.getHolograms(main)) {
                hologram.getVisibilityManager().hideTo(player);
            }
        }
    }

    public void showHologram(Player player, String npcId) {
        if(decentHolograms) {
            eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram(name + npcId);

            if(hologram == null) {
                return;
            }

            //hologram.setShowPlayer(player);
            hologram.removeHidePlayer(player);
            //hologram.show(player, 1);
        } else if(holographicDisplays) {
            for(Hologram hologram : HologramsAPI.getHolograms(main)) {
                hologram.getVisibilityManager().showTo(player);
            }
        }
    }
}
