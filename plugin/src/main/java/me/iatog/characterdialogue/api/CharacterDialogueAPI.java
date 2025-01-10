package me.iatog.characterdialogue.api;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.enums.ClickType;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.enums.ConditionType;
import me.iatog.characterdialogue.libraries.HologramLibrary;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface CharacterDialogueAPI {

    Dialogue getDialogue(String name);

    void reloadHolograms();

    void loadHologram(String npcId);

    boolean readDialogBy(Player player, String dialog, boolean firstInteraction);

    boolean wasReadedBy(Player player, String dialog, boolean firstInteraction);

    boolean readDialogBy(Player player, Dialogue dialog, boolean firstInteraction);

    boolean wasReadedBy(Player player, Dialogue dialog, boolean firstInteraction);

    void runDialogue(Player player, Dialogue dialogue, boolean debugMode, AdaptedNPC npc);

    void runDialogue(Player player, String dialogueName, boolean debugMode, AdaptedNPC npc);

    void runDialogueExpression(Player player, String dialog);

    void runDialogueExpression(Player player, String dialog, String npcName);

    void runDialogueExpression(Player player, String dialog, String npcName, SingleUseConsumer<CompletedType> onComplete, DialogSession session, AdaptedNPC npc);

    void runDialogueExpressions(Player player, List<String> lines, ClickType type, AdaptedNPC npc, String displayName, String dialogueName);

    void runDialogueExpressions(Player player, List<String> lines, String displayName, String dialogueName);

    Dialogue getNPCDialogue(String id);

    Map<String, Dialogue> getDialogues();

    String getNPCDialogueName(String id);

    int getBukkitVersion();

    void enableMovement(Player player);

    void disableMovement(Player player);

    boolean canEnableMovement(Player player);

    void saveDialogue(Player player, String name, boolean firstInteraction);

    boolean evaluateConditions(Player player, List<String> conditions, ConditionType type);

    default void saveDialogue(@NotNull Player player, @NotNull Dialogue dialogue, boolean firstInteraction) {
        saveDialogue(player, dialogue.getName(), firstInteraction);
    }

    Pattern getLineRegex();

    HologramLibrary getHologramLibrary();
}
