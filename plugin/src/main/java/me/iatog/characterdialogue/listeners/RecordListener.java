package me.iatog.characterdialogue.listeners;

import me.iatog.characterdialogue.command.RecordCommand;
import me.iatog.characterdialogue.path.PathRecorder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import static me.iatog.characterdialogue.util.TextUtils.colorize;

public class RecordListener implements Listener {

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if(RecordCommand.recorders.containsKey(player.getUniqueId())) {
            PathRecorder recorder = RecordCommand.recorders.get(player.getUniqueId());
            if(recorder.isRecording()) {
                event.setCancelled(true);
                recorder.stopRecording(true);
                player.spigot().sendMessage(
                      ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorize("&cRecording saved."))
                );

                player.sendMessage(colorize("&aRecording &8'&c" + recorder.getName() + "&8'&a has been saved."));
            }
        }
    }

}
