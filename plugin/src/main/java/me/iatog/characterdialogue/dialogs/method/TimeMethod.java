package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;

public class TimeMethod extends DialogMethod<CharacterDialoguePlugin> {

    public TimeMethod(CharacterDialoguePlugin provider) {
        super("time", provider);
        addConfigurationType("time", ConfigurationType.INTEGER);
        addConfigurationType("relative", ConfigurationType.BOOLEAN);
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration config = context.getConfiguration();
        long time = config.getInteger("time", 0);
        boolean relative = config.getBoolean("relative", false);

        context.getPlayer().setPlayerTime(time, relative);
        context.next();
    }
}
