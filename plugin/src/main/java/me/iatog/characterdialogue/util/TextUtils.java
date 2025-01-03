package me.iatog.characterdialogue.util;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private final static int CENTER_PX = 154;
    private final static Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    @SuppressWarnings("deprecation")
    @NotNull
    public static String colorize(String message) {
        String version = Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1];

        if (Integer.parseInt(version) >= 16) {
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color.substring(1)) + "");
                matcher = pattern.matcher(message);
            }
        }

        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Component colorizeComponent(String message) {
        return Component.text(colorize(message));
    }

    public static List<String> wrapText(String text, int maxLineLength, String color) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= maxLineLength) {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                lines.add(color + currentLine);
                currentLine = new StringBuilder(word);
            }
        }

        lines.add(color + currentLine);
        return lines;
    }
    public static List<String> wrapText(String text, int maxLineLength) {
        return wrapText(text, maxLineLength, "");
    }

    public static void sendCenteredMessage(Player player, String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty()) return;

        String message = TextUtils.colorize(rawMessage);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        //player.sendMessage(sb + message);
        AdventureUtil.sendMessage(player, sb + message);
    }
}
