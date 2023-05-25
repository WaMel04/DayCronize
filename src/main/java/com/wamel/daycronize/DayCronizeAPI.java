package com.wamel.daycronize;

import com.wamel.daycronize.bukkit.BukkitInitializer;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DayCronizeAPI {

    public static ArrayList<String> playerList = new ArrayList<>();
    public static HashMap<Integer, String> serverNameMap = new HashMap<>();
    public static HashMap<String, String> playerServerNameMap = new HashMap<>();

    /**
     * 프록시에 접속한 모든 플레이어의 이름을
     * 리스트 형태로 반환하는 메소드입니다.
     *
     * @return 프록시에 접속한 모든 플레이어의 이름 목록 (ArrayList<String>)
     */
    public static ArrayList<String> getPlayerList() {
        return playerList;
    }

    /**
     * 서버의 포트로 이름을 구하는 메소드입니다.
     *
     * @param port 서버의 포트
     * @return 서버의 이름 (찾을 수 없을 경우, undefined 반환)
     */
    public static String getServerName(int port) {
        return serverNameMap.getOrDefault(port, "undefined");
    }

    /**
     * 서버의 이름으로 서버의 포트를 구하는 메소드입니다.
     *
     * @param serverName 서버의 영문명 (/server로 확인 가능)
     * @return 서버의 포트 (찾을 수 없을 경우, -1 반환)
     */
    public static int getServerPort(String serverName) {
        for (Map.Entry<Integer, String> entry : serverNameMap.entrySet()) {
            if (entry.getValue().equals(serverName)) {
                return entry.getKey();
            }
        }

        return -1;
    }

    /**
     * 플레이어의 이름으로 플레이어의 서버의 이름을 구하는 메소드입니다.
     *
     * @param playerName 플레이어의 이름
     * @return 서버의 이름 (찾을 수 없을 경우, undefined 반환)
     */
    public static String getPlayerServerName(String playerName) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();

            return playerServerNameMap.getOrDefault(uuid, "undefined");
        });
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "undefined";
    }

    /**
     * 플레이어의 UUID로 플레이어의 서버의 이름을 구하는 메소드입니다.
     *
     * @param playerUuid 플레이어의 UUID
     * @return 서버의 이름 (찾을 수 없을 경우, undefined 반환)
     */
    public static String getPlayerServerName(UUID playerUuid) {
        return playerServerNameMap.getOrDefault(playerUuid.toString(), "undefined");
    }

    /**
     * 특정 UUID를 가진 플레이어를 서버에 접속하게 만드는 메소드입니다.
     *
     * @param uuid 플레이어의 UUID
     * @param serverName 서버의 이름
     */
    public static void connect(String uuid, String serverName) {
        try (Jedis jedis = BukkitInitializer.getJedis()) {
            jedis.publish("DayCronize_ServerChangeRequestSubscriber", serverName + "|" + uuid);
        }
    }


}
