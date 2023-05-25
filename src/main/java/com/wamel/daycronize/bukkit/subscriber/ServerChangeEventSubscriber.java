package com.wamel.daycronize.bukkit.subscriber;


import com.wamel.daycronize.bukkit.BukkitInitializer;
import com.wamel.daycronize.bukkit.RedisConfig;
import com.wamel.daycronize.event.ServerChangeEvent;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

public class ServerChangeEventSubscriber extends Subscriber {

    private Jedis subscriber;
    private String channelName = "DayCronize_ServerChangeEvent";

    public ServerChangeEventSubscriber() {
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
            // startServerPort | toServerPort | uuid | playerName
            String[] splitMessage = message.split("\\|");
            String startServerPort = splitMessage[0];
            String toServerPort = splitMessage[1];
            String uuid = splitMessage[2];
            String playerName = splitMessage[3];

            ServerChangeEvent event = new ServerChangeEvent(Integer.parseInt(startServerPort), Integer.parseInt(toServerPort), uuid, playerName);

            Bukkit.getScheduler().runTask(BukkitInitializer.getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));
        }
    }

}
