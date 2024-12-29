package me.iatog.characterdialogue.libraries;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import me.iatog.characterdialogue.api.CharacterDialogueAPI;
import me.iatog.characterdialogue.api.dialog.DialogHologram;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.api.events.ExecuteMethodEvent;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.enums.ClickType;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.placeholders.Placeholders;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.session.EmptyDialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import me.iatog.characterdialogue.util.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiImplementation implements CharacterDialogueAPI {

    private final Pattern lineRegex;

    private final CharacterDialoguePlugin main;
    private final HologramLibrary hologramLibrary;

    public ApiImplementation(CharacterDialoguePlugin main) {
        this.main = main;
        hologramLibrary = new HologramLibrary(main);
        //String regex = "^(\\w+)(?:\\{([^}]*)})?(?::\\s*(.*))?$";
        String regex = "^(\\w+)(?:\\{([^{}]*(?:\\{[^{}]*}[^{}]*)*)})?(?::\\s*(.*))?$";
        this.lineRegex = Pattern.compile(regex);
    }

    public Pattern getLineRegex() {
        return lineRegex;
    }

    public NPCAdapter<?> getAdapter() {
        return this.main.getAdapter();
    }

    @Override
    public HologramLibrary getHologramLibrary() {
        return hologramLibrary;
    }

    @Override
    public void reloadHolograms() {
        if(!main.getAdapter().getName().equals("Citizens Adapter")) {
            return;
        }

        hologramLibrary.reloadHolograms();
    }

    @Override
    public void loadHologram(String npcId) {
       // NPC citizensNpc = CitizensAPI.getNPCRegistry().getById(npcId);
        if(!main.getAdapter().getName().equals("Citizens Adapter")) {
            return;
        }

        AdaptedNPC npc = main.getAdapter().getById(npcId);

        if (npc == null) {
            return;
        }

        Dialogue dialogue = getNPCDialogue(npcId);

        if (dialogue == null) {
            return;
        }

        DialogHologram hologram = dialogue.getHologram();

        if (hologram != null && hologram.isEnabled() && !getHologramLibrary().hasHologram(npcId)) {
            Location location = npc.getStoredLocation();
            location.add(0, 2 + hologram.getY(), 0);
            String npcName = dialogue.getDisplayName();
            List<String> lines = hologram.getLines();

            hologramLibrary.addHologram(lines, location, npcName, npcId);

            //citizensNpc.setAlwaysUseNameHologram(false);
        }
    }

    @Override
    public Dialogue getDialogue(String name) {
        return main.getCache().getDialogues().get(name);
    }

    @Override
    public boolean readDialogBy(Player player, String dialog) {
        String path = "players." + player.getUniqueId();
        YamlDocument playerCache = main.getFileFactory().getPlayerCache();
        List<String> readedDialogues = playerCache.getStringList(path + ".readed-dialogues");

        if (! playerCache.contains(path)) {
            readedDialogues = new ArrayList<>();
        }

        if (readedDialogues.contains(dialog)) {
            return false;
        }

        readedDialogues.add(dialog);
        playerCache.set(path + ".readed-dialogues", readedDialogues);

        try {
            playerCache.save();
        } catch (IOException e) {
            main.getLogger().warning("Player cache failed saving");
        }
        return true;
    }

    @Override
    public boolean wasReadedBy(Player player, String dialog) {
        YamlDocument playerCache = main.getFileFactory().getPlayerCache();
        String path = "players." + player.getUniqueId();
        List<String> readedDialogues = playerCache.getStringList(path + ".readed-dialogues");

        return playerCache.contains(path) && readedDialogues.contains(dialog);
    }

    @Override
    public Dialogue getNPCDialogue(String id) {
        return getDialogue(getNPCDialogueName(id));
    }

    @Override
    public String getNPCDialogueName(String id) {
        YamlDocument config = main.getFileFactory().getConfig();
        return config.getString("npc." + id);
    }

    @Override
    public boolean readDialogBy(Player player, Dialogue dialog) {
        return readDialogBy(player, dialog.getName());
    }

    @Override
    public boolean wasReadedBy(Player player, Dialogue dialog) {
        return wasReadedBy(player, dialog.getName());
    }

    @Override
    public void runDialogue(Player player, Dialogue dialogue, boolean debugMode, AdaptedNPC npc) {
        if (main.getCache().getDialogSessions().containsKey(player.getUniqueId())) {
            return;
        }

        DialogSession session = new DialogSession(main, player, dialogue, npc);
        session.setDebugMode(debugMode);
        if (! dialogue.isMovementAllowed()) {
            disableMovement(player);
        }

        main.getCache().getDialogSessions().put(player.getUniqueId(), session);
        session.start(0);
    }

    @Override
    public void runDialogue(Player player, String dialogueName, boolean debugMode, AdaptedNPC npc) {
        runDialogue(player, getDialogue(dialogueName), debugMode, npc);
    }

    @Override
    public void runDialogueExpression(Player player, String dialog) {
        runDialogueExpression(player, dialog, "Dummy");
    }

    public void runDialogueExpression(Player player, String dialog, String npcName) {
        runDialogueExpression(player, dialog, npcName, SingleUseConsumer.create((x) -> {
              }),
              new EmptyDialogSession(main, player, Collections.singletonList(dialog), npcName, null),
              null);
    }

    @Override
    public void runDialogueExpression(Player player, String dialog, String rawNpcName,
                                      SingleUseConsumer<CompletedType> onComplete, DialogSession session, AdaptedNPC npc) {
        Matcher matcher = lineRegex.matcher(dialog.trim());
        String npcName = TextUtils.colorize(rawNpcName);

        if(dialog.startsWith("#")) { // To leave notes, if necessary
            onComplete.accept(CompletedType.CONTINUE);
            return;
        }

        if (!matcher.find()) {
            session.sendDebugMessage("Line '" + dialog + "' don't match.", "runExpression");
            main.getLogger().warning("Line '" + dialog + "' don't match in " + session.getDialogue().getName());
            onComplete.accept(CompletedType.DESTROY);
            return;
        }

        String methodName = matcher.group(1).toLowerCase().trim();
        String configPart = (matcher.group(2) != null ? matcher.group(2).trim() : "")
              .replace("%npc_name%", npcName);
        String arg = matcher.group(3) != null ? matcher.group(3).trim() : "";

        if (!main.getCache().getMethods().containsKey(methodName)) {
            main.getLogger().warning("The method \"" + methodName + "\" doesn't exists");
            onComplete.accept(CompletedType.CONTINUE);
            return;
        }
        session.sendDebugMessage("Running method '" + methodName + "'", "API:197");
        arg = arg.replace("%npc_name%", npcName);
        arg = Placeholders.translate(player, arg);

        MethodConfiguration configuration = new MethodConfiguration(arg, Placeholders.translate(player, configPart));

        DialogMethod<? extends JavaPlugin> method = main.getCache().getMethods().get(methodName);

        if (method.isDisabled()) {
            String msg = "Tried to run a disabled method: " + methodName;
            session.sendDebugMessage(msg, "runExpression");
            main.getLogger().warning(msg);
            onComplete.accept(CompletedType.CONTINUE);
            return;
        }

        ExecuteMethodEvent event = new ExecuteMethodEvent(player, method, ClickType.ALL, - 999, npcName);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            MethodContext context = new MethodContext(player, session, configuration, onComplete, npc);

            try {
                // Dependency check
                List<String> dependencies = method.getDependencies();
                boolean correct = true;
                if (! dependencies.isEmpty()) {
                    for (String depend : dependencies) {
                        if (! Bukkit.getPluginManager().isPluginEnabled(depend)) {
                            correct = false;
                            break;
                        }
                    }
                }

                if (correct) {
                    method.execute(context);
                } else {
                    String msg = "The method '" + methodName +
                          "' requires [" + String.join(", ", dependencies) + "]";
                    session.sendDebugMessage(msg, "runExpression");
                    main.getLogger().severe(msg);
                }
            } catch (Exception exception) {
                session.sendDebugMessage("Exception during &8\"&c" + methodName + "&8\"&7 method execution.",
                      "runExpression");
                main.getLogger().severe("Exception while executing " + methodName + " method.");
                main.getLogger().severe("Dialogue: " + session.getDialogue().getName() + " | Line: " + session.getCurrentIndex());
                exception.printStackTrace();
                onComplete.accept(CompletedType.DESTROY);
            }
        } else {
            session.sendDebugMessage("Method execution cancelled by external plugin", "API:208");
            onComplete.accept(CompletedType.CONTINUE);
        }
    }

    @Override
    public void runDialogueExpressions(Player player, List<String> lines, ClickType clickType, AdaptedNPC npc,
                                       String displayName, String dialogueName) {
        if (main.getCache().getDialogSessions().containsKey(player.getUniqueId())) {
            return;
        }

        DialogSession session = new DialogSession(main, player, lines, clickType, npc,
              displayName == null ? "Dummy" : displayName, dialogueName);

        main.getCache().getDialogSessions().put(player.getUniqueId(), session);
        session.start(0);
    }

    @Override
    public void runDialogueExpressions(Player player, List<String> lines, String displayName, String dialogueName) {
        runDialogueExpressions(player, lines, ClickType.ALL, null, displayName, dialogueName);
    }

    @Override
    public boolean enableMovement(Player player) {
        YamlDocument playerCache = main.getFileFactory().getPlayerCache();
        String playerPath = "players." + player.getUniqueId();

        if (! playerCache.getBoolean(playerPath + ".remove-effect", false)) {
            return false;
        }

        float speed = Float.parseFloat(playerCache.getString(playerPath + ".last-speed"));
        player.setWalkSpeed(speed);
        player.removePotionEffect(PotionEffectType.JUMP);
        main.getCache().getFrozenPlayers().remove(player.getUniqueId());

        playerCache.set(playerPath + ".remove-effect", false);

        try {
            playerCache.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return playerCache.getBoolean(playerPath + ".remove-effect", false);
    }

    @Override
    public boolean disableMovement(Player player) {
        YamlDocument playerCache = main.getFileFactory().getPlayerCache();
        String path = "players." + player.getUniqueId();

        if (playerCache.getBoolean(path + ".remove-effect", false)) {
            return false;
        }

        playerCache.set(path + ".last-speed", player.getWalkSpeed());
        playerCache.set(path + ".remove-effect", true);

        try {
            playerCache.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        main.getCache().getFrozenPlayers().add(player.getUniqueId());
        player.setWalkSpeed(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));

        return ! playerCache.getBoolean(path + ".remove-effect", true);
    }

    @Override
    public boolean canEnableMovement(Player player) {
        YamlDocument playerCache = main.getFileFactory().getPlayerCache();
        String path = "players." + player.getUniqueId();

        return playerCache.getBoolean(path + ".remove-effect", false);
    }

    @Override
    public Map<String, Dialogue> getDialogues() {
        return main.getCache().getDialogues();
    }

    @Override
    public int getBukkitVersion() {
        return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    }
}
