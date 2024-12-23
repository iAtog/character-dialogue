package me.iatog.characterdialogue.command;

import com.google.gson.Gson;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.path.Record;
import me.iatog.characterdialogue.path.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.iatog.characterdialogue.util.TextUtils.colorize;

@Command(
      names = "record"
)
public class RecordCommand implements CommandClass {

    public static final Map<UUID, PathRecorder> recorders = new HashMap<>();
    private final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
    private final Gson gson;
    private final PathStorage storage;

    public RecordCommand() {
        this.gson = new Gson();
        storage = main.getPathStorage();
    }

    @Command(
          names = "start",
          permission = "characterdialogue.command.path.create",
          desc = "Start recording the path"
    )
    public void startRecording(@Sender Player sender, String name) {
        if(isPresent(sender)) {
            sender.sendMessage(colorize("&cYou are already in a recording, use &7/characterd record stop &cto stop it."));
            return;
        }

        PathRecorder recorder = new PathRecorder(main, sender, gson);
        recorder.setName(name);
        recorders.put(sender.getUniqueId(), recorder);

        new BukkitRunnable() {
            int second = 0;
            @Override
            public void run() {
                if(second == 5) {
                    recorder.startRecording();
                    this.cancel();
                } else {
                    sender.sendMessage(colorize("&aStarting in " + (5 - second) + "s"));
                    second++;
                }
            }
        }.runTaskTimer(main, 0, 20);
    }

    @Command(
          names = "stop"
    )
    public void stopRecording(@Sender Player sender) {
        if(!isPresent(sender)) {
            sender.sendMessage(colorize("&cYou are not currently on a recording."));
            return;
        }

        PathRecorder recorder = recorders.get(sender.getUniqueId());
        recorder.stopRecording(true);
        sender.sendMessage(colorize("&aRecording &8'&c" + recorder.getName() + "&8'&a has been saved."));
    }

    @Command(
          names = "view"
    )
    public void recordData(@Sender Player sender, Record record) {
        if(record == null) {
            sender.sendMessage(colorize("&cRecord not found."));
            return;
        }

        List<RecordLocation> path = record.locations();
        int totalPoints = path.size();
        double durationSeconds = totalPoints / 20.0;
        RecordLocation firstPoint = path.getFirst();
        RecordLocation lastPoint = path.getLast();

        sender.sendMessage(colorize("&eRecord&f: &c&l" + record.name()));
        sender.sendMessage(colorize("&eTotal Points&8: &f" + totalPoints));
        sender.sendMessage(colorize("&eFirst Point&8: " + formatLocation(firstPoint)));
        sender.sendMessage(colorize("&eLast Point&8: " + formatLocation(lastPoint)));
        sender.sendMessage(colorize("&eEstimated duration&8: &a" + String.format("%.2f", durationSeconds) + " seconds"));
    }

    @Command(
          names = "cancel"
    )
    public void cancelRecording(@Sender Player sender) {
        if(!isPresent(sender)) {
            sender.sendMessage(colorize("&cYou are not currently on a recording."));
            return;
        }

        PathRecorder recorder = recorders.get(sender.getUniqueId());
        recorder.stopRecording(false);
        recorders.remove(sender.getUniqueId());
        sender.sendMessage(colorize("&aRecording cancelled (not saved)"));
    }

    @Command(
          names = "replay"
    )
    public void replay(@Sender Player player, Record record, AdaptedNPC npc) {
        if(record == null) {
            player.sendMessage("Record not found.");
            return;
        }
        if(npc == null) {
            player.sendMessage("NPC not found.");
            return;
        }

        PathReplayer replay = new PathReplayer(record.locations(), npc);
        player.sendMessage(colorize("&aStarting recorded replay."));
        replay.startReplay();
    }

    private boolean isPresent(Player player) {
        return recorders.containsKey(player.getUniqueId());
    }

    private String formatLocation(RecordLocation location) {
        return String.format("&7World: %s, X: %.2f, Y: %.2f, Z: %.2f, Yaw: %.2f, Pitch: %.2f",
              location.getWorldName(), location.getX(), location.getY(), location.getZ(),
              location.getYaw(), location.getPitch());
    }

}
