package me.iatog.characterdialogue;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.iatog.characterdialogue.api.dialog.RegionalDialogue;
import me.iatog.characterdialogue.dialogs.ChoiceInfo;
import me.iatog.characterdialogue.dialogs.LoadedChoice;
import me.iatog.characterdialogue.enums.ConditionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Initializer {
    private final CharacterDialoguePlugin main;

    public Initializer(CharacterDialoguePlugin main) {
        this.main = main;
    }
    /**
     * Load all files from the folder CharacterDialogue/dialogues/
     */
    public void loadAllDialogues() throws IOException {
        String folderName = main.getDataFolder() + "/" + "dialogues";
        File folder = new File(folderName);

        boolean res = loadDefault(
                folder,
                folderName,
                "dialogues/examples.yml",
                "examples.yml",
                (document) -> main.getAllDialogues().add(document)
        );

        if(!res) {
            clearAllDialogues();
            load(folder, (document) -> main.getAllDialogues().add(document));
        }
    }

    boolean loadDefault(File folder, String folderName, String defName, String finalName, Consumer<YamlDocument> action) {
        if(!folder.exists()) {
            folder.mkdir();
            try {
                YamlDocument defFile = YamlDocument.create(new File(folderName + "/" + finalName), Objects.requireNonNull(main.getResource(defName)));
                action.accept(defFile);
            } catch (IOException e) {
                //getLogger().warning("Error loading default file: " + e.getMessage());
            }

            return true;
        }

        return false;
    }

    public void load(File folder, Consumer<YamlDocument> action) {
        if(folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    if (!file.isFile()) continue;

                    try {
                        YamlDocument yamlDocument = YamlDocument.create(file);

                        action.accept(yamlDocument);
                    } catch(IOException e) {
                        //getLogger().severe("Error loading folder: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void clearAllChoices() {
        main.getCache().getLoadedChoices().clear();
    }

    private void loadChoices(YamlDocument document) {
        Map<String, LoadedChoice> loadedChoices = main.getCache().getLoadedChoices();

        for(String choiceName : document.getSection("choices").getRoutesAsStrings(false)) {
            List<ChoiceInfo> choices = new ArrayList<>();
            String msg = document.getString("choices." + choiceName + ".message", "");

            for(String option : document.getSection("choices." + choiceName).getRoutesAsStrings(false)) {
                if(option.equals("message")) {
                    continue;
                }

                Section section = document.getSection("choices." + choiceName + "." + option);
                String type = section.getString("type");
                String message = section.getString("message", "no message specified");
                String argument = section.getString("argument", "");
                ChoiceInfo info = new ChoiceInfo(option, type, message, argument);
                choices.add(info);
            }

            loadedChoices.put(choiceName, new LoadedChoice(choices, msg));
        }
    }

    public void loadAllChoices() {
        String folderName = main.getDataFolder() + "/" + "choices";
        File folder = new File(folderName);

        boolean res = loadDefault(
                folder,
                folderName,
                "choices.yml",
                "choice-example.yml",
                this::loadChoices
        );

        if(!res) {
            load(folder, this::loadChoices);
        }
    }

    public void clearAllDialogues() {
        main.getAllDialogues().clear();
    }

    public void loadRegionalDialogues() {
        YamlDocument config = main.getFileFactory().getConfig();

        for (String regionName : config.getSection("regional-dialogues").getRoutesAsStrings(false)) {
            Section section = config.getSection("regional-dialogues." + regionName);
            List<String> conditions = section.getStringList("conditions");
            String conditionType = section.getString("conditions-type", "all");

            if(!conditionType.equalsIgnoreCase("all") && !conditionType.equalsIgnoreCase("one")) {
                return;
            }

            ConditionType type = ConditionType.to(conditionType);
            String dialogueName = section.getString("dialogue");
            RegionalDialogue dialogue = new RegionalDialogue(type, conditions, dialogueName, regionName);

            main.getCache().getRegionalDialogues().put(regionName, dialogue);
        }
    }
}
