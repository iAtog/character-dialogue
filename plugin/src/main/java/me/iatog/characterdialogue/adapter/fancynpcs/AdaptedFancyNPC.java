package me.iatog.characterdialogue.adapter.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.enums.EquipmentType;
import me.iatog.characterdialogue.dialogs.method.npc_control.follow.FollowingNPC;
import me.iatog.characterdialogue.path.PathRunnable;
import me.iatog.characterdialogue.path.RecordLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class AdaptedFancyNPC implements AdaptedNPC {

    private static final CharacterDialoguePlugin main = CharacterDialoguePlugin.getInstance();

    private final Npc npc;
    private final FancyNPCsAdapter adapter;

    private BukkitTask task;

    public AdaptedFancyNPC(Npc npc, FancyNPCsAdapter adapter) {
        this.npc = npc;
        this.adapter = adapter;
    }

    @Override
    public String getName() {
        return npc.getData().getName();
    }

    @Override
    public String getId() {
        return npc.getData().getName();
    }

    @Override
    public Location getStoredLocation() {
        return npc.getData().getLocation();
    }

    @SuppressWarnings("deprecation")
    @Override
    public AdaptedNPC copy() {
        NpcData data = new NpcData(getId() + "-" + generateId(5) + "_cloned",
              UUID.randomUUID(),
              npc.getData().getLocation().clone());
        data.setType(npc.getData().getType());
        data.setCollidable(false);
        if(npc.getData().getType() == EntityType.PLAYER) {
            data.setSkin(npc.getData().getSkin());
        }

        data.applyAllAttributes(npc);
        data.setLocation(npc.getData().getLocation());
        data.setCollidable(npc.getData().isCollidable());
        data.setDisplayName("<empty>");

        final Npc copied = FancyNpcsPlugin.get().getNpcAdapter().apply(data);

        copied.setSaveToFile(false);
        copied.create();
        FancyNpcsPlugin.get().getNpcManager().registerNpc(copied);
        
        return adapter.adapt(copied);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void destroy() {
        npc.removeForAll();
        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
        npc.updateForAll();
    }

    @Override
    public void spawn(Location location) {
        teleport(location);
    }

    @Override
    public void teleport(Location location) {
        npc.getData().setLocation(location);
        npc.moveForAll(false);
    }

    @Override
    public void faceLocation(Player player) {
        npc.lookAt(player, player.getLocation());
        npc.update(player);
    }

    @Override
    public void equip(Player player, EquipmentType type, ItemStack item) {
        NpcEquipmentSlot slot = parseSlot(type);
        if(slot == null) {
            main.getLogger().warning("Invalid equipment type provided in NPC_CONTROL type");
            return;
        }
        npc.getData().addEquipment(slot, item);
        npc.update(player);
    }

    @Override
    public void sneak(Player player, boolean sneaking) {
        NpcAttribute poseAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, "pose");

        if(sneaking) {
            npc.getData().addAttribute(poseAttr, "CROUCHING");
        } else {
            npc.getData().getAttributes().remove(poseAttr);
        }

        npc.update(player);
    }

    private NpcEquipmentSlot parseSlot(EquipmentType type) {
        switch(type) {
            case HEAD -> {
                return NpcEquipmentSlot.HEAD;
            }
            case BODY -> {
                return NpcEquipmentSlot.CHEST;
            }
            case LEGS -> {
                return NpcEquipmentSlot.LEGS;
            }
            case BOOTS -> {
                return NpcEquipmentSlot.FEET;
            }
            case MAIN_HAND -> {
                return NpcEquipmentSlot.MAINHAND;
            }
            case OFF_HAND -> {
                return NpcEquipmentSlot.OFFHAND;
            }
        }

        return null;
    }

    @Override
    public void follow(Player player) {
        if(task != null) {
            task.cancel();
            task = null;
        }

        FollowingNPC following = main.getServices().getFollowingNPC();
        Entity followingEntity = following.createFollowingEntity(player, getStoredLocation());

        following.scheduleNPCMovement(this, followingEntity, player);
    }

    @Override
    public void unfollow(Player player) {
        FollowingNPC following = CharacterDialoguePlugin.getInstance().getServices().getFollowingNPC();
        following.stopAndRemoveEntity(getId());
    }

    @Override
    public void followPath(List<RecordLocation> locations, @Nullable Player viewer) {
        if(task != null) {
            task.cancel();
        }

        this.task = new PathRunnable(locations, this, viewer)
              .runTaskTimer(CharacterDialoguePlugin.getInstance(), 0, 1);
    }

    @Override
    public void show(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            npc.spawn(player);
        });
    }

    @Override
    public void hide(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            npc.remove(player);
        });
    }

    @Override
    public void hideForAll() {
        npc.removeForAll();
    }
}
