package me.iatog.characterdialogue.loader;

import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.command.CharacterDialogueCommand;
import me.iatog.characterdialogue.command.translator.MessageTranslation;
import me.iatog.characterdialogue.command.usage.PluginUsageBuilder;
import me.iatog.characterdialogue.dialogs.Group;
import me.iatog.characterdialogue.gui.GUI;
import me.iatog.characterdialogue.command.part.dialogue.DialoguePartFactory;
import me.iatog.characterdialogue.command.part.group.GroupPartFactory;
import me.iatog.characterdialogue.command.part.gui.GUIPartFactory;
import me.iatog.characterdialogue.command.part.item.CustomItemPartFactory;
import me.iatog.characterdialogue.command.part.method.DialogMethodArgument;
import me.iatog.characterdialogue.command.part.method.MethodPartFactory;
import me.iatog.characterdialogue.command.part.npc.NPCPartFactory;
import me.iatog.characterdialogue.command.part.record.RecordPartFactory;
import me.iatog.characterdialogue.path.Record;
import me.iatog.characterdialogue.util.CustomItem;

public class CommandLoader implements Loader {

    private final CharacterDialoguePlugin main;
    private final AnnotatedCommandTreeBuilder builder;
    private final BukkitCommandManager commandManager;

    public CommandLoader(CharacterDialoguePlugin main) {
        this.main = main;
        this.commandManager = new BukkitCommandManager("CharacterDialogue");

        this.commandManager.setTranslator(new MessageTranslation(main));
        this.commandManager.setUsageBuilder(new PluginUsageBuilder(main));

        PartInjector injector = PartInjector.create();
        injector.install(new DefaultsModule());
        injector.install(new BukkitModule());

        injector.bindFactory(Dialogue.class, new DialoguePartFactory(main));
        injector.bindFactory(DialogMethodArgument.class, new MethodPartFactory(main));
        injector.bindFactory(Group.class, new GroupPartFactory(main));
        injector.bindFactory(AdaptedNPC.class, new NPCPartFactory(main));
        injector.bindFactory(GUI.class, new GUIPartFactory(main));
        injector.bindFactory(Record.class, new RecordPartFactory(main));
        injector.bindFactory(CustomItem.class, new CustomItemPartFactory(main));

        this.builder = new AnnotatedCommandTreeBuilderImpl(injector);
    }

    @Override
    public void load() {
        registerCommands(
              new CharacterDialogueCommand(main)
        );
    }

    public BukkitCommandManager getCommandManager() {
        return commandManager;
    }

    public void registerCommands(CommandClass... commands) {
        for (CommandClass command : commands) {
            commandManager.registerCommands(builder.fromClass(command));
        }
    }

}
