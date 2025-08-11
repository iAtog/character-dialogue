package me.iatog.characterdialogue.dialogs;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodContext {

    private final Player player;
    private final DialogSession session;
    private final MethodConfiguration configuration;
    private final SingleUseConsumer<CompletedType> consumer;
    private final AdaptedNPC npc;

    public MethodContext(@NotNull Player player,
                         @NotNull DialogSession session,
                         @NotNull MethodConfiguration configuration,
                         @NotNull SingleUseConsumer<CompletedType> consumer,
                         @Nullable AdaptedNPC npc) {
        this.player = player;
        this.session = session;
        this.configuration = configuration;
        this.consumer = consumer;
        this.npc = npc;
    }

    public Player getPlayer() {
        return player;
    }

    public DialogSession getSession() {
        return session;
    }

    public MethodConfiguration getConfiguration() {
        return configuration;
    }

    public SingleUseConsumer<CompletedType> getConsumer() {
        return consumer;
    }

    public AdaptedNPC getNPC() {
        return npc;
    }

    public void next() {
        this.consumer.accept(CompletedType.CONTINUE);
    }

    public void destroy() {
        this.consumer.accept(CompletedType.DESTROY);
    }

    public void pause() {
        this.consumer.accept(CompletedType.PAUSE);
    }

    public void logError(Logger logger, Level level, String cause) {
        logger.log(level, "Dialogue '" + session.getDialogue().getName() + "' L" + session.getCurrentIndex() + ": " + cause);
    }

    public void logError(Logger logger, String cause) {
        logError(logger, Level.SEVERE, cause);
    }
}
