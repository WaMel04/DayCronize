package com.wamel.daycronize;

import com.wamel.daycronize.bukkit.BukkitInitializer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;

public class DayCronizeAPI {

    public static ArrayList<String> getPlayerList() {
        try (Jedis jedis = BukkitInitializer.getJedis()) {
            ArrayList<String> list = new ArrayList<>();

            for (String name : jedis.smembers("DayCronize.PlayerList")) {
                list.add(name);
            }

            return list;
        }
    }
}
