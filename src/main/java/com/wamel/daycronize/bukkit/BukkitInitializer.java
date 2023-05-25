package com.wamel.daycronize.bukkit;

import com.wamel.daycronize.DayCronizeAPI;
import com.wamel.daycronize.bukkit.subscriber.ProxyConnectEventSubscriber;
import com.wamel.daycronize.bukkit.subscriber.ProxyDisconnectEventSubscriber;
import com.wamel.daycronize.bukkit.subscriber.ServerChangeEventSubscriber;
import com.wamel.daycronize.bukkit.subscriber.Subscriber;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Map;

public class BukkitInitializer extends JavaPlugin {

    private static BukkitInitializer instance;
    private static JedisPool pool;

    @Override
    public void onEnable() {
        instance = this;

        RedisConfig.load();

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(0);
        jedisPoolConfig.setMaxTotal(300 * 8);
        jedisPoolConfig.setMaxWaitMillis(-1);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);

        pool = new JedisPool(jedisPoolConfig, RedisConfig.ip, Integer.parseInt(RedisConfig.port), 1000 * 15);

        initSubscribers();

        try (Jedis jedis = pool.getResource()) {
            for (String name : jedis.smembers("day_cronize:player_list")) {
                DayCronizeAPI.playerList.add(name);
            }
            for (Map.Entry<String, String> entry : jedis.hgetAll("day_cronize:server_info").entrySet()) {
                DayCronizeAPI.serverNameMap.put(Integer.parseInt(entry.getKey()), entry.getValue());
            }
        }
    }

    @Override
    public void onDisable() {
        try {
            for (Subscriber subscriber : subscribers) {
                subscriber.stop();
            }
            if (pool != null) {
                if (!pool.isClosed()) {
                    pool.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BukkitInitializer getInstance() {
        return instance;
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    ArrayList<Subscriber> subscribers = new ArrayList<>();
    private void initSubscribers() {
        subscribers.add(new ProxyConnectEventSubscriber());
        subscribers.add(new ProxyDisconnectEventSubscriber());
        subscribers.add(new ServerChangeEventSubscriber());

        for (Subscriber subscriber : subscribers) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    subscriber.start();
                }
            }.runTaskAsynchronously(instance);
        }
    }

}
