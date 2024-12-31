package me.iatog.characterdialogue.libraries;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.database.DialogPersistence;
import me.iatog.characterdialogue.database.PlayerDataDatabase;
import me.iatog.characterdialogue.dialogs.method.npc_control.follow.FollowingNPC;

public class Services {

    private final FollowingNPC followingNPC;
    private final ItemManager itemManager;
    private final DialogPersistence dialogPersistence;
    private final PlayerDataDatabase playerDataDatabase;

    public Services(CharacterDialoguePlugin main) {
        this.followingNPC = new FollowingNPC(main);
        this.itemManager = new ItemManager(main);
        this.dialogPersistence = new DialogPersistence(main);
        this.playerDataDatabase = new PlayerDataDatabase(main);
    }

    public FollowingNPC getFollowingNPC() {
        return followingNPC;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public DialogPersistence getDialogPersistence() {
        return dialogPersistence;
    }

    public PlayerDataDatabase getPlayerDataDatabase() {
        return playerDataDatabase;
    }
}
