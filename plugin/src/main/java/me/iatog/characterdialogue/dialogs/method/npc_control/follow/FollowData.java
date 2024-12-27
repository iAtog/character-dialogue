package me.iatog.characterdialogue.dialogs.method.npc_control.follow;

import java.util.UUID;

public record FollowData(UUID playerId, UUID entityId, FollowRunnable runnable) {

}