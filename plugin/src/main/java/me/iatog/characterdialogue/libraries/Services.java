package me.iatog.characterdialogue.libraries;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.follow.FollowingNPC;

public class Services {

    private final FollowingNPC followingNPC;

    public Services(CharacterDialoguePlugin main) {
        this.followingNPC = new FollowingNPC(main);
    }

    public FollowingNPC getFollowingNPC() {
        return followingNPC;
    }

}
