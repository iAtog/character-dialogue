package me.iatog.characterdialogue.listeners;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.api.dialog.RegionalDialogue;
import me.iatog.characterdialogue.api.events.RegionEnterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class RegionalDialoguesListener implements Listener {

    private final CharacterDialoguePlugin main;

    public RegionalDialoguesListener(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onEnter(RegionEnterEvent event) {
        Player player = event.getPlayer();
        String region = event.getRegionName();
        Map<String, RegionalDialogue> regionalDialogues = main.getCache().getRegionalDialogues();

        if(!regionalDialogues.containsKey(region) || player == null) {
            return;
        }

        RegionalDialogue regionalDialogue = regionalDialogues.get(region);
        Dialogue dialogue = main.getCache().getDialogues().get(regionalDialogue.getDialogueName());

        if(dialogue == null) {
            main.getLogger().warning("Tried to execute regional dialogue. but '" + regionalDialogue.getDialogueName() + "' don't exists.");
            return;
        }

        boolean conditionResult = main.getApi().evaluateConditions(
              player,
              regionalDialogue.getConditions(),
              regionalDialogue.getConditionType()
        );

        if(conditionResult) {
            dialogue.start(player, null);
        }
    }
}
