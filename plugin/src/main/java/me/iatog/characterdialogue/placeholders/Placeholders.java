package me.iatog.characterdialogue.placeholders;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.entity.Player;

public class Placeholders {
    public static String translate(Player player, String arg) {
        CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();
        YamlDocument config = main.getFileFactory().getConfig();

        for (String name : config.getSection("placeholders").getRoutesAsStrings(false)) {
            String value = config.getString("placeholders." + name);
            arg = arg.replace("%" + name + "%", value);
        }

        if (main.getHooks().isPlaceHolderAPIEnabled()) {
            arg = main.getHooks().getPlaceHolderAPIHook().translatePlaceHolders(player, arg);
        } else {
            arg = arg.replace("%player_name%", player.getName());
        }

        return TextUtils.colorize(arg);
    }
}
