package me.iatog.characterdialogue.api;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class DialogueLine {
    private String methodName;
    private MethodConfiguration configuration;
    private Map<String, ConfigurationType> configurationTypes;

    public DialogueLine(CharacterDialoguePlugin main, String line) {
        this.configurationTypes = new HashMap<>();
        Matcher matcher = main.getApi().getLineRegex().matcher(line);

        if (!matcher.find()) {
            return;
        }

        String methodName = matcher.group(1).toLowerCase().trim();
        String configPart = (matcher.group(2) != null ? matcher.group(2).trim() : "No configuration specified");
        String arg = matcher.group(3) != null ? matcher.group(3).trim() : "No argument specified";

        if(!main.getCache().getMethods().containsKey(methodName)) {
            return;
        }

        DialogMethod<?> method = main.getCache().getMethods().get(methodName);
        this.methodName = methodName;
        this.configuration = new MethodConfiguration(arg, configPart);
        this.configurationTypes = method.getConfigurationTypes();
    }

    public String getMethodName() {
        return methodName;
    }

    public MethodConfiguration getConfiguration() {
        return configuration;
    }

    public Map<String, ConfigurationType> getConfigurationTypes() {
        return configurationTypes;
    }

    public boolean isAnnotation() {
        return configuration == null || methodName == null;
    }
}
