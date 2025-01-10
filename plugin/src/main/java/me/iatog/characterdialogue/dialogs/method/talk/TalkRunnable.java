package me.iatog.characterdialogue.dialogs.method.talk;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class TalkRunnable extends BukkitRunnable {

    private final List<UUID> players;
    private final UUID uuid;
    private final String message;
    private final boolean skip;
    private final TalkType type;
    private final Player player;
    private final Sound sound;
    private final String translatedMessage;
    private final float volume;
    private final float pitch;
    private final DialogSession session;
    private final SingleUseConsumer<CompletedType> completed;
    private final String npcName;
    private final CharacterDialoguePlugin provider;

    private int index = 0;
    private boolean finished = false;
    private final String color;

    public TalkRunnable(List<UUID> players, UUID uuid, String message, boolean skip, TalkType type, Player player, Sound sound, String translatedMessage, float volume, float pitch, DialogSession session, SingleUseConsumer<CompletedType> completed, String npcName, CharacterDialoguePlugin provider, String color) {
        this.players = players;
        this.uuid = uuid;
        this.message = message;
        this.skip = skip;
        this.type = type;
        this.player = player;
        this.sound = sound;
        this.translatedMessage = translatedMessage;
        this.volume = volume;
        this.pitch = pitch;
        this.session = session;
        this.completed = completed;
        this.npcName = npcName;
        this.provider = provider;
        this.color = color;
    }

    @Override
    public void run() {
        if(Bukkit.getPlayer(uuid) == null) {
            this.cancel();
            players.remove(uuid);
            if(!completed.executed()) {
                completed.accept(CompletedType.DESTROY);
            }
            return;
        }

        try {
            if ((!players.contains(uuid) && skip && ! isCancelled()) && ! finished) {
                stopAnimation();
                return;
            }
            if (index < message.length()) {
                animateText();
            } else {
                cancel();
                if (!finished) {
                    finishAnimation();
                }
            }
        } catch (NullPointerException ex) {
            handleException(ex);
        }
    }

    private void stopAnimation() {
        this.cancel();
        this.finished = true;
        type.execute(player, translatedMessage, npcName, color);
        player.playSound(player.getLocation(), sound, volume, pitch);
        session.sendDebugMessage("Finished talk because: " + ! players.contains(uuid) + " | " + skip + " | " + ! isCancelled(), "TalkMethod:134");
        players.remove(uuid);
        completed.accept(CompletedType.CONTINUE);
    }

    private void animateText() {
        String writingMessage = translatedMessage.substring(0, index + 1);
        char currentChar = translatedMessage.charAt(index);
        index++;

        if (currentChar != ' ' && currentChar != ',' && currentChar != '.') {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

        type.execute(player, writingMessage, npcName, color);
    }

    private void finishAnimation() {
        if (!this.finished) {
            completed.accept(CompletedType.CONTINUE);
            session.sendDebugMessage("Starting next... (else) (finishAnimation)", "TalkMethod:149");
        }

        this.finished = true;
        session.sendDebugMessage("Finished because message " + index + " < " + message.length() +
              " (cancelled: " + isCancelled() + ")", "TalkMethod:152");
        players.remove(uuid);
        this.cancel();
    }

    private void handleException(Exception ex) {
        provider.getLogger().severe("exception in talkMethod [" + message + "]");
        ex.printStackTrace();
        completed.accept(CompletedType.DESTROY);
        this.cancel();
        this.finished = true;
    }
}
