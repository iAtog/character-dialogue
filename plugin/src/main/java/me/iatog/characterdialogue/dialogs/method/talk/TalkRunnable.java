package me.iatog.characterdialogue.dialogs.method.talk;

import me.iatog.characterdialogue.enums.CompletedType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TalkRunnable extends BukkitRunnable implements Listener {

    private final TalkData data;
    private int index = 0;
    private boolean finished = false;
    private boolean isSkipping = false;

    public TalkRunnable(TalkData data) {
        this.data = data;
        data.provider().getServer().getPluginManager().registerEvents(this, data.provider());
    }

    @Override
    public void run() {
        if (!data.player().isOnline()) {
            cleanup(CompletedType.DESTROY);
            return;
        }

        if (isSkipping && data.skip()) {
            finishImmediately();
            return;
        }

        if (index < data.translatedMessage().length()) {
            animateText();
        } else {
            finishNaturally();
        }
    }

    private void cleanup(CompletedType type) {
        if (this.finished) return;
        this.finished = true;
        this.cancel();

        HandlerList.unregisterAll(this);
        
        if (!data.completed().executed()) {
            data.completed().accept(type);
        }
    }

    private void finishImmediately() {
        data.type().execute(data.player(), data.translatedMessage(), data.npcName());
        data.player().playSound(data.player().getLocation(), data.sound(), data.volume(), data.pitch());
        
        data.session().sendDebugMessage("Skipped animation", "TalkRunnable");
        cleanup(CompletedType.CONTINUE);
    }

    private void finishNaturally() {
        data.session().sendDebugMessage("Finished animation naturally", "TalkRunnable");
        cleanup(CompletedType.CONTINUE);
    }

    private void animateText() {
        index = calcNextIndex(index);

        String writingMessage = data.translatedMessage().substring(0, index + 1);
        char currentChar = data.translatedMessage().charAt(index);
        index++;

        if (currentChar != ' ' && currentChar != ',' && currentChar != '.') {
            data.player().playSound(data.player().getLocation(), data.sound(), data.volume(), data.pitch());
        }

        data.type().execute(data.player(), writingMessage, data.npcName());
    }

    private int calcNextIndex(int currentIndex) {
        String msg = data.translatedMessage();
        if (currentIndex < msg.length() && msg.charAt(currentIndex) == '<') {
            int closingIndex = msg.indexOf('>', currentIndex);
            if (closingIndex != -1) {
                return closingIndex;
            }
        }
        return currentIndex;
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId().equals(data.player().getUniqueId())) {
            cleanup(CompletedType.DESTROY);
        }
    }

    @EventHandler
    public void sneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer().getUniqueId().equals(data.player().getUniqueId()) && event.isSneaking()) {
            this.isSkipping = true;
        }
    }

}
