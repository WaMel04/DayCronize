package com.wamel.daycronize.proxy;

import com.wamel.daycronize.proxy.listener.ProxyEventListener;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public final class ProxyInitializer extends Plugin {

    private static ProxyInitializer instance;
    private static Jedis jedis;

    @Override
    public void onEnable() {
        instance = this;
        jedis = new Jedis("localhost", 6379);

        getProxy().getPluginManager().registerListener(this, new ProxyEventListener());
        initBukkitPacketListenerSubscriber();
    }

    @Override
    public void onDisable() {
        jedis.close();
    }

    public static ProxyInitializer getInstance() {
        return instance;
    }

    public static Jedis getJedis() {
        return jedis;
    }

    private void initBukkitPacketListenerSubscriber() {
        getProxy().getScheduler().runAsync(instance, new Runnable() {
            @Override
            public void run() {
                String channelName = "DayCronize_BukkitPacketListenerRegistration";
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (!channel.equalsIgnoreCase(channelName))
                            return;

                        // port
                        getProxy().getConsole().sendMessage("§e[DayCronize] " + message + " 포트의 서버와 연결에 성공했습니다.");
                    }
                };

                jedis.subscribe(jedisPubSub, channelName);
            }
        });
    }

}
