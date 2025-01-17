package me.iatog.characterdialogue.enums;

import me.iatog.characterdialogue.dialogs.method.conditional.ConditionData;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.session.EmptyDialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum ConditionalExpression {
    //RUN_DIALOGUE/STOP_AND_SEND_MESSAGE/STOP/RUN_METHOD/CONTINUE
    RUN_DIALOGUE((data, completed) -> {
        DialogSession session = data.getSession();
        Player player = session.getPlayer();
        String expression = data.getExpression();
        completed.accept(CompletedType.DESTROY);

        if (!data.getMain().getCache().getDialogues().containsKey(expression) || expression.isEmpty()) {
            data.getMain().getLogger().severe("The dialogue '" + expression + "' was not found.");
            AdventureUtil.sendMessage(player, "<red><bold>Unknown dialogue found.");
            return;
        }

        data.getMain().getApi().runDialogue(player, expression, false, data.getSession().getNPC());
    }),
    STOP_SEND_MSG((data, completed) -> {
        Player player = data.getSession().getPlayer();
        completed.accept(CompletedType.DESTROY);
        AdventureUtil.sendMessage(player, data.getExpression()
              .replace("%npc_name%", data.getSession().getDisplayName()));
    }),

    STOP((data, completed) -> completed.accept(CompletedType.DESTROY)),

    RUN_METHOD((data, completed) -> {
        DialogSession session = data.getSession();
        Player player = session.getPlayer();
        String expression = data.getExpression();

        if (data.getExpression().isEmpty() || data.getMain().getCache().getMethods().containsKey(expression)) {
            session.destroy();
            data.getMain().getLogger().severe("The dialogue '" + expression + "' was not found.");
            AdventureUtil.sendMessage(player, "<red><bold>Unknown method found: " + expression);
            return;
        }

        data.getMain().getApi().runDialogueExpression(player, expression, session.getDisplayName(),
              SingleUseConsumer.create(completedRes ->
                  completed.accept(CompletedType.CONTINUE)
              ), new EmptyDialogSession(data.getMain(), player, Collections.singletonList(expression), session.getDisplayName(),
                    data.getSession().getNPC()), data.getSession().getNPC());

    }),
    CONTINUE((data, completed) -> completed.accept(CompletedType.CONTINUE));

    private final BiConsumer<ConditionData, Consumer<CompletedType>> action;

    ConditionalExpression(BiConsumer<ConditionData, Consumer<CompletedType>> action) {
        this.action = action;
    }

    public void execute(ConditionData data, Consumer<CompletedType> completed) {
        action.accept(data, completed);
    }
}