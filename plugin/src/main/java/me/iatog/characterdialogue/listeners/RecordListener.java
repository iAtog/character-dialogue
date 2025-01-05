package me.iatog.characterdialogue.listeners;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.command.RecordCommand;
import me.iatog.characterdialogue.path.PathRecorder;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class RecordListener implements Listener {

    private final CharacterDialoguePlugin main;

    public RecordListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if(RecordCommand.recorders.containsKey(player.getUniqueId())) {
            PathRecorder recorder = RecordCommand.recorders.get(player.getUniqueId());
            if(recorder.isRecording()) {
                event.setCancelled(true);
                recorder.stopRecording(true);

                AdventureUtil.sendActionBar(player, "<red>Recording saved.");
                AdventureUtil.sendMessage(player, main.language(true, "command.record.saved", recorder.getName()));
            }
        }
    }
}
