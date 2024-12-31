package me.iatog.characterdialogue.listeners;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.CharacterDialogueAPI;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.api.events.AdapterNPCInteractEvent;
import me.iatog.characterdialogue.api.events.AdapterNPCSpawnEvent;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.method.conditional.ConditionalMethod;
import me.iatog.characterdialogue.enums.ClickType;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class AdaptedNPCListener implements Listener {

    private final CharacterDialoguePlugin main;

    public AdaptedNPCListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(AdapterNPCInteractEvent event) {
        CharacterDialogueAPI api = main.getApi();
        Player player = event.getPlayer();
        AdaptedNPC npc = event.getNPC();
        Dialogue dialogue = api.getNPCDialogue(npc.getId());

        if (dialogue == null || main.getCache().getDialogSessions().containsKey(player.getUniqueId())) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (player.hasMetadata("dialogueCooldown")) {
            long cooldown = player.getMetadata("dialogueCooldown").get(0).asLong();
            if (currentTime < cooldown) {
                player.sendMessage(TextUtils.colorize("&cCalm down."));
                return;
            }
        }

        long cooldownTime = 2000;
        player.setMetadata("dialogueCooldown", new FixedMetadataValue(main, currentTime + cooldownTime));

        ClickType clickType = dialogue.getClickType();

        if (clickType != ClickType.ALL && clickType != event.getClickType()) {
            return;
        }

        Dialogue.DialoguePermission permissions = dialogue.getPermissions();

        if (permissions != null && permissions.getPermission() != null) {
            String permission = permissions.getPermission();
            String message = permissions.getMessage();

            if (!player.hasPermission(permission)) {
                if (message != null) {
                    player.sendMessage(
                          Placeholders.translate(player, message.replace("%npc_name%", dialogue.getDisplayName()))
                    );
                }

                return;
            }
        }

        if (dialogue.isFirstInteractionEnabled() && !api.wasReadedBy(player, dialogue, true)) {
            dialogue.startFirstInteraction(player, true, npc);
            return;
        }

        event.setCancelled(true);
        dialogue.start(player, npc);
    }

    @EventHandler
    public void onSpawn(AdapterNPCSpawnEvent event) {
        AdaptedNPC npc = event.getNPC();
        String id = npc.getId();
        CharacterDialogueAPI api = main.getApi();

        if (api.getNPCDialogue(id) != null) {
            api.loadHologram(id);
        }
    }

    //@EventHandler
    public void hideNPCs(AdapterNPCSpawnEvent event) {
        AdaptedNPC npc = event.getNPC();
        Player player = event.getPlayer();
        YamlDocument config = main.getFileFactory().getConfig();
        String path = "hidden-npcs." + npc.getId() + ".conditions";

        if(player == null || !config.contains(path)) {
            return;
        }

        List<String> conditions = config.getStringList(path);

        if(!conditions.isEmpty()) {
            ConditionalMethod conditionalMethod = getMethod("conditional");

            boolean result = false;

            for(String condition : conditions) {
                if(conditionalMethod.evaluateCondition(player, condition)) {
                    result = true;
                } else {
                    result = false;
                    break;
                }
            }



            event.setCancelled(result);
        }

    }

    @SuppressWarnings("unchecked")
    private <T> T getMethod(String method) {
        return (T) main.getCache().getMethods().get(method);
    }

}
