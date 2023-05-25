package com.wamel.daycronize.bukkit.subscriber;

import redis.clients.jedis.JedisPubSub;

public abstract class Subscriber extends JedisPubSub {

    public abstract void start();
    public abstract void stop();
    
}
