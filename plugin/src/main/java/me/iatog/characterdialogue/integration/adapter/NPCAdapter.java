package me.iatog.characterdialogue.integration.adapter;

import me.iatog.characterdialogue.api.NPCCapability;

import java.util.Set;

public interface NPCAdapter<T> {
    String getName();
    AdaptedNPC adapt(T npc);

    default boolean hasCapability(NPCCapability capability) {
        return getSupportedCapabilities().contains(capability);
    }

    Set<NPCCapability> getSupportedCapabilities();
}
