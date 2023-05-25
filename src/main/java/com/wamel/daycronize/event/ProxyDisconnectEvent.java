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

public class ProxyDisconnectEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Integer leaveServerPort;
    private String uuid;

    public ProxyDisconnectEvent(Integer leaveServerPort, String uuid) {
        this.leaveServerPort = leaveServerPort;
        this.uuid = uuid;
    }

    public Integer getLeaveServerPort() {
        return leaveServerPort;
    }

    public String getUuid() {
        return uuid;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
