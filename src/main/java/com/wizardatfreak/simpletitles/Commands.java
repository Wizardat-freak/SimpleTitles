package com.wizardatfreak.simpletitles;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    private final SimpleTitles plugin;

    public Commands(SimpleTitles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("simpletitles.command.reload")) {
                String error = plugin.getConfig().getString("messages.no-permission", "&cУ вас нет прав на использование этой команды!");
                sender.sendMessage(Utils.parse(error));
                return true;
            }
            
            plugin.getConfigManager().load();
            
            // Reapply titles for all online players with new config
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                String activeTitle = plugin.getTitleManager().getActiveTitle(player);
                if (activeTitle != null) {
                    plugin.getTitleManager().applyTitle(player, activeTitle);
                }
            }
            
            String msg = plugin.getConfig().getString("messages.reload", "&aКонфигурация плагина обновлена!");
            sender.sendMessage(Utils.parse(msg));
            return true;
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки могут открывать меню титулов.");
            return true;
        }

        if (!player.hasPermission("simpletitles.command.menu")) {
            String error = plugin.getConfig().getString("messages.no-permission", "&cУ вас нет прав на использование этой команды!");
            player.sendMessage(Utils.parse(error));
            return true;
        }
        
        plugin.getGuiManager().openMainMenu(player);
        return true;
    }
}
