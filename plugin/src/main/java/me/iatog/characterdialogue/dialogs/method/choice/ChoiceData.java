package me.iatog.characterdialogue.dialogs.method.choice;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.entity.Player;

public class ChoiceData {
    private final ChoiceSession choiceSession;
    private final DialogSession dialogSession;
    private final Player player;
    private final YamlDocument configFile;

    public ChoiceData(ChoiceSession choiceSession, DialogSession dialogSession, Player player, YamlDocument configFile) {
        this.choiceSession = choiceSession;
        this.dialogSession = dialogSession;
        this.player = player;
        this.configFile = configFile;
    }

    public ChoiceSession getChoiceSession() {
        return choiceSession;
    }

    public DialogSession getDialogSession() {
        return dialogSession;
    }

    public Player getPlayer() {
        return player;
    }

    public YamlDocument getConfigFile() {
        return configFile;
    }
}
