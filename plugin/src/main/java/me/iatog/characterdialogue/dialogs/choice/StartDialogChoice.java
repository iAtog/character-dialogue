package me.iatog.characterdialogue.dialogs.choice;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.CharacterDialogueAPI;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.dialogs.ChoiceContext;
import me.iatog.characterdialogue.dialogs.DialogChoice;
import org.bukkit.entity.Player;

public class StartDialogChoice extends DialogChoice {

    private final CharacterDialoguePlugin main;

    public StartDialogChoice(CharacterDialoguePlugin main) {
        super("start_dialogue", true);
        this.main = main;
    }

    @Override
    public void onSelect(ChoiceContext context) {
        Dialogue dialogue = main.getApi().getDialogue(context.getArgument());
        Player player = context.getPlayer();
        AdaptedNPC npc = context.getDialogSession().getNPC();
        context.getDialogSession().destroy();

        if (dialogue == null) {
            main.getLogger().severe("The dialogue \"" + context.getArgument() + "\" doesn't exists");
            return;
        }

        if (dialogue.isFirstInteractionEnabled() && !main.getApi().wasReadedBy(player, dialogue, true)) {
            dialogue.startFirstInteraction(player, true, npc);
        } else {
            dialogue.start(player, context.getDialogSession().isOnDebugMode(), npc);
        }
    }

}
