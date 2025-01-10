package me.iatog.characterdialogue.command;

import com.google.gson.Gson;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.command.object.CSubCommand;
import me.iatog.characterdialogue.path.Record;
import me.iatog.characterdialogue.path.*;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Command(
      names = "record"
)
public class RecordCommand extends CSubCommand implements CommandClass {

    public static final Map<UUID, PathRecorder> recorders = new HashMap<>();
    private final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
    private final Gson gson;
    private final PathStorage storage = main.getPathStorage();

    public RecordCommand() {
        super();
        this.gson = new Gson();
    }

    public void addCommands() {
        addCommand("characterd record start", "<name>", "Create a new recording");
        addCommand("characterd record stop", "", "Stop current recording");
        addCommand("characterd record cancel", "", "Cancel current recording");
        addCommand("characterd record view", "<name>", "View recording info");
        addCommand("characterd record delete", "<name>", "Delete an recording.");
        addCommand("characterd record replay", "<name> <npc>", "Replay recording on a npc");
    }

    @Command(names = "", desc = "Main command")
    public void mainCommand(CommandSender sender) {
        mainCommandLogic(main, sender);
    }

    @Usage("<newName>")
    @Command(
          names = "start",
          permission = "characterdialogue.command.replay",
          desc = "Start recording the path"
    )
    public void startRecording(@Sender Player sender, String name) {
        if(isPresent(sender)) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.in-recording"));
            return;
        }

        if(storage.getAllPaths().containsKey(name)) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.already-exists"));
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
                    AdventureUtil.sendMessage(sender, main.language(true, "command.record.guide"));
                    this.cancel();
                } else {
                    AdventureUtil.sendMessage(sender, main.language(true, "command.record.starting", String.valueOf(5 - second)));
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
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.not-recording"));
            return;
        }

        PathRecorder recorder = recorders.get(sender.getUniqueId());
        recorder.stopRecording(true);
        AdventureUtil.sendMessage(sender, main.language(true, "command.record.saved", recorder.getName()));
    }

    @Usage("<name>")
    @Command(
          names = "view",
          permission = "characterdialogue.command.record.view"
    )
    public void recordData(CommandSender sender, Record record) {
        if(record == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.not-found"));
            return;
        }

        List<RecordLocation> path = record.locations();
        int totalPoints = path.size();
        double durationSeconds = totalPoints / 20.0;
        RecordLocation firstPoint = path.getFirst();
        RecordLocation lastPoint = path.getLast();

        AdventureUtil.sendMessage(sender, main.language("command.record.view.record", record.name()));
        AdventureUtil.sendMessage(sender, main.language("command.record.view.total", totalPoints));
        AdventureUtil.sendMessage(sender, main.language("command.record.view.first", formatLocation(firstPoint)));
        AdventureUtil.sendMessage(sender, main.language("command.record.view.last", formatLocation(lastPoint)));
        AdventureUtil.sendMessage(sender, main.language("command.record.view.duration", String.format("%.2f", durationSeconds)));
    }

    @Usage("<name>")
    @Command(
          names = "delete",
          desc = "Delete recording",
          permission = "characterdialogue.command.record.delete"
    )
    public void deleteRecording(CommandSender sender, Record record) {
        if(record == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.not-found"));
            return;
        }

        storage.removePath(record.name());
        AdventureUtil.sendMessage(sender, main.language(true, "command.record.deleted", record.name()));
    }

    @Command(
          names = "cancel",
          permission = "characterdialogue.command.replay"
    )
    public void cancelRecording(@Sender Player sender) {
        if(!isPresent(sender)) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.not-recording"));
            return;
        }

        PathRecorder recorder = recorders.get(sender.getUniqueId());
        recorder.stopRecording(false);
        recorders.remove(sender.getUniqueId());
        AdventureUtil.sendMessage(sender, main.language(true, "command.record.cancelled"));
    }

    @Usage("<name> <npc>")
    @Command(
          names = "replay",
          permission = "characterdialogue.command.viewreplay"
    )
    public void replay(@Sender Player sender, Record record, AdaptedNPC npc) {
        if(record == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.not-found"));
            return;
        }

        if(npc == null) {
            AdventureUtil.sendMessage(sender, main.language(true, "command.record.no-npc"));
            return;
        }

        PathReplayer replay = new PathReplayer(record.locations(), npc);
        AdventureUtil.sendMessage(sender, main.language(true, "command.record.replaying", npc.getName()));
        replay.startReplay(sender);
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
