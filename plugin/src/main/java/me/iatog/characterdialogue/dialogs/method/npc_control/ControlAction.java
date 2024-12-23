package me.iatog.characterdialogue.dialogs.method.npc_control;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ControlData;
import me.iatog.characterdialogue.path.PathReplayer;
import me.iatog.characterdialogue.path.RecordLocation;
import org.bukkit.Location;

import java.util.List;
import java.util.function.Consumer;

import static me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod.registries;

public enum ControlAction {
    START((ctx) -> {
        ctx.util().createClone(ctx.context(), ctx.targetNPC());
        ctx.context().next();
    }),
    DESTROY((ctx) -> {
        if (registries.containsKey(ctx.player().getUniqueId())) {
            ctx.util().removeRegistered(ctx.player(), ctx.targetNPC());
            ctx.context().next();
        }
    }),
    FOLLOW((ctx) -> {
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData playerRegistry = registry.get(ctx.targetNPC().getId());

        if(playerRegistry != null) {
            ctx.util().toggleFollow(playerRegistry.getCopy(), ctx.player(), true);
        }

        ctx.context().next();
    }),
    UNFOLLOW((ctx) -> {
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData playerRegistry = registry.get(ctx.targetNPC().getId());

        if(playerRegistry != null) {
            ctx.util().toggleFollow(playerRegistry.getCopy(), ctx.player(), false);
        }

        ctx.context().next();
    }),
    POSE((ctx) -> {
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData data = registry.get(ctx.targetNPC().getId());
        MethodConfiguration configuration = ctx.context().getConfiguration();

        if(data != null) {
            AdaptedNPC clone = data.getCopy();
            Location newLocation = ctx.util().getConfigLocation(configuration, ctx.player().getLocation());

            if(newLocation == null) {
                ctx.plugin().getLogger().warning("Invalid coordinates specified in pose action.");
                ctx.context().destroy();
            } else {
                ctx.util().toggleFollow(clone, ctx.player(), false);
                clone.teleport(newLocation);

                if(configuration.getBoolean("lookPlayer", false)) {
                    clone.faceLocation(ctx.player().getLocation());
                }

                ctx.context().next();
            }
        }
    }),
    MOVE_TO((ctx) -> {
        ctx.player().sendMessage("Disabled.");
        ctx.context().next();
        /*
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData data = registry.get(ctx.targetNPC().getId());
        MethodConfiguration configuration = ctx.context().getConfiguration();

        if(data != null) {
            AdaptedNPC clone = data.getCopy();
            Location moveTo = ctx.util().getConfigLocation(configuration, ctx.player().getLocation());

            if(moveTo == null) {
                ctx.plugin().getLogger().severe("Invalid coordinates provided");
                ctx.context().destroy();
                return;
            }

            //FollowPlayerTrait trait = clone.getOrAddTrait(FollowPlayerTrait.class);
            //trait.setTarget(null);

            Navigator navigator = clone.getNavigator();
            NavigatorParameters params = navigator.getDefaultParameters();
            if(navigator.isNavigating()) {
                navigator.cancelNavigation();
            }

            params.baseSpeed(15);
            params.range(30);

            navigator.getPathStrategy();
            navigator.setTarget(moveTo);
        }

        ctx.context().next();*/
    }),
    RECORD((ctx) -> {
        String recordName = ctx.context().getConfiguration().getString("record");
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData data = registry.get(ctx.targetNPC().getId());

        if(recordName == null || recordName.isEmpty()) {
            ctx.plugin().getLogger().warning("No record name specified in record action.");
            ctx.context().destroy();
            return;
        }

        List<RecordLocation> locations = ctx.plugin().getPathStorage().getPath(recordName);

        if(locations == null) {
            ctx.plugin().getLogger().warning("Record '" + recordName + "' not found.");
            ctx.context().destroy();
            return;
        }

        if(data != null) {
            PathReplayer replayer = new PathReplayer(locations, data.getCopy());
            replayer.startReplay();
        } else {
            ctx.plugin().getLogger().warning("An attempt was made to play a recording but the cloned npc was not found.");
        }

        ctx.context().next();
    });

    private final Consumer<ActionData> consumer;

    ControlAction(Consumer<ActionData> consumer) {
        this.consumer = consumer;
    }

    public void execute(MethodContext context, ControlUtil util, AdaptedNPC target) {
        this.consumer.accept(new ActionData(context, util, target));
    }
}
