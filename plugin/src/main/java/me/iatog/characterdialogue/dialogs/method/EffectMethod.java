package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class EffectMethod extends DialogMethod<CharacterDialoguePlugin> {


    public EffectMethod(CharacterDialoguePlugin main) {
        super("effect", main);
        addConfigurationType("seconds", ConfigurationType.INTEGER);
        addConfigurationType("amplifier", ConfigurationType.INTEGER);
        addConfigurationType("clear", ConfigurationType.BOOLEAN);
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        Player player = context.getPlayer();
        DialogSession session = context.getSession();

        if (configuration.getArgument().equalsIgnoreCase("clear")) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            context.next();
            return;
        }

        if (configuration.getArgument().isEmpty()) {
            String msg = "No potion effects have been specified in L" + session.getCurrentIndex() + ", skipping...";
            getProvider().getLogger().warning(msg);
            session.sendDebugMessage(msg, "EffectMethod");
            context.next();
            return;
        }

        String[] effects = configuration.getArgument().split(",");

        int duration = configuration.getInteger("seconds", 10) * 20;
        int amplifier = configuration.getInteger("amplifier", 1);
        boolean clear = configuration.getBoolean("clear", false);

        for (String effectName : effects) {
            Optional<PotionEffectType> effect = Optional.ofNullable(PotionEffectType.getByName(effectName.trim().toUpperCase()));
            if (effect.isEmpty()) {
                String msg = "The name of the \"" + effectName + "\" effect has not been found.";
                getProvider().getLogger().warning(msg);
                session.sendDebugMessage(msg, "EffectMethod");
                continue;
            }

            if (clear) {
                player.removePotionEffect(effect.get());
            } else {
                player.addPotionEffect(new PotionEffect(effect.get(), duration, amplifier));
            }
        }

        context.next();
    }
}
