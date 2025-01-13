package me.iatog.characterdialogue.libraries;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.player.PlayerData;
import org.bukkit.entity.Player;

public class PlayerPacketListenerManager implements PacketListener {
    // TESTING CLASS
    private final CharacterDialoguePlugin main;

    public PlayerPacketListenerManager(CharacterDialoguePlugin main) {
        this.main = main;
    }

    public void _onPacketReceive(PacketReceiveEvent event) {
        Player player = event.getPlayer();
        if(event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            main.getLogger().info("Blocked PacketPlayClientPluginMessage");
        }
        if (event.getPacketType() != PacketType.Play.Client.CHAT_MESSAGE ||
              !main.getCache().getDialogSessions().containsKey(player.getUniqueId())) {
            if(event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
                main.getLogger().info("Blocked PacketPlayClientChatMessage");
            }

            return;
        }

        WrapperPlayClientChatMessage chat = new WrapperPlayClientChatMessage(event);
        //chat.read();
        //Component component = chat.readComponent();
        PlayerData data = main.getApi().getData(player);

        data.addMessage(chat.readComponent());
        main.getLogger().info("Component RECEIVED: " + chat.readComponent().toString());

        event.setCancelled(true);
    }

    public void _onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();

        if(event.getPacketType() == PacketType.Play.Server.CHAT_MESSAGE) {
            //WrapperPlayClientChatMessage pm = new WrapperPlayClientChatMessage(event);

            //main.getLogger().info("PacketPlayServerChatMessage: " + pm.readMessageSignature().toString());
        }

        if(event.getPacketType() == PacketType.Play.Server.CHAT_MESSAGE) {
            WrapperPlayServerChatMessage chat = new WrapperPlayServerChatMessage(event);
            main.getLogger().info("PacketPlayServerChatMessage: " + chat.getMessage());
            //Component comp = chat.readComponentAsJSON();

            //main.getLogger().warning("PACKET SENDED: " + chat.readComponentAsJSON().toString());
        }
    }
}
