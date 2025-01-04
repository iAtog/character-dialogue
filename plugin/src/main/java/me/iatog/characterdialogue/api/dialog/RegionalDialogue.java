package me.iatog.characterdialogue.api.dialog;

import me.iatog.characterdialogue.enums.ConditionType;

import java.util.List;

public class RegionalDialogue {

    private final ConditionType conditionType;
    private final List<String> conditions;
    private final String dialogueName;
    private final String region;

    public RegionalDialogue(ConditionType conditionType, List<String> conditions, String dialogueName, String region) {
        this.conditionType = conditionType;
        this.conditions = conditions;
        this.dialogueName = dialogueName;
        this.region = region;
    }

    public String getDialogueName() {
        return dialogueName;
    }

    public String getRegion() {
        return region;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }
}
