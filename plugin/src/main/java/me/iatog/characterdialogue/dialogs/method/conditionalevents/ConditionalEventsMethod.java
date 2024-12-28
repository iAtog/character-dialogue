package me.iatog.characterdialogue.dialogs.method.conditionalevents;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConditionalEventsMethod extends DialogMethod<CharacterDialoguePlugin> {

    public static final Map<UUID, EventData> cache = new HashMap<>();

    // It will wait for a ConditionalEvents event to be fulfilled
    // if not fulfilled the dialog ends, and if fulfilled the dialog continues

    // conditional_events{timeout=<seconds>, action=<action_name>}: <event_name>
    // conditional_events: event_name (ACTION = default - TIMEOUT = 60)
    // conditional_events{pause=false}: <event_name> (PAUSE THE DIALOGUE BUT KEEPING THE SLOW EFFECT)

    public ConditionalEventsMethod(CharacterDialoguePlugin provider) {
        super("conditional_events", provider);
        addPluginDependency("ConditionalEvents", () -> {
            Bukkit.getPluginManager().registerEvents(new ConditionalEventListener(), provider);
            handleTimeouts();
        });

        addConfigurationType("action", ConfigurationType.TEXT);
        addConfigurationType("timeout", ConfigurationType.INTEGER);
        addConfigurationType("pause", ConfigurationType.BOOLEAN);

        setDescription("Wait for a ConditionalEvents event to execute.");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        DialogSession session = context.getSession();
        int timeoutInMillis = (configuration.getInteger("timeout", 60) * 1000);
        String argument = configuration.getArgument();
        String action = configuration.getString("action", "default");
        String onTimeout = configuration.getString("onTimeout", "send: &cYou took a long time");

        if (argument.isEmpty()) {
            getProvider().getLogger().warning("No ConditionalEvents event provided.");
            context.next();
            return;
        }

        long exp = (System.currentTimeMillis() + timeoutInMillis);
        boolean pause = configuration.getBoolean("pause", true);

        EventData data = new EventData(argument, session, exp, context.getConsumer(), pause, action, onTimeout);
        cache.put(context.getPlayer().getUniqueId(), data);
        if (pause) {
            context.pause();
        }

        session.sendDebugMessage("Waiting for ConditionalEvents response...", "ConditionalEventsMethod");
    }

    private void handleTimeouts() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(getProvider(), () -> {
            for (EventData data : cache.values()) {
                long expiration = data.expTime();
                String line = data.onTimeout();

                if (System.currentTimeMillis() >= expiration) {
                    if (! line.isEmpty()) {
                        Dialogue dialogue = data.getSession().getDialogue();
                        getProvider().getApi().runDialogueExpression(data.getSession().getPlayer(), line,
                              dialogue.getDisplayName(), SingleUseConsumer.create(t -> {
                              }),
                              data.getSession(), data.getSession().getNPC());
                    }

                    if (! data.consumer().executed()) {
                        data.consumer().accept(CompletedType.DESTROY);
                    } else {
                        data.getSession().destroy();
                    }

                    cache.remove(data.getSession().getPlayer().getUniqueId());
                }
            }
        }, 10, 20);
    }
}
