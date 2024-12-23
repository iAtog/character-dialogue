package me.iatog.characterdialogue.session;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.Choice;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.api.interfaces.Session;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class ChoiceSession implements Session {

    private final CharacterDialoguePlugin main;
    private final Player player;
    private final UUID uuid;
    private final Map<Integer, Choice> choices;
    private boolean destroyed;

    public ChoiceSession(CharacterDialoguePlugin main, Player player) {
        this.main = main;
        this.player = player;
        this.uuid = UUID.randomUUID();
        this.choices = new TreeMap<>();
    }

    public Choice getChoice(int index) {
        return choices.get(index);
    }

    public boolean addChoice(int index, String message, Class<? extends DialogChoice> clazz, String argument) {
        if (choices.containsKey(index) || destroyed) {
            return false;
        }

        choices.put(index, new Choice(index, message, clazz, argument));
        return true;
    }

    public boolean addChoice(String message, Class<? extends DialogChoice> clazz, String argument) {
        return addChoice(choices.size() + 1, message, clazz, argument);
    }

    public boolean addChoice(String message, Class<? extends DialogChoice> clazz) {
        return addChoice(choices.size() + 1, message, clazz, "");
    }

    public boolean check() {
        return false;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<Integer, Choice> getChoices() {
        return choices;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public void destroy() {
        main.getCache().getChoiceSessions().remove(player.getUniqueId());
        this.destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
