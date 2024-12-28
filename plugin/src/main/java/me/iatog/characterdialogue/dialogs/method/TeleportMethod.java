package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TeleportMethod extends DialogMethod<CharacterDialoguePlugin> {

    public TeleportMethod() {
        super("teleport");
        addConfigurationType("x", ConfigurationType.FLOAT);
        addConfigurationType("y", ConfigurationType.FLOAT);
        addConfigurationType("z", ConfigurationType.FLOAT);
        addConfigurationType("yaw", ConfigurationType.FLOAT);
        addConfigurationType("pitch", ConfigurationType.FLOAT);
        addConfigurationType("world", ConfigurationType.TEXT);

        setDescription("Teleports the player");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        Player player = context.getPlayer();

        Location location = player.getLocation();
        double x = configuration.getFloat("x", (float) location.getX());
        double y = configuration.getFloat("y", (float) location.getY());
        double z = configuration.getFloat("z", (float) location.getZ());
        float yaw = configuration.getFloat("yaw", location.getYaw());
        float pitch = configuration.getFloat("pitch", location.getPitch());
        String world = configuration.getString("world", Objects.requireNonNull(location.getWorld()).getName());

        Location to = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        player.teleport(to);
        context.next();
    }
}
