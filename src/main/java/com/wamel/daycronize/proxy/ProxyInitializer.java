package com.wamel.daycronize.proxy;

import com.wamel.daycronize.bukkit.subscriber.Subscriber;
import com.wamel.daycronize.proxy.listener.ProxyEventListener;
import com.wamel.daycronize.proxy.subscriber.ServerChangeRequestSubscriber;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;

public final class ProxyInitializer extends Plugin {

    private static ProxyInitializer instance;
    private static JedisPool pool;

    @Override
    public void onEnable() {
        instance = this;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(0);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);

        pool = new JedisPool(jedisPoolConfig, "localhost", 6379, 1000 * 15);

        getProxy().getPluginManager().registerListener(this, new ProxyEventListener());

        Jedis jedis = pool.getResource();

        jedis.del("day_cronize:player_list");
        jedis.del("day_cronize:server_info");

        for (ProxiedPlayer player : getProxy().getPlayers()) {
            jedis.sadd("day_cronize:player_list", player.getName());
        }
        for (ServerInfo info : getProxy().getServers().values()) {
            jedis.hset("day_cronize:server_info", String.valueOf(info.getAddress().getPort()), info.getName());
        }

        initSubscribers();
    }

    @Override
    public void onDisable() {
        for (Subscriber subscriber : subscribers) {
            subscriber.stop();
        }

        pool.close();
    }

    public static ProxyInitializer getInstance() {
        return instance;
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    ArrayList<Subscriber> subscribers = new ArrayList<>();
    private void initSubscribers() {
        subscribers.add(new ServerChangeRequestSubscriber());

        for (Subscriber subscriber : subscribers) {
            getProxy().getScheduler().runAsync(instance, () -> {
                subscriber.start();
            });
        }
    }

}
