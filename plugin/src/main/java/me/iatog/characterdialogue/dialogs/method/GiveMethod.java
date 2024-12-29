package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.libraries.ItemManager;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GiveMethod extends DialogMethod<CharacterDialoguePlugin> {
    // give{material=flint, amount=32, name="Super flint"}
    public GiveMethod(CharacterDialoguePlugin main) {
        super("give", main);

        addConfigurationType("material", ConfigurationType.TEXT);
        addConfigurationType("amount", ConfigurationType.INTEGER);
        addConfigurationType("name", ConfigurationType.TEXT);
        addConfigurationType("item", ConfigurationType.TEXT);
        setDescription("Gives an item to the player, it can be a custom item.");
    }

    @Override
    public void execute(MethodContext context) {
        MethodConfiguration configuration = context.getConfiguration();
        DialogSession session = context.getSession();

        if(configuration.contains("item")) {
            String itemId = configuration.getString("item");
            ItemManager itemManager = getProvider().getServices().getItemManager();

            if(!itemManager.existsItem(itemId)) {
                getProvider().getLogger().severe("No item found with name: " + itemId);
                context.next();
                return;
            }

            ItemStack itemStack = itemManager.getItem(itemId);
            context.getPlayer().getInventory().addItem(itemStack);
            context.next();
            return;
        }

        Material material;
        int amount = configuration.getInteger("amount", 1);

        try {
            material = Material.valueOf(configuration.getString("material").toUpperCase());
        } catch (Exception exception) {
            String msg = "The item material '" + configuration.getString("material") + "' is not valid.";
            session.sendDebugMessage(msg, "GiveMethod");
            getProvider().getLogger().warning(msg);
            context.next();
            return;
        }
        ItemStack itemStack = new ItemStack(material, amount);
        String name = configuration.getString("name", "");
        if (! name.isEmpty()) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(name);
            itemStack.setItemMeta(meta);
        }

        context.getPlayer().getInventory().addItem(itemStack);
        context.next();
    }
}
