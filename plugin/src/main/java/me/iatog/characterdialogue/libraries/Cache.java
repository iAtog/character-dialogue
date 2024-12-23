package me.iatog.characterdialogue.libraries;

import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.misc.PlayerData;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Cache {

    private final Map<String, DialogMethod<? extends JavaPlugin>> methods;
    private final Map<String, DialogChoice> choices;
    private final Map<UUID, DialogSession> dialogSessions;
    private final Map<UUID, ChoiceSession> choiceSessions;
    private final Map<String, Dialogue> dialogues;
    private final List<UUID> frozenPlayers;
    private final Map<UUID, PlayerData> playerDataMap;

    public Cache() {
        this.methods = new HashMap<>();
        this.dialogSessions = new HashMap<>();
        this.choiceSessions = new HashMap<>();
        this.choices = new HashMap<>();
        this.dialogues = new HashMap<>();
        this.frozenPlayers = new ArrayList<>();
        this.playerDataMap = new HashMap<>();
    }

    public Map<String, DialogMethod<? extends JavaPlugin>> getMethods() {
        return methods;
    }

    public Map<String, DialogChoice> getChoices() {
        return choices;
    }

    public Map<UUID, DialogSession> getDialogSessions() {
        return dialogSessions;
    }

    public Map<UUID, ChoiceSession> getChoiceSessions() {
        return choiceSessions;
    }

    public Map<String, Dialogue> getDialogues() {
        return dialogues;
    }

    public List<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return playerDataMap;
    }

    public void clearAll() {
        methods.clear();
        dialogSessions.clear();
        choiceSessions.clear();
        dialogues.clear();
    }
}
