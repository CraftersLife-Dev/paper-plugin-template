package com.github.crafterslife.dev.papertemplate.paper.event.events;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ExampleEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Component message;

    public ExampleEvent(Component message) {
        this.message = message;
    }

    public Component getMessage() {
        return this.message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
