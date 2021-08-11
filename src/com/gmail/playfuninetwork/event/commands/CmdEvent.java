package com.gmail.playfuninetwork.event.commands;

import com.gmail.playfuninetwork.event.events.EventsLifeCycle;
import com.gmail.playfuninetwork.event.events.EventsPlayerManager;
import com.gmail.playfuninetwork.event.question.Question;
import com.gmail.playfuninetwork.event.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CmdEvent implements CommandExecutor {
    Main main = Main.getInstance();

    EventsLifeCycle lifeCycle = this.main.lifeCycle;

    EventsPlayerManager playerManager = this.main.playerManager;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("event.staff")) {
            sender.sendMessage("§c權限不足!");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equals("setspawn")) {
                this.lifeCycle.spawn = ((Player)sender).getLocation();
                sender.sendMessage(Main.PREFIX + "§c§出生點 §e設置成功.");
            } else if (args[0].equals("setrespawn")) {
                this.lifeCycle.respawn = ((Player)sender).getLocation();
                sender.sendMessage(Main.PREFIX + "§c§重生點 §e設置成功.");
            } else if (args[0].equals("mutechat")) {
                this.playerManager.muteChat(sender);
            } else if (args[0].equals("staff")) {
                Player p = (Player)sender;
                if (this.lifeCycle.setStaff(p)) {
                    p.sendMessage(Main.PREFIX + "§e你已進入 §c管理員模式§e.");
                } else {
                    p.sendMessage(Main.PREFIX + "§e退出 §c管理員模式§e.");
                }
            } else if (args[0].equals("freeze")) {
                this.playerManager.toggleFreeze(sender.getName());
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equals("whitelist")) {
                if (args[1].equals("addall")) {
                    Bukkit.getOnlinePlayers().forEach(p -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "whitelist add " + p.getName()));
                    sender.sendMessage(Main.PREFIX + "§e成功將所有玩家新增至白名單.");
                } else if (args[1].equals("removeall")) {
                    Bukkit.getWhitelistedPlayers().forEach(p -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "whitelist remove " + p.getName()));
                    sender.sendMessage(Main.PREFIX + "§e成功清空所有白名單.");
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("tp")) {
                if (args[1].equals("all")) {
                    Bukkit.getOnlinePlayers().forEach(p -> p.teleport((Entity)sender));
                    sender.sendMessage(Main.PREFIX + "§e你已傳送 所有玩家.");
                } else if (args[1].equals("alive")) {
                    this.lifeCycle.alive.forEach(p -> p.teleport((Entity)sender));
                    sender.sendMessage(Main.PREFIX + "§e你傳送了 §c存活玩家 §e到你身邊.");
                } else if (args[1].equals("dead")) {
                    this.lifeCycle.dead.forEach(p -> p.teleport((Entity)sender));
                    sender.sendMessage(Main.PREFIX + "§e你傳送了 §c淘汰玩家 §e到你身邊.");
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("revive")) {
                if (args[1].equals("all")) {
                    if (this.lifeCycle.respawnAll()) {
                        Bukkit.broadcastMessage(Main.PREFIX + "§a所有玩家 §e都被復活了! by §c"+ sender.getName());
                    } else {
                        sender.sendMessage(Main.PREFIX + "§c請設置一個重生點!");
                    }
                } else {
                    Player p = Bukkit.getPlayer(args[1]);
                    if (p == null) {
                        sender.sendMessage(Main.PREFIX + "§c找不到此玩家.");
                        return true;
                    }
                    if (this.lifeCycle.respawn(p)) {
                        Bukkit.broadcastMessage(Main.PREFIX + "§a"+ p.getName() + "§e已經復活 by §c"+ sender.getName());
                    } else {
                        sender.sendMessage(Main.PREFIX + "§c玩家仍然存活 或 重生點未被設置!");
                    }
                }
            } else if (args[0].equals("staff")) {
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    sender.sendMessage(Main.PREFIX + "§c找不到此玩家.");
                    return true;
                }
                if (this.lifeCycle.setStaff(p)) {
                    p.sendMessage(Main.PREFIX + "§e你已進入 §c管理員模式.");
                } else {
                    sender.sendMessage("7[§cEvent§7] : §c已將該玩家退出管理員模式!");
                    p.sendMessage(Main.PREFIX + "§c你退出了 §7管理員 §模式.");
                }
            } else if (args[0].equals("freeze")) {
                Integer sec;
                try {
                    sec = Integer.valueOf(Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(Main.PREFIX + "§c請輸入數字.");
                    return true;
                }
                this.playerManager.freezeForTime(sec.intValue(), sender.getName());
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 3) {
            if (args[0].equals("question")) {
                this.playerManager.startNewQuestion(new Question(args[0], args[1], 1, sender));
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 4) {
            if (args[0].equals("question")) {
                Integer max;
                try {
                    max = Integer.valueOf(Integer.parseInt(args[3]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(Main.PREFIX + "§c請輸入數字.");
                    return true;
                }
                this.playerManager.startNewQuestion(new Question(args[1], args[2], max.intValue(), sender));
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(Main.PREFIX + "§e/event setspawn");
        sender.sendMessage(Main.PREFIX + "§e/event setrespawn");
        sender.sendMessage(Main.PREFIX + "§e/event mutechat");
        sender.sendMessage(Main.PREFIX + "§e/event staff [<player>]");
        sender.sendMessage(Main.PREFIX + "§e/event freeze [<second(s)>] ");
        sender.sendMessage(Main.PREFIX + "§e/event whitelist <removeall/addall>");
        sender.sendMessage(Main.PREFIX + "§e/event tp <all/live/dead>");
        sender.sendMessage(Main.PREFIX + "§e/event revive <all/player>");
        sender.sendMessage(Main.PREFIX + "§e/event question <question> <answer> [<maxRight>]");
        sender.sendMessage("");
    }
}
