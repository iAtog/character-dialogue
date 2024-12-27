package me.iatog.characterdialogue.api.dialog;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.enums.ClickType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Dialogue {
    boolean isPersistent();
    List<String> getPersistentLines();

    String getName();
    List<String> getLines();
    ClickType getClickType();
    String getDisplayName();
    DialogHologram getHologram();
    List<String> getFirstInteractionLines();

    boolean isFirstInteractionEnabled();
    boolean start(Player player, boolean debug, AdaptedNPC npc);
    boolean startFirstInteraction(Player player, boolean log, AdaptedNPC npc);
    DialoguePermission getPermissions();
    boolean isMovementAllowed();
    default boolean start(@NotNull Player player, @Nullable AdaptedNPC npc) {
        return start(player, false, npc);
    }
    YamlDocument getDocument();
    default Section getSection() {
        return getDocument().getSection("dialogue." + getName());
    }
    boolean isSlowEffectEnabled();

    class DialoguePermission {

        private final String permission;
        private final String message;

        public DialoguePermission(String permission, String message) {
            this.permission = permission;
            this.message = message;
        }

        public String getPermission() {
            return permission;
        }

        public String getMessage() {
            return message;
        }

    }
}
