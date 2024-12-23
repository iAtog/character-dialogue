package me.iatog.characterdialogue.dialogs.method.choice.form;

import me.iatog.characterdialogue.dialogs.method.choice.ChoiceData;
import me.iatog.characterdialogue.dialogs.method.choice.ChoiceUtil;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.HashMap;
import java.util.Map;

public class ChoiceForm {

    private final Map<String, Integer> buttonValues;

    public ChoiceForm() {
        this.buttonValues = new HashMap<>();
    }

    public Form load(ChoiceData data) {
        Player player = data.getPlayer();
        SimpleForm.Builder form = SimpleForm.builder();
        String model = data.getConfigFile().getString("choice.text-model", "&a{I})&e {S}");

        form.title("Select an option");
        //form.content("Select below");

        data.getChoiceSession().getChoices().forEach((index, choice) -> {
            String parsedModel = TextUtils.colorize(model.replace("{I}",
                  String.valueOf(index)).replace("{S}",
                  choice.getMessage()));
            //player.sendMessage("Current index: " + index);
            //form.button(parsedModel, FormImage.Type.URL, ChoiceUtil.getHeadNumber(index));
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
