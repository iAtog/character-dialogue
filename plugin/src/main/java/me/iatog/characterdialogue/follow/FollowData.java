package me.iatog.characterdialogue.follow;

import java.util.UUID;

public record FollowData(UUID playerId, UUID entityId, FollowRunnable runnable) {

}