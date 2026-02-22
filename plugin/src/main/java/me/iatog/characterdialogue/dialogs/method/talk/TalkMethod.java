package me.iatog.characterdialogue.dialogs.method.talk;

import com.google.common.base.Strings;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TalkMethod extends DialogMethod<CharacterDialoguePlugin> {

    public static final String line = Strings.repeat(" ", 80);
    public static String[] emptyList = new String[0];
    public TalkMethod(CharacterDialoguePlugin main) {
        super("talk", main);
        emptyList = TalkType.getEmptyList();

        addConfigurationType("type", ConfigurationType.TEXT);
        addConfigurationType("name", ConfigurationType.TEXT);
        addConfigurationType("sound", ConfigurationType.TEXT);
        addConfigurationType("volume", ConfigurationType.FLOAT);
        addConfigurationType("pitch", ConfigurationType.FLOAT);
        addConfigurationType("tickSpeed", ConfigurationType.INTEGER);
        addConfigurationType("skip", ConfigurationType.BOOLEAN);

        setDescription("Performs a speech animation to the player");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        DialogSession session = context.getSession();
        SingleUseConsumer<CompletedType> completed = context.getConsumer();
        Player player = context.getPlayer();
        String message = Placeholders.translate(player, configuration.getArgument());
        String npcName = configuration.getString("name", session.getDialogue().getDisplayName());

        float volume = configuration.getFloat("volume", 0.5f);
        float pitch = configuration.getFloat("pitch", 0.5f);
        boolean skip = configuration.getBoolean("skip", false);
        int ticks = configuration.getInteger("tickSpeed", 2);

        TalkType type;
        Sound sound;

        try {
            type = TalkType.valueOf(configuration.getString("type", "action_bar").toUpperCase());
            sound = Sound.valueOf(configuration.getString("sound", "BLOCK_STONE_BUTTON_CLICK_OFF").toUpperCase());
        } catch (EnumConstantNotPresentException ex) {
            getProvider().getLogger().severe(error(session, "invalid type/sound"));
            session.sendDebugMessage("Error parsing data: " + ex.getMessage(), "TalkMethod:55");
            context.destroy();
            return;
        }

        if (message.isEmpty()) {
            getProvider().getLogger().severe(error(session, "empty message"));
            session.sendDebugMessage("Error parsing data: message is empty", "TalkMethod:64");
            context.destroy();
            return;
        }

        TalkData data = new TalkData(player, sound, volume, pitch, skip, type, npcName, message, session, completed, getProvider());
        new TalkRunnable(data).runTaskTimer(getProvider(), 20L, ticks);
    }

    private String error(DialogSession s, String m) {
        return "The line L" + s.getCurrentIndex() + " in " + s.getDialogue().getName() + " is not valid. (" + m + ")";
    }

}
