package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;

public class TitleMethod extends DialogMethod<CharacterDialoguePlugin> {

    public TitleMethod() {
        super("title");
        addConfigurationType("title", ConfigurationType.TEXT);
        addConfigurationType("subtitle", ConfigurationType.TEXT);
        addConfigurationType("fadeIn", ConfigurationType.INTEGER);
        addConfigurationType("stay", ConfigurationType.INTEGER);
        addConfigurationType("fadeOut", ConfigurationType.INTEGER);

        setDescription("Displays a title/subtitle to the player");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        String title = configuration.getString("title");
        String subtitle = configuration.getString("subtitle", "");
        int fadeIn = configuration.getInteger("fadeIn", 20);
        int stay = configuration.getInteger("stay", 60);
        int fadeOut = configuration.getInteger("fadeOut", 20);

        context.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        context.next();
    }
}
