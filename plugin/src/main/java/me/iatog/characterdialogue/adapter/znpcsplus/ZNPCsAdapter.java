package me.iatog.characterdialogue.adapter.znpcsplus;

import me.iatog.characterdialogue.adapter.NPCAdapter;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.listeners.znpcsplus.ZNPCsPlusListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ZNPCsAdapter extends NPCAdapter<NpcEntry> {

    public ZNPCsAdapter() {
        super();
    }

    @Override
    public AdaptedNPC adapt(NpcEntry npc) {
        return new AdaptedZNPC(npc, this);
    }

    @Override
    public AdaptedNPC getById(String id) {
        return adapt(NpcApiProvider.get().getNpcRegistry().getById(id));
    }

    @Override
    public List<String> getNPCs() {
        return NpcApiProvider.get().getNpcRegistry().getAllIds().stream().toList();
    }

    @Override
    public void registerEvents(JavaPlugin main) {
        registerListener(main, new ZNPCsPlusListener((CharacterDialoguePlugin) main));
    }

    @Override
    public String getName() {
        return "zNPCs Adapter";
    }
}
