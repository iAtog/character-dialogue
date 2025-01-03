package me.iatog.characterdialogue.command.translator;

import me.fixeddev.commandflow.Namespace;
import me.fixeddev.commandflow.translator.TranslationProvider;
import me.fixeddev.commandflow.translator.Translator;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.function.Function;

public class MessageTranslation implements Translator {

    private final MiniMessage minimessage;
    private final MessageTranslationProvider provider;

    public MessageTranslation(CharacterDialoguePlugin main) {
        this.provider = new MessageTranslationProvider(main);
        this.minimessage = MiniMessage.miniMessage();
    }

    @Override
    public Component translate(Component component, Namespace namespace) {
        if(component instanceof TranslatableComponent comp) {
            String key = comp.key();
            String translatedMessage = provider.getTranslation(namespace, key);
            return minimessage.deserialize(translatedMessage);
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
