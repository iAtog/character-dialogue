package me.iatog.characterdialogue.dialogs.method.talk;

import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.Sound;

public class TalkConfiguration {
    private final DialogSession session;
    private String message;
    private TalkType type;
    private long tickSpeed = 2;
    private Sound sound = Sound.BLOCK_STONE_BUTTON_CLICK_OFF;
    private float volume = 0.5f;
    private float pitch = 0.5f;
    private boolean skippable = false;

    public TalkConfiguration(DialogSession session) {
        this.session = session;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TalkType getType() {
        return type;
    }

    public void setType(TalkType type) {
        this.type = type;
    }

    public DialogSession getSession() {
        return session;
    }

    public long getTickSpeed() {
        return tickSpeed;
    }

    public void setTickSpeed(long tickSpeed) {
        this.tickSpeed = tickSpeed;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isSkippable() {
        return skippable;
    }

    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }
}