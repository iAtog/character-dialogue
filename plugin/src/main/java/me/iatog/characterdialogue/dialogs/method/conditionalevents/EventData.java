package me.iatog.characterdialogue.dialogs.method.conditionalevents;

import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;

public record EventData(String getArgument, DialogSession getSession, long expTime,
                        SingleUseConsumer<CompletedType> consumer, boolean isPaused,
                        String action, String onTimeout) {
}
