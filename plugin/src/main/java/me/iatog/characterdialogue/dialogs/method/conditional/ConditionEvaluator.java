package me.iatog.characterdialogue.dialogs.method.conditional;

import me.iatog.characterdialogue.enums.Operator;
import me.iatog.characterdialogue.placeholders.Placeholders;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionEvaluator {

    private static final Pattern INNER_PARENTHESES = Pattern.compile("\\(([^()]+)\\)");
    private static final Pattern LOGICAL_OPERATOR = Pattern.compile("\\s*(&&|\\|\\|)\\s*");

    public boolean evaluate(Player player, String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Condition cannot be empty");
        }

        String current = expression.trim();
        Matcher matcher = INNER_PARENTHESES.matcher(current);
        while (matcher.find()) {
            String innerExpression = matcher.group(1);
            boolean innerResult = evaluateFlatExpression(player, innerExpression);

            current = current.substring(0, matcher.start())
                    + innerResult
                    + current.substring(matcher.end());

            matcher = INNER_PARENTHESES.matcher(current);
        }

        if (current.contains("(") || current.contains(")")) {
            throw new IllegalArgumentException("Unbalanced parentheses in: " + expression);
        }

        return evaluateFlatExpression(player, current);
    }

    private boolean evaluateFlatExpression(Player player, String expression) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = LOGICAL_OPERATOR.matcher(expression.trim());
        int lastEnd = 0;

        while (matcher.find()) {
            tokens.add(expression.substring(lastEnd, matcher.start()).trim());
            tokens.add(matcher.group(1));
            lastEnd = matcher.end();
        }
        tokens.add(expression.substring(lastEnd).trim());

        if (tokens.size() % 2 == 0 || tokens.stream().anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Malformed logical expression: " + expression);
        }

        for (int i = 0; i < tokens.size(); i += 2) {
            boolean result = evaluateAtom(player, tokens.get(i));
            tokens.set(i, String.valueOf(result));
        }

        for (int i = 1; i < tokens.size(); i += 2) {
            if (tokens.get(i).equals("&&")) {
                boolean left = Boolean.parseBoolean(tokens.get(i - 1));
                boolean right = Boolean.parseBoolean(tokens.get(i + 1));
                tokens.set(i - 1, String.valueOf(left && right));
                tokens.remove(i);
                tokens.remove(i);
                i -= 2;
            }
        }

        boolean finalResult = Boolean.parseBoolean(tokens.getFirst());
        for (int i = 1; i < tokens.size(); i += 2) {
            if (tokens.get(i).equals("||")) {
                boolean right = Boolean.parseBoolean(tokens.get(i + 1));
                finalResult = finalResult || right;
            }
        }

        return finalResult;
    }

    private boolean evaluateAtom(Player player, String condition) {
        if (condition.equalsIgnoreCase("true")) return true;
        if (condition.equalsIgnoreCase("false")) return false;

        return evaluateSingleCondition(player, condition);
    }

    public boolean evaluateSingleCondition(Player player, String condition) {
        Operator operator = null;
        String leftSide = null;
        String rightSide = null;

        for (Operator op : Operator.values()) {
            if (condition.contains(op.getText())) {
                operator = op;
                String[] parts = condition.split(Pattern.quote(op.getText()), 2);
                leftSide = Placeholders.translate(player, parts[0].trim());
                rightSide = Placeholders.translate(player, parts[1].trim());
                break;
            }
        }

        if (operator == null || leftSide == null || rightSide == null) {
            throw new IllegalArgumentException("Invalid format: " + condition);
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
}
