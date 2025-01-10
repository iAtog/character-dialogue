package me.iatog.characterdialogue.dialogs.method.npc_control.action;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.method.npc_control.ControlRegistry;
import me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlAction;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ActionData;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ControlData;
import me.iatog.characterdialogue.enums.EquipmentType;
import me.iatog.characterdialogue.libraries.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static me.iatog.characterdialogue.dialogs.method.npc_control.NPCControlMethod.registries;

public class NPCEquipAction implements NPCControlAction {
    @Override
    public void execute(ActionData ctx) {
        ControlRegistry registry = registries.get(ctx.player().getUniqueId());
        ControlData data = registry.get(ctx.targetNPC().getId());
        MethodConfiguration configuration = ctx.context().getConfiguration();

        if(data != null) {
            AdaptedNPC clone = data.getCopy();
            String item = configuration.getString("item", "null");
            String equipment = configuration.getString("slot");
            boolean useMaterial = item == null || item.equals("null");

            ItemStack itemStack;

            if(equipment == null || equipment.isEmpty()) {
                Objects.requireNonNull(equipment, "Invalid equipment type provided '" + equipment + "' in npc_control method");
                ctx.context().next();
                return;
            }

            if(useMaterial) {
                String mat = configuration.getString("material", "BARRIER");
                try {
                    Material material = Material.valueOf(mat.toUpperCase());
                    itemStack = new ItemStack(material);
                } catch(Exception ex) {
                    ctx.plugin().getLogger().severe("Invalid item material provided '" + mat +
                          "' in npc_control method: " + ex.getMessage());
                    ctx.context().next();
                    return;
                }
            } else {
                String itemId = configuration.getString("item");
                ItemManager itemManager = ctx.plugin().getServices().getItemManager();

                if(itemId == null || !itemManager.existsItem(itemId)) {
                    ctx.plugin().getLogger().warning("The item '" + itemId + "' was not found.");
                    ctx.context().next();
                    return;
                }

                itemStack = itemManager.getItem(itemId);
            }

            try {
                if(itemStack != null) {
                    EquipmentType equipmentType = EquipmentType.valueOf(equipment.toUpperCase());
                    clone.equip(ctx.player(), equipmentType, itemStack);
                }
            } catch (Exception ex) {
                ctx.plugin().getLogger().severe("Error while equipping item to npc: " + ex.getMessage());
            }

            ctx.context().next();
        }
    }

    @Override
    public String getName() {
        return "equip";
    }
}
