package com.wamel.daycronize.proxy.listener;

import com.wamel.daycronize.proxy.ProxyInitializer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

public class ProxyEventListener implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Integer toServerPort = event.getTarget().getAddress().getPort();

        Jedis jedis = new Jedis("localhost", 6379);

        ProxyInitializer.getInstance().getProxy().broadcast(event.getPlayer().getName() + " " + toServerPort + " " + event.getPlayer().getServer());

        if (event.getPlayer().getServer() == null) {
            // toServerPort | uuid
            jedis.publish("DayCronize_ProxyConnectEvent", toServerPort + "|" + player.getUniqueId().toString());
        } else {
            Integer startServerPort = event.getPlayer().getServer().getInfo().getAddress().getPort();

            // startServerPort | toServerPort | uuid
            jedis.publish("DayCronize_ServerChangeEvent", startServerPort + "|" + toServerPort + "|" + player.getUniqueId().toString());
        }
    }

    @EventHandler
    public void onProxyDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Integer leaveServerPort = player.getServer().getInfo().getAddress().getPort();

        Jedis jedis = new Jedis("localhost", 6379);
        // leaveServerPort | uuid
        jedis.publish("DayCronize_ProxyDisconnectEvent", leaveServerPort + "|" + player.getUniqueId().toString());
    }

}
