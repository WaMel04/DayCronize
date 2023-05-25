package com.wamel.daycronize;

import com.wamel.daycronize.bukkit.BukkitInitializer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Set;

public class DayCronizeAPI {

    public static ArrayList<String> getPlayerList() {
        Jedis jedis = BukkitInitializer.getJedis();

        Set<String> players = jedis.smembers("DayCronize.PlayerList");
        ArrayList<String> list = new ArrayList<>();

        if (players == null)
            return list;

        for (String name : players) {
            list.add(name);
        }

        return list;
    }

}
