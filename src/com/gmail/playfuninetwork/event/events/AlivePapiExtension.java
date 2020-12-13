package com.gmail.playfuninetwork.event.events;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class AlivePapiExtension extends PlaceholderExpansion {
    EventsLifeCycle lifeCycle;

    public AlivePapiExtension(EventsLifeCycle e) {
        this.lifeCycle = e;
    }

    public String getIdentifier() {
        return "event";
    }

    public String getAuthor() {
        return "PlayfunI Network";
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean register() {
        return PlaceholderAPI.registerPlaceholderHook(getIdentifier(), (PlaceholderHook)this);
    }

    public String onPlaceholderRequest(Player p, String identifier) {
        if (identifier.equals("alive")) {
            long alive = this.lifeCycle.alive.stream().filter(pl -> pl.isOnline()).count();
            return String.valueOf(alive);
        }
        if (identifier.equals("dead")) {
            long dead = this.lifeCycle.dead.stream().filter(pl -> pl.isOnline()).count();
            return String.valueOf(dead);
        }
        if (identifier.equals("staff")) {
            long staff = this.lifeCycle.staff.stream().filter(pl -> pl.isOnline()).count();
            return String.valueOf(staff);
        }
        return "WRONG IDENTIFIER";
    }
}
