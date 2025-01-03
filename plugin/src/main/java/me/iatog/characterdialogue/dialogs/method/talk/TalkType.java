package me.iatog.characterdialogue.dialogs.method.talk;

import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum TalkType {
    ACTION_BAR((player, text, npcName) -> {
        AdventureUtil.sendActionBar(player,
              "<gray>[<aqua>" + npcName + "<gray>] <gray>" +
              Placeholders.translate(player, text)
        );
    }),
    MESSAGE((player, text, npcName) -> {
        String npc = TextUtils.colorize("&8[&b" + npcName + "&8] &7");

        player.sendMessage(getEmptyList());
        player.sendMessage(npc + text);
    }),
    FULL_CHAT((player, text, npcName) -> {
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

    private final TriConsumer<Player, String, String> consumer;

    TalkType(TriConsumer<Player, String, String> consumer) {
        this.consumer = consumer;
    }

    public static String[] getEmptyList() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            list.add(TextUtils.colorize("&7"));
        }

        return list.toArray(String[]::new);
    }

    public void execute(Player target, String message, String npcName) {
        consumer.accept(target, message, npcName);
    }
}