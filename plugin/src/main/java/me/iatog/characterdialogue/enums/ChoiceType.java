package me.iatog.characterdialogue.enums;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceData;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceGUI;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceUtil;
import me.iatog.characterdialogue.dialogs.method.choice.form.ChoiceForm;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.function.Consumer;

import static me.iatog.characterdialogue.dialogs.method.choice.ChoiceMethod.COMMAND_NAME;

@SuppressWarnings("deprecation")
public enum ChoiceType {
    CHAT(data -> {
        String model = data.getConfigFile().getString("choice.text-model", "<gray>[<red><number><gray>] <message>");
        ChoiceSession session = data.getChoiceSession();
        TextComponent.Builder builder = Component.text().append(Component.newline());
        session.getChoices().forEach((index, choice) -> {
            Component modelComponent = AdventureUtil.minimessage(
                  model,
                  AdventureUtil.placeholder("player", data.getPlayer().getName()),
                  AdventureUtil.placeholder("message", choice.getMessage()),
                  AdventureUtil.placeholder("number", index+"")
            );

            Component messageComponent = modelComponent
                  .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(COMMAND_NAME + " " + session.getUniqueId() + " " + index))
                  .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                        AdventureUtil.minimessage(
                              CharacterDialoguePlugin.getInstance().getFileFactory().getLanguage()
                              .getString("select-choice").replace("%str%", index + "")
                        )
                  ));

            builder.append(messageComponent).append(Component.newline());
        });

        data.getPlayer().getInventory().setHeldItemSlot(8);
        AdventureUtil.sendMessage(data.getPlayer(), builder.build());
    }, data -> data.getPlayer().closeInventory()),
    CHATE(data -> {
        ComponentBuilder questions = new ComponentBuilder("\n");
        String model = data.getConfigFile().getString("choice.text-model", "&a{I})&e {S}");
        ChoiceSession choiceSession = data.getChoiceSession();

        choiceSession.getChoices().forEach((index, choice) -> {
            String parsedModel = TextUtils.colorize(model).replace("{I}",
                  String.valueOf(index)).replace("{S}", choice.getMessage());
            String command = COMMAND_NAME + " " + choiceSession.getUniqueId() + " " + index;

            questions.append(Placeholders.translate(data.getPlayer(), parsedModel) + " \n")
                  .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                  .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ChoiceUtil.getSelectText(index)));
        });

        data.getPlayer().getInventory().setHeldItemSlot(8);
        data.getPlayer().spigot().sendMessage(questions.create());
    }, (data -> data.getPlayer().closeInventory())),
    GUI(data -> {
        ChoiceGUI choiceGUI = new ChoiceGUI(CharacterDialoguePlugin.getInstance());
        choiceGUI.buildGUI(data);
    }, data -> {
        data.getPlayer().closeInventory();
    }),
    BEDROCK_GUI(data -> {
        Player player = data.getPlayer();
        if (! FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            ChoiceType.GUI.generateQuestions(data);
        } else {
            ChoiceForm choiceForm = new ChoiceForm();
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), choiceForm.load(data));
        }
    }, data -> {
        data.getPlayer().closeInventory();
    });

    private final Consumer<ChoiceData> consumer;
    private final Consumer<ChoiceData> close;

    ChoiceType(Consumer<ChoiceData> consumer, Consumer<ChoiceData> close) {
        this.consumer = consumer;
        this.close = close;
    }

    public void generateQuestions(ChoiceData data) {
        consumer.accept(data);
    }

    public Consumer<ChoiceData> getCloseAction() {
        return close;
    }
}
