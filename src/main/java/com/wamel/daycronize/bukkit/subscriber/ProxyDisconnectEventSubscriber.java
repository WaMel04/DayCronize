package com.wamel.daycronize.bukkit.subscriber;


import com.wamel.daycronize.DayCronizeAPI;
import com.wamel.daycronize.bukkit.BukkitInitializer;
import com.wamel.daycronize.bukkit.RedisConfig;
import com.wamel.daycronize.event.ProxyDisconnectEvent;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

public class ProxyDisconnectEventSubscriber extends Subscriber {

    private Jedis subscriber;
    private String channelName = "DayCronize_ProxyDisconnectEvent";

    public ProxyDisconnectEventSubscriber() {
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
            // leaveServerPort | uuid | playerName
            String[] splitMessage = message.split("\\|");
            String leaveServerPort = splitMessage[0];
            String uuid = splitMessage[1];
            String playerName = splitMessage[2];

            ProxyDisconnectEvent event = new ProxyDisconnectEvent(Integer.parseInt(leaveServerPort), uuid, playerName);
            Bukkit.getScheduler().runTask(BukkitInitializer.getInstance(), (Runnable) -> Bukkit.getPluginManager().callEvent(event));

            if (DayCronizeAPI.playerList.contains(playerName))
                DayCronizeAPI.playerList.remove(playerName);

            DayCronizeAPI.playerServerNameMap.remove(uuid);
        }
    }

}
