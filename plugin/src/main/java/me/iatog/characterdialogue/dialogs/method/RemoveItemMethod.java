package me.iatog.characterdialogue.dialogs.method;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.ConfigurationType;
import me.iatog.characterdialogue.dialogs.DialogMethod;
import me.iatog.characterdialogue.dialogs.MethodConfiguration;
import me.iatog.characterdialogue.dialogs.MethodContext;
import me.iatog.characterdialogue.libraries.ItemManager;
import org.bukkit.inventory.ItemStack;

public class RemoveItemMethod extends DialogMethod<CharacterDialoguePlugin> {

    public RemoveItemMethod(CharacterDialoguePlugin provider) {
        super("remove_item", provider);

        addConfigurationType("item", ConfigurationType.TEXT);
    }

    @Override
    public void execute(MethodContext context) {
        ItemManager manager = getProvider().getServices().getItemManager();
        MethodConfiguration configuration = context.getConfiguration();
        String itemId = configuration.getString("item");

        if(!configuration.contains("item") || itemId == null || !manager.existsItem(itemId)) {
            getProvider().getLogger().severe("No item found/specified in remove_item method.");
            context.next();
        } else {
            ItemStack itemStack = manager.getItem(itemId);
            context.getPlayer().getInventory().remove(itemStack);
        }
    }
}
