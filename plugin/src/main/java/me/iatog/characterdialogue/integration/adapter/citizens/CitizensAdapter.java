package me.iatog.characterdialogue.integration.adapter.citizens;

import me.iatog.characterdialogue.api.NPCCapability;
import me.iatog.characterdialogue.integration.adapter.AdaptedNPC;
import me.iatog.characterdialogue.integration.adapter.NPCAdapter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;

import java.util.Set;

public class CitizensAdapter implements NPCAdapter<NPC> {

    public CitizensAdapter() {
        super();
        //CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(PathTrait.class).withName("path_trait"));
        //CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FollowPlayerTrait.class));
    }

    @Override
    public String getName() {
        return "CitizensAdapter";
    }

    @Override
    public AdaptedNPC adapt(NPC npc) {
        return new AdaptedCitizensNPC(npc);
    }

    @Override
    public Set<NPCCapability> getSupportedCapabilities() {
        return Set.of(
              NPCCapability.EQUIPMENT_CHANGE,
              NPCCapability.FOLLOW_PLAYER,
              NPCCapability.PATH_FOLLOWING,
              NPCCapability.HOLOGRAM_SUPPORT
        );
    }
}
