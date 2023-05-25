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
        if (event.isCancelled())
            return;

        ProxiedPlayer player = event.getPlayer();

        if (event.getTarget() == null)
            return;

        Integer toServerPort = event.getTarget().getAddress().getPort();

        if (event.getPlayer().getServer() == null) {
            try (Jedis jedis = ProxyInitializer.getJedis()) {
                // toServerPort | uuid | playerName
                jedis.publish("DayCronize_ProxyConnectEvent", toServerPort + "|" + player.getUniqueId().toString() + "|" + player.getName());
                jedis.sadd("day_cronize.player_list", player.getName());
            }
        } else {
            Integer startServerPort = event.getPlayer().getServer().getInfo().getAddress().getPort();

            if (startServerPort == toServerPort)
                return;

            try (Jedis jedis = ProxyInitializer.getJedis()) {
                // startServerPort | toServerPort | uuid | playerName
                jedis.publish("DayCronize_ServerChangeEvent", startServerPort + "|" + toServerPort + "|" + player.getUniqueId().toString() + "|" + player.getName());
            }
        }
    }

    @EventHandler
    public void onProxyDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() == null)
            return;


        Integer leaveServerPort = player.getServer().getInfo().getAddress().getPort();

        try (Jedis jedis = ProxyInitializer.getJedis()) {
            // leaveServerPort | uuid | playerName
            jedis.publish("DayCronize_ProxyDisconnectEvent", leaveServerPort + "|" + player.getUniqueId().toString() + "|" + player.getName());
            jedis.srem("day_cronize.player_list", player.getName());
        }
    }

}
