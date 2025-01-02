package me.iatog.characterdialogue.command.translator;

import me.fixeddev.commandflow.Namespace;
import me.fixeddev.commandflow.translator.TranslationProvider;
import me.fixeddev.commandflow.translator.Translator;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.function.Function;

public class MessageTranslation implements Translator {

    private final MessageTranslationProvider provider;
    private final CharacterDialoguePlugin main;

    public MessageTranslation(CharacterDialoguePlugin main) {
        this.provider = new MessageTranslationProvider(main);
        this.main = main;
    }

    @Override
    public Component translate(Component component, Namespace namespace) {
        if(component instanceof TranslatableComponent comp) {
            String key = comp.key();
            main.getLogger().info("TRANSLATABLE KEY: " + key);
            String translatedMessage = provider.getTranslation(namespace, key);
            return Component.text(translatedMessage);
        }
        return component;
    }

    @Override
    public void setProvider(TranslationProvider provider) {

    }

    @Override
    public void setConverterFunction(Function<String, TextComponent> stringToComponent) {

    }
}
