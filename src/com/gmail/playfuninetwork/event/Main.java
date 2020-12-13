package com.gmail.playfuninetwork.event;

import com.gmail.playfuninetwork.event.events.AlivePapiExtension;
import com.gmail.playfuninetwork.event.events.EventsLifeCycle;
import com.gmail.playfuninetwork.event.events.EventsPlayerManager;
import com.gmail.playfuninetwork.event.commands.CmdEvent;
import com.gmail.playfuninetwork.event.commands.TabEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static final String PREFIX = "§7[§cEvent§7] : §e";

    public static Main instance;

    public EventsLifeCycle lifeCycle;

    public EventsPlayerManager playerManager;

    public com.gmail.playfuninetwork.event.commands.CmdEvent cmd_event;

    public void onEnable() {
        instance = this;
        this.lifeCycle = new EventsLifeCycle();
        this.playerManager = new EventsPlayerManager();
        this.cmd_event = new CmdEvent();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new AlivePapiExtension(this.lifeCycle)).register();
        Bukkit.getPluginManager().registerEvents((Listener)this.lifeCycle, (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)this.playerManager, (Plugin)this);
        Bukkit.getPluginCommand("event").setExecutor((CommandExecutor)this.cmd_event);
        Bukkit.getPluginCommand("event").setTabCompleter((TabCompleter)new TabEvent());
    }

    public static Main getInstance() {
        return instance;
    }
}
