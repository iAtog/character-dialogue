package me.iatog.characterdialogue.enums;

import org.bukkit.event.block.Action;

public enum ClickType {
    LEFT(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK),
    RIGHT(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    ALL(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);

    private Action[] actions;

    ClickType(Action... actions) {
        this.actions = actions;
    }

    public static boolean match(String click) {
        boolean isValid = false;

        for (ClickType type : values()) {
            if (type.name().equalsIgnoreCase(click)) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    public Action getAction() {
        return actions[0];
    }

    public Action[] getActions() {
        return actions;
    }

    public boolean isValid(Action actionToCompare) {
        boolean valid = false;
        for (int i = 0; i < getActions().length; i++) {
            Action action = getActions()[i];
            if (actionToCompare == action) {
                valid = true;
                break;
            }
        }
        return valid;
    }
}
