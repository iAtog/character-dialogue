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
          permission = "characterdialogue.command.replay",
          desc = "Start recording the path"
    )
    public void startRecording(@Sender Player sender, String name) {
        if(isPresent(sender)) {
            sender.sendMessage(main.language(true, "command.record.in-recording"));
            return;
        }

        if(storage.getAllPaths().containsKey(name)) {
            sender.sendMessage(main.language(true, "command.record.already-exists"));
            return;
        }

        PathRecorder recorder = new PathRecorder(main, sender, gson);
        recorder.setName(name);
        recorders.put(sender.getUniqueId(), recorder);

        new BukkitRunnable() {
            int second = 0;
            @Override
            public void run() {
                if(second >= 5) {
                    recorder.startRecording();
                    sender.sendMessage(main.language(true, "command.record.guide"));
                    this.cancel();
                } else {
                    sender.sendMessage(main.language(true, "command.record.starting", String.valueOf(5 - second)));
                    second++;
                }
            }
        }.runTaskTimer(main, 0, 20);
    }

    @Command(
          names = "stop",
          permission = "characterdialogue.command.replay"

    )
    public void stopRecording(@Sender Player sender) {
        if(!isPresent(sender)) {
            sender.sendMessage(main.language(true, "command.record.not-recording"));
            return;
        }

        PathRecorder recorder = recorders.get(sender.getUniqueId());
        recorder.stopRecording(true);
        sender.sendMessage(main.language(true, "command.record.saved"));
    }

    @Command(
          names = "view",
          permission = "characterdialogue.command.replay.view"
    )
    public void recordData(@Sender Player sender, Record record) {
        if(record == null) {
            sender.sendMessage(main.language(true, "command.record.not-found"));
            return;
        }

        List<RecordLocation> path = record.locations();
        int totalPoints = path.size();
        double durationSeconds = totalPoints / 20.0;
        RecordLocation firstPoint = path.getFirst();
        RecordLocation lastPoint = path.getLast();

        sender.sendMessage(main.language("command.record.view.record", record.name()));
        sender.sendMessage(main.language("command.record.view.total", totalPoints));
        sender.sendMessage(main.language("command.record.view.first", formatLocation(firstPoint)));
        sender.sendMessage(main.language("command.record.view.last", formatLocation(lastPoint)));
        sender.sendMessage(main.language("command.record.view.duration", String.format("%.2f", durationSeconds)));
    }

    @Command(
          names = "cancel",
          permission = "characterdialogue.command.replay"
    )
    public void cancelRecording(@Sender Player sender) {
        if(!isPresent(sender)) {
            sender.sendMessage(main.language(true, "command.record.not-recording"));
            return;
        }

        PathRecorder recorder = recorders.get(sender.getUniqueId());
        recorder.stopRecording(false);
        recorders.remove(sender.getUniqueId());
        sender.sendMessage(main.language(true, "command.record.cancelled"));
    }

    @Command(
          names = "replay",
          permission = "characterdialogue.command.viewreplay"
    )
    public void replay(@Sender Player player, Record record, AdaptedNPC npc) {
        if(record == null) {
            player.sendMessage(main.language(true, "command.record.not-found"));
            return;
        }

        if(npc == null) {
            player.sendMessage(main.language(true, "command.record.no-npc"));
            return;
        }

        PathReplayer replay = new PathReplayer(record.locations(), npc);
        player.sendMessage(main.language(true, "command.record.replaying", npc.getName()));
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
