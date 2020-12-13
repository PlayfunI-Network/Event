package com.gmail.playfuninetwork.event.events;

import me.clip.placeholderapi.PlaceholderAPI;
import com.gmail.playfuninetwork.event.Main;
import com.gmail.playfuninetwork.event.question.Question;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EventsPlayerManager implements Listener {
    Main main = Main.getInstance();

    EventsLifeCycle lifeCycle = this.main.lifeCycle;

    public boolean chatMuted = false;

    public boolean frozen = false;

    public boolean flow = true;

    private BukkitTask delay;

    Question question;

    public EventsPlayerManager() {
        this.lifeCycle.playerManager = this;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (this.chatMuted && !e.getPlayer().hasPermission("event.staff")) {
            if (this.question != null) {
                e.getPlayer().sendMessage("§7[§cEvent§7] : §e"+ (this.question.getAnswer().equalsIgnoreCase(e.getMessage()) ? "§a§lGreat!You got it right!" : "§cWrong! Try again."));
            } else {
                e.getPlayer().sendMessage("§7[§cEvent§7] : §c§l聊天室目前關閉!");
            }
            e.setCancelled(true);
        }
        e.setMessage(PlaceholderAPI.setPlaceholders(e.getPlayer(), e.getMessage()));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (this.frozen && !this.lifeCycle.isStaff(e.getPlayer())) {
            Location newLocation = e.getFrom();
            e.getFrom().setPitch(e.getTo().getPitch());
            e.getFrom().setYaw(e.getTo().getYaw());
            e.setTo(newLocation);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (this.frozen && !this.lifeCycle.isStaff(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onLiquidChange(BlockFromToEvent e) {
        if (!this.flow && (e.getBlock().getType() == Material.LAVA || e
                .getBlock().getType() == Material.STATIONARY_LAVA || e
                .getBlock().getType() == Material.WATER || e
                .getBlock().getType() == Material.STATIONARY_WATER))
            e.setCancelled(true);
    }

    public void muteChat(CommandSender by) {
        this.chatMuted = !this.chatMuted;
        Bukkit.broadcastMessage("§7[§cEvent§7] : §e聊天室已被 " + (this.chatMuted ? "muted" : "unmuted") + " by §c" + by

                .getName());
    }

    public void freezeForTime(int seconds, final String name) {
        if (this.delay != null)
            this.delay.cancel();
        this.frozen = true;
        Bukkit.broadcastMessage("§7[§cEvent§7] :§a" + name + " §e暫停了伺服器. 請稍待片刻!");
        Bukkit.broadcastMessage("§7[§cEvent§7] :§a" + name + " §e伺服器暫停將停止於 §a"+ seconds + "§e 秒.");
        this

                .delay = (new BukkitRunnable() {
            public void run() {
                EventsPlayerManager.this.frozen = false;
                Bukkit.broadcastMessage("§7[§cEvent§7] : §a" + name + " §e解除了伺服器暫停!");
            }
        }).runTaskLater((Plugin)this.main, (seconds * 20));
    }

    public void toggleFreeze(String name) {
        this.frozen = !this.frozen;
        if (this.frozen) {
            Bukkit.broadcastMessage("§7[§cEvent§7] :§a" + name + " §e暫停了伺服器. 請稍待片刻!");
        } else {
            Bukkit.broadcastMessage("§7[§cEvent§7] :§a" + name + " §e解除了伺服器暫停!");
        }
    }

    public void changeFlow(CommandSender by) {
        this.flow = !this.flow;
        by.sendMessage("§7[§cEvent§7] : §e" + (this.flow ? "" : "§c") + "流體已被 " + (this.flow ? "continued" : "stopped") + ".");
    }

    public void startNewQuestion(Question question) {
        if (this.question != null)
            this.question.finish();
        this.question = question;
        this.question.start();
    }

    public void finishQuestion() {
        this.question = null;
    }
}
