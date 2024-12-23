package me.iatog.characterdialogue.adapter.citizens;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.adapter.NPCAdapter;
import me.iatog.characterdialogue.dialogs.method.npc_control.trait.FollowPlayerTrait;
import me.iatog.characterdialogue.listeners.citizens.CitizensListener;
import me.iatog.characterdialogue.path.PathTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CitizensAdapter extends NPCAdapter<NPC> {

    public CitizensAdapter() {
        super();
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(PathTrait.class).withName("path_trait"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FollowPlayerTrait.class));
    }

    @Override
    public AdaptedNPC adapt(NPC npc) {
        return new AdaptedCitizensNPC(npc);
    }

    @Override
    public AdaptedNPC getById(String id) {
        NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(id));
        return npc == null ? null : adapt(npc);
    }

    @Override
    public List<String> getNPCs() {
        List<String> list = new ArrayList<>();
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        Iterator<NPC> npcIterator = registry.iterator();
        npcIterator.forEachRemaining(npc -> {
            list.add(String.valueOf(npc.getId()));
        });

        return list;
    }

    @Override
    public void registerEvents(JavaPlugin main) {
        registerListener(main,
              new CitizensListener((CharacterDialoguePlugin) main)
        );
    }

    @Override
    public String getName() {
        return "Citizens Adapter";
    }
}
