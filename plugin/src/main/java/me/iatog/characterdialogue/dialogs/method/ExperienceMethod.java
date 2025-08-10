package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;

public class ExperienceMethod extends DialogMethod<CharacterDialoguePlugin> {
    public ExperienceMethod(CharacterDialoguePlugin provider) {
        super("experience", provider);
        addConfigurationType("action", ConfigurationType.TEXT);
        addConfigurationType("amount", ConfigurationType.FLOAT);
    }

    @Override
    public void execute(MethodContext context) {
        var player = context.getPlayer();
        var config = context.getConfiguration();
        String action = config.getString("action");
        int amount = config.getInteger("amount", 1);
        ExperienceActions actionEnum = null;

        try {
            actionEnum = ExperienceActions.valueOf(action.toUpperCase());
        } catch(Exception ex) {
            getProvider().getLogger().severe("Experience action '" + action + "' in " + context.getSession().getDialogue().getName() + " is not valid");
            context.next();
            return;
        }

        switch(actionEnum) {
            case GIVE -> player.giveExp(amount);
            case GIVE_LEVELS -> player.giveExpLevels(amount);
            case SET -> player.setExp(amount);
        }
        
        context.next();
    }

    private enum ExperienceActions {
        GIVE,
        GIVE_LEVELS,
        SET;
    }
}
