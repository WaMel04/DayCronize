package com.wamel.daycronize.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProxyDisconnectEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Integer leaveServerPort;
    private String uuid;
    private String playerName;

    public ProxyDisconnectEvent(Integer leaveServerPort, String uuid, String playerName) {
        this.leaveServerPort = leaveServerPort;
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public Integer getLeaveServerPort() {
        return leaveServerPort;
    }

    public boolean getIsLeaveServer() {
        if (Bukkit.getPort() == leaveServerPort)
            return true;
        else
            return false;
    }

    public String getUuid() {
        return uuid;
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
