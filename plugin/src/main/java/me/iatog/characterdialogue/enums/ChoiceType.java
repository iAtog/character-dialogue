package me.iatog.characterdialogue.enums;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceData;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceGUI;
import me.iatog.characterdialogue.dialogs.method.choice.form.ChoiceForm;
import me.iatog.characterdialogue.session.ChoiceSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.function.Consumer;

import static me.iatog.characterdialogue.dialogs.method.choice.ChoiceMethod.COMMAND_NAME;

public enum ChoiceType {
    CHAT(data -> {
        data.getChoiceSession().setUseChat(true);
    }, d -> {d.getPlayer().closeInventory();}),
    CHATE(data -> {
        String model = data.getConfigFile().getString("choice.text-model", "<gray>[<red><number><gray>] <message>");
        ChoiceSession session = data.getChoiceSession();
        TextComponent.Builder builder = Component.text().append(Component.newline());
        session.getChoices().forEach((index, choice) -> {
            Component modelComponent = AdventureUtil.minimessage(
                  model.replace("<message>", choice.getMessage()),
                  AdventureUtil.placeholder("player", data.getPlayer().getName()),
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
    GUI(data -> {
        ChoiceGUI choiceGUI = new ChoiceGUI(CharacterDialoguePlugin.getInstance());
        choiceGUI.buildGUI(data);
    }, data ->
        data.getPlayer().closeInventory()
    ),
    BEDROCK_GUI(data -> {
        Player player = data.getPlayer();
        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            ChoiceType.GUI.generateQuestions(data);
        } else {
            ChoiceForm choiceForm = new ChoiceForm();
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), choiceForm.load(data));
        }
    }, data ->
        data.getPlayer().closeInventory()
    );

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
