package com.wamel.daycronize.bukkit;

import com.wamel.daycronize.event.ProxyConnectEvent;
import com.wamel.daycronize.event.ProxyDisconnectEvent;
import com.wamel.daycronize.event.ServerChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;

public class BukkitInitializer extends JavaPlugin {

    private static BukkitInitializer instance;
    private static JedisPool pool;

    @Override
    public void onEnable() {
        instance = this;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        pool = new JedisPool(jedisPoolConfig, "localhost", 6379, 1000 * 15);

        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                String channelName = "DayCronize_ProxyConnectEvent";
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (channel.equalsIgnoreCase(channelName)) {
                            // toServerPort | uuid | playerName
                            String[] splitMessage = message.split("\\|");
                            String toServerPort = splitMessage[0];
                            String uuid = splitMessage[1];
                            String playerName = splitMessage[2];

                            ProxyConnectEvent event = new ProxyConnectEvent(Integer.parseInt(toServerPort), uuid, playerName);
                            Bukkit.getScheduler().runTask(getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
                        }
                    }
                };

                Jedis jedis = pool.getResource();

                jedis.subscribe(jedisPubSub, channelName);
                jedis.close();
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
                            // leaveServerPort | uuid | playerName
                            String[] splitMessage = message.split("\\|");
                            String leaveServerPort = splitMessage[0];
                            String uuid = splitMessage[1];
                            String playerName = splitMessage[2];

                            ProxyDisconnectEvent event = new ProxyDisconnectEvent(Integer.parseInt(leaveServerPort), uuid, playerName);
                            Bukkit.getScheduler().runTask(getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
                        }
                    }
                };

                Jedis jedis = pool.getResource();

                jedis.subscribe(jedisPubSub, channelName);
                jedis.close();
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
                            // startServerPort | toServerPort | uuid | playerName
                            String[] splitMessage = message.split("\\|");
                            String startServerPort = splitMessage[0];
                            String toServerPort = splitMessage[1];
                            String uuid = splitMessage[2];
                            String playerName = splitMessage[3];

                            ServerChangeEvent event = new ServerChangeEvent(Integer.parseInt(startServerPort), Integer.parseInt(toServerPort), uuid, playerName);

                            Bukkit.getScheduler().runTask(getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
                        }
                    }
                };

                Jedis jedis = pool.getResource();

                jedis.subscribe(jedisPubSub, channelName);
                jedis.close();
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
                            String playerName = splitMessage[3];

                            if (Integer.parseInt(startServerPort) == Bukkit.getPort() || Integer.parseInt(toServerPort) == Bukkit.getPort()) {
                                ServerChangeEvent event = new ServerChangeEvent(Integer.parseInt(startServerPort), Integer.parseInt(toServerPort), uuid, playerName);

                                Bukkit.getScheduler().runTask(getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
                            }
                        }
                    }
                };

                Jedis jedis = pool.getResource();

                jedis.subscribe(jedisPubSub, channelName);
                jedis.close();
            }
        });
    }

    public static BukkitInitializer getInstance() {
        return instance;
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }


}
