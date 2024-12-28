package me.iatog.characterdialogue.dialogs.method.choice;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.dialogs.method.choice.listener.ChoiceChatTypeListener;
import me.iatog.characterdialogue.enums.ChoiceType;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChoiceMethod extends DialogMethod<CharacterDialoguePlugin> {

    public static String COMMAND_NAME = "/;;$/5-choice";

    // 'choice{type=chat, timeout=15}: choice_sample'
    // 'choice{type=bedrock, timeout=20}: choice_sample'
    // 'choice{type=gui, timeout=0}: no_cooldown_choice'
    public static final Map<UUID, BukkitTask> taskList= new HashMap<>();

    private final Map<UUID, ChoiceSession> sessions;
    private final boolean floodgateEnabled;

    public ChoiceMethod(CharacterDialoguePlugin provider) {
        super("choice", provider);
        this.sessions = provider.getCache().getChoiceSessions();
        this.floodgateEnabled = Bukkit.getPluginManager().isPluginEnabled("floodgate");

        addConfigurationType("type", ConfigurationType.TEXT);
        addConfigurationType("timeout", ConfigurationType.INTEGER);

        Bukkit.getPluginManager().registerEvents(new ChoiceChatTypeListener(provider), provider);

        setDescription("Displays options to the player");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        DialogSession dialogSession = context.getSession();
        Player player = context.getPlayer();
        YamlDocument configFile = getProvider().getFileFactory().getConfig();

        String selectedChoice = configuration.getArgument();
        int cooldown = configuration.getInteger("timeout", 30);

        if (!ChoiceUtil.isContextValid(context)) {
            context.next();
            return;
        }

        String type = configuration.getString("type", "chat").toUpperCase();
        ChoiceType choiceType;

        try {
            choiceType = ChoiceType.valueOf(type);
        } catch (Exception exception) {
            String msg = "Invalid choice type provided \"" + type + "\" in \"" +
                  dialogSession.getDialogue().getName() + "\" dialogue.";
            getProvider().getLogger().warning(msg);
            dialogSession.sendDebugMessage(msg, "ChoiceMethod");
            context.next();
            return;
        }

        if (choiceType == ChoiceType.BEDROCK_GUI && ! floodgateEnabled) {
            String msg = "\"BEDROCK_GUI\" choice type cannot be used, geyser " +
                  "and floodgate are not present on the plugins folder.";
            dialogSession.sendDebugMessage(msg, "ChoiceMethod");
            getProvider().getLogger().warning(msg);
            choiceType = ChoiceType.CHAT;
        }

        ChoiceSession choiceSession = new ChoiceSession(getProvider(), player);

        context.pause();

        ChoiceUtil.addChoices(choiceSession, selectedChoice);
        sessions.put(player.getUniqueId(), choiceSession);

        ChoiceData data = new ChoiceData(
              choiceSession,
              dialogSession,
              player,
              configFile
        );

        choiceType.generateQuestions(data);

        if (cooldown > 0) {
            ChoiceUtil.manageCooldown(data, cooldown, choiceType.getCloseAction());
        }
    }
}
