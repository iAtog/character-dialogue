package me.iatog.characterdialogue.session;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.interfaces.Session;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class InitializeSession implements Session {

    private final CharacterDialoguePlugin main;

    private final DialogSession session;
    private List<String> lines;
    private UUID playerId;
    private int index;

    public InitializeSession(CharacterDialoguePlugin main, List<String> lines, Player player, DialogSession session) {
        this.main = main;
        this.lines = lines;
        this.playerId = player.getUniqueId();
        this.session = session;
    }

    public void start(Consumer<Boolean> onFinish, int index) {
        if (index >= lines.size()) {
            onFinish.accept(true);
            return;
        }
        
        SingleUseConsumer<CompletedType> consumer = SingleUseConsumer.create((c) -> {
            setIndex(getIndex() + 1);
            start(onFinish, getIndex());
        });

        main.getApi().runDialogueExpression(
              getPlayer(),
              lines.get(index),
              session.getDialogue().getDisplayName(),
              consumer,
              session,
              null
        );
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    @Override
    public void destroy() {

    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}