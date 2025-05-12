package me.iatog.characterdialogue.integration.adapter.citizens;

import me.iatog.characterdialogue.api.NPCAdapterInfo;
import me.iatog.characterdialogue.integration.adapter.AdapterException;
import me.iatog.characterdialogue.integration.adapter.AdapterFactory;
import me.iatog.characterdialogue.integration.adapter.NPCAdapter;

@NPCAdapterInfo(
      pluginName = "Citizens",
      version = "2.0.38"
)
public class CitizensAdapterFactory implements AdapterFactory {
    @Override
    public NPCAdapter<?> createAdapter() throws AdapterException {
        try {
            Class.forName("net.citizensnpcs.api.npc.NPC");

            Class<?> adapterClass = Class.forName("me.iatog.characterdialogue.integration.adapter.citizens.CitizensAdapter");
            return (NPCAdapter<?>) adapterClass.getDeclaredConstructor().newInstance();
        } catch(Exception ex) {
            throw new AdapterException("Citizens", "Required Citizens classes not found", ex);
        }
    }
}
