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
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Random;

public class AdaptedNPCListener implements Listener {

    private final CharacterDialoguePlugin main;
    private final Random random;

    public AdaptedNPCListener(CharacterDialoguePlugin main) {
        this.main = main;
        this.random = new Random();
    }

    @EventHandler
    public void onInteract(AdapterNPCInteractEvent event) {
        CharacterDialogueAPI api = main.getApi();
        Player player = event.getPlayer();
        AdaptedNPC npc = event.getNPC();
        YamlDocument config = main.getFileFactory().getConfig();
        String route = "npc." + npc.getId();

        if(!config.contains(route)) {
            return;
        }

        String dialogues = config.getString(route);
        Dialogue dialogue = null;

        if(dialogues.contains(",")) {
            String[] split = dialogues.split(",");
            int index = random.nextInt(split.length);
            dialogue = main.getCache().getDialogues().get(split[index]);
        } else {
            dialogue = main.getCache().getDialogues().get(dialogues);
        }

        if (dialogue == null || main.getCache().getDialogSessions().containsKey(player.getUniqueId())) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (player.hasMetadata("dialogueCooldown")) {
            long cooldown = player.getMetadata("dialogueCooldown").getFirst().asLong();
            if (currentTime < cooldown) {
                AdventureUtil.sendMessage(player, main.language("cooldown-message"));
                return;
            }
        }

        long cooldownTime = (config.getInt("cooldown-time", 2) * 1000);
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
                    AdventureUtil.sendMessage(player,
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

}
