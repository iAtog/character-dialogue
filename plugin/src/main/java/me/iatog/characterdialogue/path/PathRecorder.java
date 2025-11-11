package me.iatog.characterdialogue.path;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.command.RecordCommand;
import me.iatog.characterdialogue.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.KeybindComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PathRecorder {

    private final CharacterDialoguePlugin main;
    private final Gson gson;
    private final UUID playerId;
    private String name;

    private List<RecordLocation> paths;
    private boolean recording;
    private BukkitTask task;
    private long startTime;
    private int ticks;

    public PathRecorder(CharacterDialoguePlugin main, Player player, Gson gsonInstance) {
        this.main = main;
        this.paths = new ArrayList<>();
        this.gson = gsonInstance;
        this.playerId = player.getUniqueId();
    }

    public void startRecording() {
        if(recording || task != null) {
            return;
        }
        String message = main.language("command.record.action-bar");

        startTime = System.currentTimeMillis();
        recording = true;
        this.task = Bukkit.getScheduler().runTaskTimer(main, () -> {
            if(recording) {
                int elapsed = (int) ((System.currentTimeMillis() - startTime) / 1000);
                int seconds = elapsed == 0 ? 1 : elapsed;
                paths.add(new RecordLocation(player().getLocation(), player().isSneaking()));
                if(ticks % 20 == 0) {
                    Component component = AdventureUtil.minimessage(
                            message,
                            AdventureUtil.placeholder("seconds", seconds+"")
                    );

                    AdventureUtil.sendActionBar(player(), component);
                }
                ticks++;
            }
        }, 0, 1);
    }

    public void stopRecording(boolean save) {
        if(this.task != null) {
            this.recording = false;
            this.task.cancel();
            this.task = null;

            if(save) {
                save();
            }

            RecordCommand.recorders.remove(playerId);
        }
    }

    private void save() {
        main.getPathStorage().savePath(name, this.paths);
    }

    public String getPathAsJson() {
        return gson.toJson(paths);
    }

    public void loadPathFromJson(String json) {
        paths = gson.fromJson(json, new TypeToken<List<RecordLocation>>(){}.getType());
    }

    private Player player() {
        return Bukkit.getPlayer(playerId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecordLocation> getPaths() {
        return paths;
    }

    public boolean isRecording() {
        return recording;
    }
}
