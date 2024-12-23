package me.iatog.characterdialogue.dialogs.method.npc_control;

import me.iatog.characterdialogue.adapter.AdaptedNPC;
import me.iatog.characterdialogue.dialogs.method.npc_control.data.ControlData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControlRegistry {

    private final UUID playerId;

    // Integer = the original npc id
    // FollowData = original and his clone
    private final Map<String, ControlData> npcMap;

    public ControlRegistry(Player player) {
        this.npcMap = new HashMap<>();
        this.playerId = player.getUniqueId();
    }

    public ControlData get(String npcId) {
        return npcMap.get(npcId);
    }

    public void addNPC(AdaptedNPC original, AdaptedNPC clone) {
        this.npcMap.put(original.getId(), new ControlData(original, clone));
    }

    public ControlData removeNPC(String npcId) {
        return this.npcMap.remove(npcId);
    }

    public ControlData removeNPC(AdaptedNPC originalNpc) {
        return this.removeNPC(originalNpc.getId());
    }

    public boolean isOnRegistry(String npcId) {
        return this.npcMap.containsKey(npcId);
    }

    public AdaptedNPC findCopy(String id) {
        for (ControlData data : npcMap.values()) {
            if (data.getCopy().getId().equals(id)) {
                return data.getCopy();
            }
        }

        return null;
    }


    public void clearAll() {
        this.npcMap.forEach((id, data) -> {
            data.getCopy().destroy();
        });

        this.npcMap.clear();
    }

    public UUID getOwner() {
        return playerId;
    }

}