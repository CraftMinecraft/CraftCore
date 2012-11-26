package com.craftminecraft.craftsuite.events;

import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.craftminecraft.craftsuite.utils.json.JSONObject;

public class RecievedCallEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Map jsonmap;
    private boolean cancelled;         
    
    public RecievedCallEvent(Map apiCall) {
        jsonmap = apiCall;
        cancelled = false;
    }

    public Map getMap() {
        return jsonmap;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
