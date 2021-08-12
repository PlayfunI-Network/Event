package com.gmail.playfuninetwork.event.question;

import com.gmail.playfuninetwork.event.Main;
import com.gmail.playfuninetwork.event.events.EventsPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Question implements Listener {
    Main main = Main.getInstance();

    EventsPlayerManager playerManager = this.main.playerManager;

    String question;

    String answer;

    ArrayList<String> guessed = new ArrayList<>();

    int maxright;

    CommandSender initializer;

    public Question(String question, String answer, int maxright, CommandSender init) {
        this.question = question.replace("_", " ");
        this.answer = answer.replace("_", " ");
        this.maxright = maxright;
        this.initializer = init;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().equalsIgnoreCase(this.answer) && !this.guessed.contains(e.getPlayer().getName())) {
            this.guessed.add(e.getPlayer().getName());
            if (this.guessed.size() >= this.maxright) {
                finish();
                e.setCancelled(true);
            }
        } else if (this.guessed.contains(e.getPlayer().getName())) {
            e.getPlayer().sendMessage(Main.PREFIX + "§c請等待問題結束!");
            e.setCancelled(true);
        }
    }

    public void start() {
        if (this.playerManager.chatMuted)
            this.playerManager.muteChat(this.initializer);
        Bukkit.getPluginManager().registerEvents(this, (Plugin) this.main);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(Main.PREFIX + "§a" + this.initializer.getName() + "§7 : §e" + this.question);
        Bukkit.broadcastMessage("");
    }

    public void finish() {
        if (this.guessed.size() == 0) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(Main.PREFIX + "§c問題已被強制終止!");
            Bukkit.broadcastMessage("");
            AsyncPlayerChatEvent.getHandlerList().unregister(this);
            this.playerManager.muteChat(this.initializer);
            return;
        }
        String players = "";
        for (String p : this.guessed)
            players = players + p + ", ";
        players = players.substring(0, players.length() - 2);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(Main.PREFIX + "§e" + ((this.guessed.size() > 1) ? "玩家 §a" : "玩家 §a") + players + " §e猜對了! 答案是: §7" + this.answer + "§e.");
        Bukkit.broadcastMessage("");
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        this.playerManager.muteChat(this.initializer);
        this.playerManager.finishQuestion();
    }

    public String getAnswer() {
        return this.answer;
    }
}
