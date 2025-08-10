package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodContext;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

public class WeatherMethod extends DialogMethod<CharacterDialoguePlugin> {

    // weather: DOWNFALL or CLEAR or RESET
    public WeatherMethod(CharacterDialoguePlugin provider) {
        super("weather", provider);
    }

    @Override
    public void execute(MethodContext context) {
        String arg = context.getConfiguration().getArgument();
        if(arg.equalsIgnoreCase("RESET")) {
            context.getPlayer().resetPlayerWeather();
            context.next();
            return;
        }
        WeatherType weatherType = null;

        try {
            weatherType = WeatherType.valueOf(arg.toUpperCase());
        } catch(Exception ex) {
            getProvider().getLogger().severe("Weather '" + arg + "' in " + context.getSession().getDialogue().getName() + " is not valid");
            context.next();
            return;
        }

        context.getPlayer().setPlayerWeather(weatherType);
        context.next();
    }
}
