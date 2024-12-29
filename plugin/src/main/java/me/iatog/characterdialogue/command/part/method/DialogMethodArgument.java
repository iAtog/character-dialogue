package me.iatog.characterdialogue.command.part.method;

import me.iatog.characterdialogue.dialogs.DialogMethod;
import org.bukkit.plugin.java.JavaPlugin;

public record DialogMethodArgument(String getName, DialogMethod<? extends JavaPlugin> getMethod) {

}