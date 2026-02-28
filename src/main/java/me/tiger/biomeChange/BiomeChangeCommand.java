package me.tiger.biomeChange;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BiomeChangeCommand implements CommandExecutor {

    private final BiomeChange plugin;

    public BiomeChangeCommand(BiomeChange plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /biomechange enable | disable");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "enable" -> {
                plugin.enableFor(player);
                player.sendMessage(ChatColor.GREEN + "BiomeChange ENABLED");
                player.sendTitle(ChatColor.AQUA + "BiomeChange",
                        ChatColor.WHITE + "Enabled",
                        10, 40, 10);
            }
            case "disable" -> {
                plugin.disableFor(player);
                player.sendMessage(ChatColor.RED + "BiomeChange DISABLED");
                player.sendTitle(ChatColor.RED + "BiomeChange",
                        ChatColor.WHITE + "Disabled",
                        10, 40, 10);
            }
            default -> player.sendMessage(ChatColor.YELLOW + "Usage: /biomechange enable | disable");
        }

        return true;
    }
}