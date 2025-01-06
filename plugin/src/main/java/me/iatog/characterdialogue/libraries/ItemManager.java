package me.iatog.characterdialogue.libraries;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    private final CharacterDialoguePlugin main;
    private final Map<String, ItemStack> items;

    public ItemManager(CharacterDialoguePlugin main) {
        this.main = main;
        this.items = new HashMap<>();
        loadItemStacks();
    }

    public ItemStack getItem(String id) {
        return items.get(id);
    }

    public boolean existsItem(String id) {
        return items.containsKey(id);
    }

    public Map<String, ItemStack> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public void saveItem(String id, ItemStack itemStack) {
        YamlDocument itemsFile = main.getFileFactory().getItems();
        //itemsFile.set(id, itemStack);
        itemsFile.set(id, itemStack);
        try {
            itemsFile.save();
            items.put(id, itemStack);
        } catch (IOException e) {
            main.getLogger().severe("Error saving item: " + e.getMessage());
        }
    }

    public void delete(String id) {
        if(!items.containsKey(id)) {
            return;
        }

        YamlDocument itemsFile = main.getFileFactory().getItems();
        itemsFile.remove(id);

        try {
            itemsFile.save();
            items.remove(id);
        } catch (IOException e) {
            main.getLogger().severe("Error deleting item: " + e.getMessage());
        }
    }

    private void loadItemStacks() {
        YamlDocument itemsFile = main.getFileFactory().getItems();

        for(String itemId : itemsFile.getRoutesAsStrings(false)) {
            ItemStack item = itemsFile.getAs(itemId, ItemStack.class);
            items.put(itemId, item);
        }
    }
}
