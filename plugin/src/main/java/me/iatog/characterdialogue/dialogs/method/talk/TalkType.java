package me.iatog.characterdialogue.dialogs.method.talk;

import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum TalkType {
    ACTION_BAR(context -> {
        Player player = context.player();
        String text = context.text();
        String npcName = context.npcName();

        AdventureUtil.sendActionBar(player,
              "<gray>[<aqua>" + npcName + "<gray>] " + text
        );
    }),
    MESSAGE(context -> {
        Player player = context.player();
        String text = context.text();
        String npcName = context.npcName();
        String npc = "<gray>[<aqua>" + npcName + "<gray>] ";

        player.sendMessage(TalkMethod.emptyList);
        AdventureUtil.sendMessage(player, npc + text);
    }),
    FULL_CHAT(context -> {
        Player player = context.player();
        String text = context.text();
        String npcName = context.npcName();
        String line = "<gray><strikethrough>" + TalkMethod.line;
        String colorizedText = "<gray>[<aqua>" + npcName + "<gray>] " + text;
        List<String> wrapped = TextUtils.wrapText(colorizedText, 55);

        player.sendMessage(TalkMethod.emptyList);
        AdventureUtil.sendMessage(player, line);
        AdventureUtil.sendMessage(player, Component.empty());

        for (String wrap : wrapped) {
            TextUtils.sendCenteredMessage(player, "<gray>" + wrap);
        }

        AdventureUtil.sendMessage(player, Component.empty());
        AdventureUtil.sendMessage(player, line);
    });

    private final Consumer<TalkContext> consumer;

    TalkType(Consumer<TalkContext> consumer) {
        this.consumer = consumer;
    }

    public static String[] getEmptyList() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            list.add("");
        }

        return list.toArray(String[]::new);
    }

    public void execute(Player target, String message, String npcName) {
        consumer.accept(new TalkContext(target.getUniqueId(), message, npcName));
    }
}