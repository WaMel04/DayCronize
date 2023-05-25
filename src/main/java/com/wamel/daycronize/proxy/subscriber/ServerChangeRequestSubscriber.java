package com.wamel.daycronize.proxy.subscriber;


import com.wamel.daycronize.bukkit.subscriber.Subscriber;
import com.wamel.daycronize.proxy.ProxyInitializer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class ServerChangeRequestSubscriber extends Subscriber {

    private Jedis subscriber;
    private String channelName = "DayCronize_ServerChangeRequestSubscriber";

    public ServerChangeRequestSubscriber() {
        subscriber = new Jedis("localhost", 6379, 1000 * 15);
    }

    @Override
    public void start() {
        try {
            subscriber.connect();
            subscriber.subscribe(this, channelName);
        } catch (Exception e) {
        }
    }

    @Override
    public void stop() {
        this.unsubscribe();
        subscriber.close();
    }

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equalsIgnoreCase(channelName)) {
            // serverName | uuid
            String[] splitMessage = message.split("\\|");
            String serverName = splitMessage[0];
            String uuid = splitMessage[1];

            ProxiedPlayer player = ProxyInitializer.getInstance().getProxy().getPlayer(UUID.fromString(uuid));

            if (player != null) {
                ServerInfo info = ProxyInitializer.getInstance().getProxy().getServerInfo(serverName);

                if (info != null) {
                    player.connect(info);
                }
            }
        }
    }

}
