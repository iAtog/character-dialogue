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

    public MessageTranslation(CharacterDialoguePlugin main) {
        this.provider = new MessageTranslationProvider(main);
    }

    @Override
    public Component translate(Component component, Namespace namespace) {
        if(component instanceof TranslatableComponent comp) {
            String key = comp.key();
            String translatedMessage = provider.getTranslation(namespace, key);
            if (!comp.args().isEmpty()) {
                String argument = PlainTextComponentSerializer.plainText().serialize(comp.args().get(0));
                translatedMessage = translatedMessage.replace("<argument>", argument
                      .replace("'", ""));

                return Component.text(translatedMessage);
            }
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
