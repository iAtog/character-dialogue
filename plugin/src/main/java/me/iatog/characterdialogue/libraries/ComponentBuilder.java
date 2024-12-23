package me.iatog.characterdialogue.libraries;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * class made for cleaner handling of chat components
 *
 * @author iAtog
 */
public class ComponentBuilder {

    List<BaseComponent> components;

    public ComponentBuilder() {
        this.components = new ArrayList<>();
    }

    public ComponentBuilder addComponent(BaseComponent component) {
        this.components.add(component);
        return this;
    }

    public ComponentBuilder addTextComponent(String message) {
        return addComponent(new TextComponent(message));
    }

    public ComponentBuilder addTextComponent(String message, ChatColor color, HoverEvent hover, ClickEvent click) {
        TextComponent component = new TextComponent(message);
        component.setHoverEvent(hover);
        component.setClickEvent(click);

        if (color != null) {
            component.setColor(color);
        }

        return addComponent(component);
    }

    public BaseComponent[] build() {
        return components.toArray(new BaseComponent[0]);
    }

    public List<BaseComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

}
