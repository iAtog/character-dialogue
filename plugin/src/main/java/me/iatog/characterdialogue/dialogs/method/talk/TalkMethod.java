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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TalkMethod extends DialogMethod<CharacterDialoguePlugin> implements Listener {

    // List of players to wait sneak
    private final List<UUID> players;

    public static final String line = Strings.repeat(" ", 80);
    public static String[] emptyList = new String[0];

    /**
     * DESIGN:
     * talk: <type>|<message>
     * talk: message|Lorem ipsum
     * talk: full_chat|Lorem ipsum
     * <p>
     * NEW
     * talk{type='action_bar',sound='BLOCK_SCULK_VEIN_BREAK',volume=0.5,pitch=0.5,tickSpeed=2,skip=false}: Hello
     */
    public TalkMethod(CharacterDialoguePlugin main) {
        super("talk", main);
        this.players = new ArrayList<>();
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
        Player player = context.getPlayer();
        this.players.add(player.getUniqueId());

        animateMessage(player, context.getSession(), context.getConfiguration(), context.getConsumer());
    }

    public void animateMessage(Player player,
                               DialogSession session,
                               MethodConfiguration configuration,
                               SingleUseConsumer<CompletedType> completed) {
        String message = configuration.getArgument();
        UUID uuid = player.getUniqueId();
        final String npcName = configuration.getString("name", session.getDialogue().getDisplayName());
        final String translatedMessage = Placeholders.translate(player, message);

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
            getProvider().getLogger().severe("The line L" + session.getCurrentIndex() + " in " + session.getDialogue().getName() + " is not valid. (parse error)");
            session.sendDebugMessage("Error parsing data: " + ex.getMessage(), "TalkMethod:60");
            completed.accept(CompletedType.DESTROY);
            return;
        }

        if (message.isEmpty()) {
            getProvider().getLogger().severe("The line L" + session.getCurrentIndex() + " in " + session.getDialogue().getName() + " is not valid. (empty message)");
            session.sendDebugMessage("Error parsing data: message is empty", "TalkMethod:80");
            completed.accept(CompletedType.DESTROY);
            return;
        }
        String color = configuration.getString("color", "<gray>");
        new TalkRunnable(
              players, uuid, message, skip,
              type, player, sound, translatedMessage,
              volume, pitch, session,
              completed, npcName, getProvider(),
              color
        ).runTaskTimer(getProvider(), 20L, ticks);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        players.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!players.contains(player.getUniqueId())) {
            return;
        }

        players.remove(player.getUniqueId());
    }
}
