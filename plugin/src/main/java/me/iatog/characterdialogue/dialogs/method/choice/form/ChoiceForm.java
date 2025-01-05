package me.iatog.characterdialogue.dialogs.method.choice.form;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceData;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceUtil;
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.HashMap;
import java.util.Map;

public class ChoiceForm {

    private final Map<String, Integer> buttonValues;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

    public ChoiceForm() {
        this.buttonValues = new HashMap<>();
    }

    public Form load(ChoiceData data) {
        Player player = data.getPlayer();
        SimpleForm.Builder form = SimpleForm.builder();
        String model = data.getConfigFile().getString("choice.text-model");
        String title = CharacterDialoguePlugin.getInstance().getFileFactory().getLanguage().getString("choice-title", "Select an option");

        form.title(serializer.serialize(AdventureUtil.minimessage(title)));

        data.getChoiceSession().getChoices().forEach((index, choice) -> {
            Component component = AdventureUtil.minimessage(
                  model.replace("<message>", choice.getMessage()),
                  AdventureUtil.placeholder("player", data.getPlayer().getName()),
                  AdventureUtil.placeholder("number", index+"")
            );

            String parsedModel = serializer.serialize(component);
            form.button(parsedModel);
            buttonValues.put(parsedModel, index);
        });

        form.validResultHandler(response -> {
            String buttonText = response.clickedButton().text();
            if (data.getChoiceSession().isDestroyed()) {
                return;
            }

            if (buttonValues.containsKey(buttonText)) {
                int selectedChoice = buttonValues.get(buttonText);
                ChoiceUtil.runChoice(player, selectedChoice);
            } else {
                player.sendMessage("Invalid choice");
            }
        });

        form.closedOrInvalidResultHandler(r -> {
            buttonValues.clear();
            ChoiceUtil.removeTaskIfPresent(player.getUniqueId());
            data.getChoiceSession().destroy();
            data.getDialogSession().destroy();
            //player.sendMessage("Cancelled");
        });

        return form.build();
    }
}
