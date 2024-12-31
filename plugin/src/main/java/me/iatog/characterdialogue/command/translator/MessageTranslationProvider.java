package me.iatog.characterdialogue.command.translator;

import me.fixeddev.commandflow.Namespace;
import me.fixeddev.commandflow.translator.TranslationProvider;
import me.iatog.characterdialogue.CharacterDialoguePlugin;

public class MessageTranslationProvider implements TranslationProvider {

    private final CharacterDialoguePlugin main;

    public MessageTranslationProvider(CharacterDialoguePlugin main) {
        this.main = main;
    }

    @Override
    public String getTranslation(Namespace namespace, String key) {
        switch(key) {
            case "sender.only-player" -> main.language("general.only-player");
            case "player.offline" -> main.language("general.offline-player");
            case "command.no-permission" -> main.language("general.no-permission");
        }

        return "<null> : " + key;
    }
}