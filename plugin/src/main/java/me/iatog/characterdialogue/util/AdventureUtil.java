package me.iatog.characterdialogue.util;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdventureUtil {

    private static final MiniMessage minimessage;

    static {
        minimessage = MiniMessage.miniMessage();
    }

    /**
     * Send a message to the player using minimessage.
     * @param player the player who receives the message
     * @param message the message
     */
    public static void sendMessage(@NotNull Player player, @NotNull String message, TagResolver ...resolvers) {
        CharacterDialoguePlugin.getInstance().getAudiences().player(player).sendMessage(
              minimessage.deserialize(message, resolvers)
        );
    }

    /**
     * Send a message to the player using minimessage.
     * @param player the player who receives the message
     * @param message the message
     */
    public static void sendMessage(@NotNull Player player, @NotNull String message) {
        CharacterDialoguePlugin.getInstance().getAudiences().player(player).sendMessage(
              minimessage.deserialize(message)
        );
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String message, TagResolver ...resolvers) {
        CharacterDialoguePlugin.getInstance().getAudiences().sender(sender).sendMessage(
              minimessage.deserialize(message, resolvers)
        );
    }

    /**
     * Send a message to the sender using minimessage.
     * @param sender the sender who receives the message
     * @param message the message
     */
    public static void sendMessage(@NotNull CommandSender sender, @NotNull String message) {
        CharacterDialoguePlugin.getInstance().getAudiences().sender(sender).sendMessage(
              minimessage.deserialize(message)
        );
    }

    /**
     * Sends a component to the player using minimessage.
     * @param player the player who receives the component
     * @param component the component
     */
    public static void sendMessage(@NotNull Player player, @NotNull Component component) {
        CharacterDialoguePlugin.getInstance().getAudiences().sender(player).sendMessage(component);
    }
    /**
     * Sends a component to the sender using minimessage.
     * @param sender the sender who receives the component
     * @param component the component
     */
    public static void sendMessage(@NotNull CommandSender sender, @NotNull Component component) {
        CharacterDialoguePlugin.getInstance().getAudiences().sender(sender).sendMessage(component);
    }

    /**
     * Sends an action bar to the player
     * @param player the player who receives the message
     * @param message the action bar contents
     */
    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        CharacterDialoguePlugin.getInstance().getAudiences().player(player).sendActionBar(
              minimessage.deserialize(message)
        );
    }

    public static void sendTitle(Player player, Title title) {
        CharacterDialoguePlugin.getInstance().getAudiences().player(player).showTitle(title);
    }

    public static Component minimessage(String text) {
        return minimessage.deserialize(text);
    }

    public static Component minimessage(String text, TagResolver ...resolvers) {
        return minimessage.deserialize(text, resolvers);
    }

    public static TagResolver placeholder(String key, String value) {
        return Placeholder.unparsed(key, value);
    }

}
