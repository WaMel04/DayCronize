package com.wamel.daycronize.event;

import com.wamel.daycronize.bukkit.BukkitInitializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProxyConnectEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Integer toServerPort;
    private String uuid;
    private String playerName;

    public ProxyConnectEvent(Integer toServerPort, String uuid, String playerName) {
        this.toServerPort = toServerPort;
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public Integer getToServerPort() {
        return toServerPort;
    }

    public String getUuid() {
        return uuid;
    }

    public CompletableFuture<Player> getPlayerFuture() {
        CompletableFuture<Player> future = new CompletableFuture<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(UUID.fromString(uuid));

                if (player != null) {
                    future.complete(player);
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(BukkitInitializer.getInstance(), 0, 10);

        return future;
    }

    public boolean getIsToServer() {
        if (Bukkit.getPort() == toServerPort)
            return true;
        else
            return false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
