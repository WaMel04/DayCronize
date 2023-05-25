package com.wamel.daycronize.bukkit.subscriber;


import com.wamel.daycronize.bukkit.BukkitInitializer;
import com.wamel.daycronize.bukkit.RedisConfig;
import com.wamel.daycronize.event.ProxyConnectEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;

public class ProxyConnectEventSubscriber extends Subscriber {

    private Jedis subscriber;
    private String channelName = "DayCronize_ProxyConnectEvent";
    BukkitTask task;

    public ProxyConnectEventSubscriber() {
        subscriber = new Jedis(RedisConfig.ip, Integer.parseInt(RedisConfig.port), 1000 * 15);
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
            // toServerPort | uuid | playerName
            String[] splitMessage = message.split("\\|");
            String toServerPort = splitMessage[0];
            String uuid = splitMessage[1];
            String playerName = splitMessage[2];

            ProxyConnectEvent event = new ProxyConnectEvent(Integer.parseInt(toServerPort), uuid, playerName);
            Bukkit.getScheduler().runTask(BukkitInitializer.getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
        }
    }

}
