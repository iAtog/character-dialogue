package me.iatog.characterdialogue.dialogs.method.choice;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.Choice;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.iatog.characterdialogue.dialogs.method.choice.ChoiceMethod.COMMAND_NAME;

public class ChoiceRunnable extends BukkitRunnable {

    private final CharacterDialoguePlugin main;
    private final String line;

    private String color = "<red>";
    private long elapsedTime = 0;

    public ChoiceRunnable(CharacterDialoguePlugin main) {
        this.main = main;
        this.line = StringUtils.repeat(' ', 64);
    }

    @Override
    public void run() {
        if(main.getCache().getChoiceSessions().isEmpty()) {
            return;
        }

        elapsedTime += 5;

        if (elapsedTime >= 20) {
            elapsedTime = 0;
            if(color.equals("<red>")) {
                color = "<gray>";
            } else {
                color = "<red>";
            }
        }
        
        main.getCache().getChoiceSessions().forEach((playerId, session) -> {
            if(session.isUseChat() && !session.isDestroyed()) {
                sendMessage(session);
            }
        });
    }

    public void sendMessage(ChoiceSession session) {
        String model = main.getFileFactory().getConfig().getString("choice.text-model", "<gray>[<red><number><gray>] <message>");
        Player player = session.getPlayer();
        DialogSession dialogSession = main.getCache().getDialogSessions().get(player.getUniqueId());
        TextComponent.Builder builder = Component.text().append(Component.newline());

        session.getChoices().forEach((index, choice) ->
            generateQuestion(builder, index, choice, player, model, session)
        );

        for(int i = 0; i < 20; i++) {
            player.sendMessage(" ");
        }

        String message = session.getMessage();
        AdventureUtil.sendMessage(player, "<gray><strikethrough>" + line);

        if (message != null && !message.isEmpty()) {
            String npc;

            if(dialogSession != null) {
                npc = dialogSession.getDisplayName();
            } else {
                npc = "John Doe";
            }

            AdventureUtil.sendMessage(player, "<gray>" + message.replace("<npc>", npc));
            AdventureUtil.sendMessage(player, Component.empty());
        }

        AdventureUtil.sendMessage(player, builder.build());
        AdventureUtil.sendCenteredMessage(player, main.language("choice-select"), color);
        AdventureUtil.sendMessage(player, "<gray><strikethrough>" + line);
    }

    public void generateQuestion(TextComponent.Builder builder, int index, Choice choice, Player player, String model, ChoiceSession session) {
        Component modelComponent = AdventureUtil.minimessage(
              ((index == session.getSelected()) ? "<red><bold>⏩ <reset>" : "   ") +
                    model.replace("<message>", choice.getMessage()),
              AdventureUtil.placeholder("player", player.getName()),
              AdventureUtil.placeholder("number", (index+1)+"")
        );

        Component messageComponent = modelComponent
              .clickEvent(ClickEvent.runCommand(COMMAND_NAME + " " + session.getUniqueId() + " " + index))
              .hoverEvent(HoverEvent.showText(
                    AdventureUtil.minimessage(
                          CharacterDialoguePlugin.getInstance().getFileFactory().getLanguage()
                                .getString("select-choice").replace("%str%", index + "")
                    )
              ));
        builder.append(messageComponent).append(Component.newline());
    }
}
