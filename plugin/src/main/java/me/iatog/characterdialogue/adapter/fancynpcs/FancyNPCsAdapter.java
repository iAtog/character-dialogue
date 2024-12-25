package me.iatog.characterdialogue.adapter.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import me.iatog.characterdialogue.listeners.fancynpcs.FancyNPCSListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class FancyNPCsAdapter extends NPCAdapter<Npc> {

    @Override
    public AdaptedNPC adapt(Npc npc) {
        if(npc == null) return null;
        return new AdaptedFancyNPC(npc, this);
    }

    @Override
    public AdaptedNPC getById(String id) {
        return this.adapt(FancyNpcsPlugin.get().getNpcManager().getNpc(id));
    }

    @Override
    public List<String> getNPCs() {
        List<String> list = new ArrayList<>();

        for(Npc npc : FancyNpcsPlugin.get().getNpcManager().getAllNpcs()) {
            list.add(npc.getData().getId());
        }

        return list;
    }

    @Override
    public void registerEvents(JavaPlugin main) {
        registerListener(main,
              new FancyNPCSListener((CharacterDialoguePlugin) main)
        );
    }

    @Override
    public String getName() {
        return "FancyNPCs Adapter";
    }
}
