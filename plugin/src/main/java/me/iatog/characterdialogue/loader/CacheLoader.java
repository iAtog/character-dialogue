package me.iatog.characterdialogue.loader;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.database.DialogPersistence;
import me.iatog.characterdialogue.dialogs.choice.*;
import me.iatog.characterdialogue.dialogs.method.*;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceMethod;
import me.iatog.characterdialogue.dialogs.method.conditional.ConditionalMethod;
import me.iatog.characterdialogue.dialogs.method.conditionalevents.ConditionalEventsMethod;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod;
import me.iatog.characterdialogue.dialogs.method.talk.TalkMethod;
import me.iatog.characterdialogue.libraries.Cache;
import me.iatog.characterdialogue.session.DialogSession;

public class CacheLoader implements Loader {

    private final CharacterDialoguePlugin main;

    public CacheLoader(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public void load() {
        Cache cache = main.getCache();

        main.registerMethods(
              new SendMethod(),
              new SoundMethod(main),
              new BroadcastMethod(main),
              new WaitMethod(main),
              new CommandMethod(main),
              new TeleportMethod(),
              new EffectMethod(main),
              new SneakMethod(main),
              new ConditionalMethod(main),
              new GiveMethod(main),
              new TalkMethod(main),
              new TitleMethod(),

              new StartDialogueMethod(main),
              new NPCControlMethod(main),
              new ChoiceMethod(main),
              new ConditionalEventsMethod(main),
              new RemoveItemMethod(main),
              new ActionBarMethod(),

              new ExperienceMethod(main),
              new WeatherMethod(main),
              new TimeMethod(main)
        );

        main.registerChoices(
              new ContinueChoice(),
              new DestroyChoice(),
              new RunMethodChoice(main),
              new StartDialogChoice(main)
        );

        main.getLogger().info("Correctly loaded " + cache.getChoices().size() + " choices.");
        main.getLogger().info("Correctly loaded " + cache.getMethods().size() + " methods.");
    }

    @Override
    public void unload() {
        DialogPersistence persistence = main.getServices().getDialogPersistence();
        main.getCache().getDialogSessions().values().stream()
              .filter(DialogSession::isPersistent).forEach(persistence::saveSession);

        main.getCache().clearAll();
    }
}
