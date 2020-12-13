package com.gmail.playfuninetwork.event.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class TabEvent implements TabCompleter {
    private static final String[] ARGS1 = new String[] { "setspawn", "setrespawn", "mutechat", "changeflow", "staff", "freeze", "whitelist", "tp", "revive", "question" };

    private static final String[] ARGS_WHITELIST2 = new String[] { "removeall", "addall" };

    private static final String[] ARGS_TP2 = new String[] { "all", "live", "dead" };

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(ARGS1), completions);
        } else if (args.length == 2) {
            if (args[0].equals("staff") || args[0].equals("revive")) {
                if (args[0].equals("revive"))
                    completions.add("all");
                Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).filter(s -> s.toUpperCase().startsWith(args[args.length - 1].toUpperCase()))
                        .forEach(s -> completions.add(s));
                return completions;
            }
            StringUtil.copyPartialMatches(args[0], Arrays.asList(ARGS1), completions);
            if (completions.size() >= 1) {
                if (((String)completions.get(0)).equalsIgnoreCase("whitelist")) {
                    completions.clear();
                    StringUtil.copyPartialMatches(args[1], Arrays.asList(ARGS_WHITELIST2), completions);
                } else if (((String)completions.get(0)).equalsIgnoreCase("tp")) {
                    completions.clear();
                    StringUtil.copyPartialMatches(args[1], Arrays.asList(ARGS_TP2), completions);
                } else {
                    completions.clear();
                }
            } else {
                completions.clear();
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
