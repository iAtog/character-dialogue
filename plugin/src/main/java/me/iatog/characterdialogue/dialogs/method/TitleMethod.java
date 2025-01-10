package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;

import java.time.Duration;

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
        int fadeIn = configuration.getInteger("fadeIn", 1);
        int stay = configuration.getInteger("stay", 3);
        int fadeOut = configuration.getInteger("fadeOut", 1);

        TagResolver tag = AdventureUtil.placeholder("player", context.getPlayer().getName());
        Component titleComponent = AdventureUtil.minimessage(title, tag);
        Component subtitleComponent = AdventureUtil.minimessage(subtitle, tag);

        Title titleAdv = Title.title(
              titleComponent,
              subtitleComponent,
              Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))
        );

        AdventureUtil.sendTitle(context.getPlayer(), titleAdv);
        //context.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        context.next();
    }
}
