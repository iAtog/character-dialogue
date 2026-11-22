package me.iatog.characterdialogue.dialogs.method.conditional;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.enums.ConditionalExpression;
import me.iatog.characterdialogue.enums.Operator;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.AdventureUtil;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Pattern;

public class ConditionalMethod extends DialogMethod<CharacterDialoguePlugin> {

    private final ConditionEvaluator conditionEvaluator;

    public ConditionalMethod(CharacterDialoguePlugin main) {
        super("conditional", main);
        this.conditionEvaluator = new ConditionEvaluator();
        addConfigurationType("condition", ConfigurationType.TEXT);
        addConfigurationType("ifTrue", ConfigurationType.TEXT);
        addConfigurationType("ifFalse", ConfigurationType.TEXT);

        setDescription("A condition is created to divert the dialog according to your preference.");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        DialogSession session = context.getSession();

        try {
            String condition = configuration.getString("condition", "");
            String ifTrue = configuration.getString("ifTrue", "");
            String ifFalse = configuration.getString("ifFalse", "");
            debug(session, "Condition: " + condition);
            debug(session, "ifTrue: " + ifTrue);
            debug(session, "ifFalse: " + ifFalse);

            if (condition.isEmpty() || ifTrue.isEmpty() || ifFalse.isEmpty()) {
                getProvider().getLogger().severe("The dialogue '" + session.getDialogue().getName() + "' has an invalid configuration in L" + session.getCurrentIndex());
                getProvider().getLogger().severe("Condition: " + found(! condition.isEmpty()));
                getProvider().getLogger().severe("ifTrue: " + found(! ifTrue.isEmpty()));
                getProvider().getLogger().severe("ifFalse: " + found(! ifFalse.isEmpty()));
                context.destroy();
                debug(session, "Error obtaining configuration");
                return;
            }

            boolean conditionResult;

            try {
                conditionResult = conditionEvaluator.evaluate(context.getPlayer(), condition);
            } catch (IllegalArgumentException e) {
                AdventureUtil.sendMessage(context.getPlayer(), "<red><bold>Fatal error occurred.");
                getProvider().getLogger().warning("The dialogue '" + session.getDialogue().getName() + "' has an invalid condition in L" + session.getCurrentIndex() + " (" + e.getMessage() + ")");
                context.destroy();
                return;
            }

            String expression = (conditionResult ? ifTrue : ifFalse).replace("%apostrophe%", "'");
            String method = expression.trim().toUpperCase(Locale.ROOT);
            String argument = "";

            session.sendDebugMessage("&a" + condition + "&7: &c" + conditionResult, "ConditionalMethod");

            if (expression.contains(":")) {
                String[] parts = expression.split(":", 2);
                method = parts[0].trim().toUpperCase(Locale.ROOT);
                argument = parts.length > 1 ? parts[1].trim() : "";
            }

            ConditionalExpression conditionalExpression = ConditionalExpression.valueOf(method);
            conditionalExpression.execute(new ConditionData(
                    session, getProvider(), Placeholders.translate(context.getPlayer(), argument)),
                    context.getConsumer()
            );
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            AdventureUtil.sendMessage(context.getPlayer(), "<red><bold>Fatal error occurred.");
            getProvider().getLogger().warning("The dialogue '" + session.getDialogue().getName() + "' has an invalid format in L" + session.getCurrentIndex());
            e.printStackTrace();
            context.destroy();
        }
    }

    public ConditionEvaluator getEvaluator() {
        return conditionEvaluator;
    }

    private String found(boolean bool) {
        return bool ? "Found" : "Not found";
    }

    private void debug(DialogSession session, String txt) {
        session.sendDebugMessage(txt, "ConditionalMethod");
    }
}
