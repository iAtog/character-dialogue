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

import java.util.regex.Pattern;

public class ConditionalMethod extends DialogMethod<CharacterDialoguePlugin> {

    public ConditionalMethod(CharacterDialoguePlugin main) {
        super("conditional", main);
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
                conditionResult = evaluateCondition(context.getPlayer(), condition);
            } catch (IllegalArgumentException e) {
                AdventureUtil.sendMessage(context.getPlayer(), "<red><bold>Fatal error occurred.");
                getProvider().getLogger().warning("The dialogue '" + session.getDialogue().getName() + "' has an invalid condition in L" + session.getCurrentIndex());
                context.destroy();
                return;
            }

            String expression = (conditionResult ? ifTrue : ifFalse).replace("%apostrophe%", "'");
            String method = expression.trim().toUpperCase();
            String argument = "";

            session.sendDebugMessage("&a" + condition + "&7: &c" + conditionResult, "ConditionalMethod");

            if (expression.contains(":")) {
                String[] parts = expression.split(":", 2);
                method = parts[0].trim().toUpperCase();
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

    private String found(boolean bool) {
        return bool ? "Found" : "Not found";
    }

    public boolean evaluateCondition(Player player, String condition) {
        Operator operator = null;
        String leftSide = null;
        String rightSide = null;

        for (Operator op : Operator.values()) {
            if (condition.contains(op.getText())) {
                operator = op;
                String[] parts = condition.split(Pattern.quote(op.getText()));
                leftSide = Placeholders.translate(player, parts[0].trim());
                rightSide = Placeholders.translate(player, parts[1].trim());
                break;
            }
        }

        if (operator == null || leftSide == null || rightSide == null) {
            throw new IllegalArgumentException("Invalid format");
        }

        return compareCondition(leftSide, rightSide, operator);
    }

    private boolean compareCondition(String left, String right, Operator operator) {
        try {
            return switch (operator) {
                case LESS_THAN -> parseToDouble(left) < parseToDouble(right);
                case LESS_THAN_EQUAL -> parseToDouble(left) <= parseToDouble(right);
                case MORE_THAN -> parseToDouble(left) > parseToDouble(right);
                case MORE_THAN_EQUAL -> parseToDouble(left) >= parseToDouble(right);

                case EQUAL_TO -> left.equals(right);
                case NOT_EQUAL_TO -> !left.equals(right);
                case CONTAINS -> left.contains(right);
                case ENDS_WITH -> left.endsWith(right);
                case STARTS_WITH -> left.startsWith(right);
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The values cannot be compared: " + left + " & " + right);
        }
    }

    private double parseToDouble(String value) {
        return (value == null || value.isEmpty()) ? 0 : Double.parseDouble(value);

    }

    private void debug(DialogSession session, String txt) {
        session.sendDebugMessage(txt, "ConditionalMethod");
    }
}
