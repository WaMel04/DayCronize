package com.wamel.daycronize.bukkit;

import com.wamel.daycronize.event.ProxyConnectEvent;
import com.wamel.daycronize.event.ProxyDisconnectEvent;
import com.wamel.daycronize.event.ServerChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class BukkitInitializer extends JavaPlugin {

    private static BukkitInitializer instance;
    private static Jedis jedis;

    @Override
    public void onEnable() {
        instance = this;

        jedis = new Jedis("localhost", 6379);

        jedis.publish("DayCronize_BukkitPacketListenerRegistration", String.valueOf(Bukkit.getPort()));
        Bukkit.getConsoleSender().sendMessage("§e[DayCronize] Proxy와의 연결을 시도합니다...");

        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                String channelName = "DayCronize_ProxyConnectEvent";
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (channel.equalsIgnoreCase(channelName)) {
                            // toServerPort | uuid
                            String[] splitMessage = message.split("\\|");
                            String toServerPort = splitMessage[0];
                            String uuid = splitMessage[1];

                            if (Integer.parseInt(toServerPort) == Bukkit.getPort()) {
                                ProxyConnectEvent event = new ProxyConnectEvent(Integer.parseInt(toServerPort), uuid);
                                Bukkit.getScheduler().runTask(getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
                            }
                        }
                    }

                    @Override
                    public void onSubscribe(String channel, int subscribedChannels) {
                        Bukkit.getConsoleSender().sendMessage("§e[DayCronize] Proxy와 연결에 성공했습니다.");
                    }

                };

                jedis.subscribe(jedisPubSub, channelName);
            }
        });
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                String channelName = "DayCronize_ProxyDisconnectEvent";
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (channel.equalsIgnoreCase(channelName)) {
                            // leaveServerPort | uuid
                            String[] splitMessage = message.split("\\|");
                            String leaveServerPort = splitMessage[0];
                            String uuid = splitMessage[1];

                            if (Integer.parseInt(leaveServerPort) == Bukkit.getPort()) {
                                ProxyDisconnectEvent event = new ProxyDisconnectEvent(Integer.parseInt(leaveServerPort), uuid);
                                Bukkit.getScheduler().runTask(getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
                            }
                        }
                    }
                };

                jedis.subscribe(jedisPubSub, channelName);
            }
        });
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                String channelName = "DayCronize_ServerChangeEvent";
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (channel.equalsIgnoreCase(channelName)) {
                            // startServerPort | toServerPort | uuid
                            String[] splitMessage = message.split("\\|");
                            String startServerPort = splitMessage[0];
                            String toServerPort = splitMessage[1];
                            String uuid = splitMessage[2];

                            if (Integer.parseInt(startServerPort) == Bukkit.getPort() || Integer.parseInt(toServerPort) == Bukkit.getPort()) {
                                ServerChangeEvent event = new ServerChangeEvent(Integer.parseInt(startServerPort), Integer.parseInt(toServerPort), uuid);

                                Bukkit.getScheduler().runTask(getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
                            }
                        }
                    }
                };

                jedis.subscribe(jedisPubSub, channelName);
            }
        });
    }

    @Override
    public void onDisable() {
        jedis.close();
    }

    public static BukkitInitializer getInstance() {
        return instance;
    }

    public static Jedis getJedis() {
        return jedis;
    }

}
