package com.gmail.playfuninetwork.event.events;

import com.gmail.playfuninetwork.event.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AlivePapiExtension extends PlaceholderExpansion {
    EventsLifeCycle lifeCycle;

    public AlivePapiExtension(EventsLifeCycle e) {
        this.lifeCycle = e;
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    public String getIdentifier() {
        return "event";
    }

    public String getAuthor() {
        return "PlayfunI Network";
    }

    public String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player p, String identifier) {
        if (identifier.equals("alive")) {
            long alive = this.lifeCycle.alive.stream().filter(OfflinePlayer::isOnline).count();
            return String.valueOf(alive);
        }
        if (identifier.equals("dead")) {
            long dead = this.lifeCycle.dead.stream().filter(OfflinePlayer::isOnline).count();
            return String.valueOf(dead);
        }
        if (identifier.equals("staff")) {
            long staff = this.lifeCycle.staff.stream().filter(OfflinePlayer::isOnline).count();
            return String.valueOf(staff);
        }
        return "WRONG IDENTIFIER";
    }
}