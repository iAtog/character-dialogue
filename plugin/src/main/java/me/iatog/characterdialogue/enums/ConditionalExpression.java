package me.iatog.characterdialogue.enums;

import me.iatog.characterdialogue.dialogs.method.conditional.ConditionData;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.session.EmptyDialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import me.iatog.characterdialogue.util.TextUtils;
import org.bukkit.ChatColor;
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
        //session.destroy();
        completed.accept(CompletedType.DESTROY);

        if (! data.getMain().getCache().getDialogues().containsKey(expression) || expression.isEmpty()) {
            data.getMain().getLogger().severe("The dialogue '" + expression + "' was not found.");
            player.sendMessage(TextUtils.colorize("&c&lUnknown dialogue found."));
            return;
        }

        data.getMain().getApi().runDialogue(player, expression, false, data.getSession().getNPC());
    }),
    STOP_SEND_MSG((data, completed) -> {
        Player player = data.getSession().getPlayer();
        completed.accept(CompletedType.DESTROY);
        player.sendMessage(Placeholders.translate(player, data.getExpression()
              .replace(data.getSession().getDisplayName(), ChatColor.stripColor(data.getSession().getDisplayName()))));
    }),
    STOP((data, completed) -> {
        completed.accept(CompletedType.DESTROY);
    }),
    RUN_METHOD((data, completed) -> {
        //completed.accept(CompletedType.PAUSE);
        DialogSession session = data.getSession();
        Player player = session.getPlayer();
        String expression = data.getExpression();

        if (data.getExpression().isEmpty() || data.getMain().getCache().getMethods().containsKey(expression)) {
            session.destroy();
            data.getMain().getLogger().severe("The dialogue '" + expression + "' was not found.");
            player.sendMessage("&c&lUnknown method found.");
            return;
        }

        data.getMain().getApi().runDialogueExpression(player, expression, session.getDisplayName(),
              SingleUseConsumer.create(completedRes -> {
                  completed.accept(CompletedType.CONTINUE);
              }), new EmptyDialogSession(data.getMain(), player, Collections.singletonList(expression), session.getDisplayName(),
                    data.getSession().getNPC()), data.getSession().getNPC());

    }),
    CONTINUE((data, completed) -> {
        completed.accept(CompletedType.CONTINUE);
    });

    private final BiConsumer<ConditionData, Consumer<CompletedType>> action;

    ConditionalExpression(BiConsumer<ConditionData, Consumer<CompletedType>> action) {
        this.action = action;
    }

    public void execute(ConditionData data, Consumer<CompletedType> completed) {
        action.accept(data, completed);
    }
}