package me.iatog.characterdialogue.enums;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceData;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceGUI;
import me.iatog.characterdialogue.dialogs.method.choice.form.ChoiceForm;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.function.Consumer;

public enum ChoiceType {
    CHAT(data -> data.getChoiceSession().setUseChat(true), d -> d.getPlayer().closeInventory()),
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
            ChoiceForm choiceForm = new ChoiceForm(CharacterDialoguePlugin.getInstance());
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
