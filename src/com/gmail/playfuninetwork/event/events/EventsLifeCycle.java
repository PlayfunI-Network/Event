package com.gmail.playfuninetwork.event.events;

import java.util.ArrayList;
import java.util.Optional;
import com.gmail.playfuninetwork.event.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventsLifeCycle implements Listener {
    Main main = Main.getInstance();

    public EventsPlayerManager playerManager;

    public Location spawn;

    public Location respawn;

    public ArrayList<Player> alive = new ArrayList<>();

    public ArrayList<Player> dead = new ArrayList<>();

    public ArrayList<Player> staff = new ArrayList<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore() &&
                this.spawn != null)
            e.getPlayer().teleport(this.spawn);
        if (this.alive.stream().noneMatch(p -> p.getName().equals(e.getPlayer().getName())) && this.dead.stream().noneMatch(p -> p.getName().equals(e.getPlayer().getName())) && this.staff.stream().noneMatch(p -> p.getName().equals(e.getPlayer().getName())))
            this.alive.add(e.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (this.playerManager.frozen) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Player && isStaff((Player)e.getEntity()) && ((Player)e.getEntity()).getHealth() <= e.getDamage()) {
            e.setCancelled(true);
            ((Player)e.getEntity()).sendMessage("§ 7[§cEvent§7] : §c管理員模式. 你將不會死亡");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!isStaff(e.getEntity()) && !isDead(e.getEntity())) {
            removeAlive(e.getEntity());
            this.dead.add(e.getEntity());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (this.spawn != null) {
            e.setRespawnLocation(this.spawn);
        } else if (this.respawn != null) {
            e.setRespawnLocation(this.respawn);
        }
        e.getPlayer().sendMessage("§c§l你被淘汰了! 不過仍有復活機會喔..");
    }

    public boolean respawn(Player p) {
        if (isDead(p)) {
            if (this.respawn != null) {
                p.teleport(this.respawn);
            } else if (this.spawn != null) {
                p.teleport(this.spawn);
            }
            removeDead(p);
            this.alive.add(p);
            return true;
        }
        return false;
    }

    public boolean respawnAll() {
        if (this.spawn != null) {
            if (this.respawn != null) {
                this.dead.forEach(p -> p.teleport(this.respawn));
            } else if (this.spawn != null) {
                this.dead.forEach(p -> p.teleport(this.spawn));
            }
            for (Player p : this.dead) {
                this.alive.add(p);
                p.sendMessage("§a§l你被復活了!");
            }
            this.dead.clear();
            return true;
        }
        return false;
    }

    public boolean setStaff(Player p) {
        if (isStaff(p)) {
            removeStaff(p);
            this.alive.add(p);
            return false;
        }
        if (isAlive(p)) {
            removeAlive(p);
            this.staff.add(p);
            return true;
        }
        if (isDead(p)) {
            removeAlive(p);
            this.staff.add(p);
        } else {
            this.staff.add(p);
        }
        return true;
    }

    public boolean isStaff(Player p) {
        return this.staff.stream().anyMatch(pl -> p.getName().equals(pl.getName()));
    }

    public boolean isDead(Player p) {
        return this.dead.stream().anyMatch(pl -> p.getName().equals(pl.getName()));
    }

    public boolean isAlive(Player p) {
        return this.alive.stream().anyMatch(pl -> p.getName().equals(pl.getName()));
    }

    public void removeStaff(Player p) {
        Optional<Player> opt = this.staff.stream().filter(pl -> p.getName().equals(pl.getName())).findFirst();
        if (opt.isPresent())
            this.staff.remove(opt.get());
    }

    public void removeDead(Player p) {
        Optional<Player> opt = this.dead.stream().filter(pl -> p.getName().equals(pl.getName())).findFirst();
        if (opt.isPresent())
            this.dead.remove(opt.get());
    }

    public void removeAlive(Player p) {
        Optional<Player> opt = this.alive.stream().filter(pl -> p.getName().equals(pl.getName())).findFirst();
        if (opt.isPresent())
            this.alive.remove(opt.get());
    }
}
