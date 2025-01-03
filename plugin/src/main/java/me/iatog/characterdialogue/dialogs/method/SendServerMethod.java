package me.iatog.characterdialogue.dialogs.method;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.entity.Player;

public class SendServerMethod extends DialogMethod<CharacterDialoguePlugin> {
    // send_server{channel="BungeeCord"}: lobby2
    public SendServerMethod(CharacterDialoguePlugin main) {
        super("send_server", main);
        addConfigurationType("channel", ConfigurationType.TEXT);
        setDescription("Switches the player to another server in the proxy");
        setDisabled(true);
    }

    @Override
    public void execute(MethodContext context) {
        Player player = context.getPlayer();
        MethodConfiguration configuration = context.getConfiguration();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        String server = configuration.getArgument();
        String channel = configuration.getString("channel", "BungeeCord");

        if (server.isEmpty()) {
            player.sendMessage(TextUtils.colorize("&cNo server found"));

        } else {
            out.writeUTF("Connect");
            out.writeUTF(server);

            player.sendPluginMessage(getProvider(), channel, out.toByteArray());
        }

        context.destroy();


    }
}
