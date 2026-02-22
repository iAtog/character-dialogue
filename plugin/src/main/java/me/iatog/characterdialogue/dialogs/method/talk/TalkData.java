package me.iatog.characterdialogue.dialogs.method.talk;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.enums.CompletedType;
import me.iatog.characterdialogue.session.DialogSession;
import me.iatog.characterdialogue.util.SingleUseConsumer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public record TalkData(
    Player player, Sound sound,
    float volume, float pitch, boolean skip, TalkType type,
    String npcName, String translatedMessage,
    DialogSession session, SingleUseConsumer<CompletedType> completed,
    CharacterDialoguePlugin provider
) {

}