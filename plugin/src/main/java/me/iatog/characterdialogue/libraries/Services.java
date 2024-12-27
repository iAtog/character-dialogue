package me.iatog.characterdialogue.libraries;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.follow.FollowingNPC;

public class Services {

    private final FollowingNPC followingNPC;
    private final ItemManager itemManager;

    public Services(CharacterDialoguePlugin main) {
        this.followingNPC = new FollowingNPC(main);
        this.itemManager = new ItemManager(main);
    }

    public FollowingNPC getFollowingNPC() {
        return followingNPC;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

}
