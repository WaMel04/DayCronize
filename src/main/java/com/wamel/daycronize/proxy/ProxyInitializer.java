package com.wamel.daycronize.proxy;

import com.wamel.daycronize.proxy.listener.ProxyEventListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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

        jedis.del("DayCronize.PlayerList");

        for (ProxiedPlayer player : getProxy().getPlayers()) {
            jedis.sadd("DayCronize.PlayerList", player.getName());
        }
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
