package me.iatog.characterdialogue.dialogs.method.talk;

import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
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
              "<gray>[<aqua>" + npcName + "<gray>] " +
                    context.color() +
                    text
        );
    }),
    MESSAGE(context -> {
        Player player = context.player();
        String text = context.text();
        String npcName = context.npcName();
        String npc = TextUtils.colorize("&8[&b" + npcName + "&8] &7");

        player.sendMessage(getEmptyList());
        player.sendMessage(npc + text);
    }),
    FULL_CHAT(context -> {
        Player player = context.player();
        String text = context.text();
        String npcName = context.npcName();
        String line = "&7&m                                                                                ";
        String colorizedText = TextUtils.colorize("&8[&b" + npcName + "&8] &7" + text);
        List<String> wrapped = TextUtils.wrapText(colorizedText, 55);

        player.sendMessage(getEmptyList());
        player.sendMessage(TextUtils.colorize(line));
        player.sendMessage(TextUtils.colorize("&7"));

        for (String wrap : wrapped) {
            TextUtils.sendCenteredMessage(player, "<gray>" + wrap);
        }

        player.sendMessage(TextUtils.colorize("&7"));
        player.sendMessage(TextUtils.colorize(line));
    });

    private final Consumer<TalkContext> consumer;

    TalkType(Consumer<TalkContext> consumer) {
        this.consumer = consumer;
    }

    public static String[] getEmptyList() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            list.add(TextUtils.colorize("&7"));
        }

        return list.toArray(String[]::new);
    }

    public void execute(Player target, String message, String npcName, String color) {
        consumer.accept(new TalkContext(target.getUniqueId(), message, npcName, color));
    }
}