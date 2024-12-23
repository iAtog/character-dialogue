package me.iatog.characterdialogue.api;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.dialog.DialogHologram;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.enums.ClickType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TestDialogueImpl implements Dialogue {
    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public List<String> getLines() {
        return Collections.emptyList();
    }

    @Override
    public ClickType getClickType() {
        return ClickType.RIGHT;
    }

    @Override
    public String getDisplayName() {
        return "Test-Dialogue";
    }

    @Override
    public DialogHologram getHologram() {
        return null;
    }

    @Override
    public List<String> getFirstInteractionLines() {
        return Collections.emptyList();
    }

    @Override
    public boolean isFirstInteractionEnabled() {
        return false;
    }

    @Override
    public boolean start(Player player, boolean debug, AdaptedNPC npc) {
        return false;
    }

    @Override
    public boolean startFirstInteraction(Player player, boolean log, AdaptedNPC npc) {
        return false;
    }

    @Override
    public DialoguePermission getPermissions() {
        return null;
    }

    @Override
    public boolean isMovementAllowed() {
        return true;
    }

    @Override
    public YamlDocument getDocument() {
        return null;
    }

    @Override
    public Section getSection() {
        return null;
    }

    @Override
    public boolean isSlowEffectEnabled() {
        return false;
    }
}
