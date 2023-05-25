package com.wamel.daycronize.proxy;

import com.wamel.daycronize.proxy.listener.ProxyEventListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public final class ProxyInitializer extends Plugin {

    private static ProxyInitializer instance;
    private static JedisPool pool;

    @Override
    public void onEnable() {
        instance = this;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        pool = new JedisPool(jedisPoolConfig, "localhost", 6379, 1000 * 15);

        getProxy().getPluginManager().registerListener(this, new ProxyEventListener());
    }

    @Override
    public void onDisable() {
        pool.close();
    }

    public static ProxyInitializer getInstance() {
        return instance;
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

}
