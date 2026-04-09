package com.wizardatfreak.simpletitles;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TitleManager implements Listener {

    private final SimpleTitles plugin;
    private final NamespacedKey titleKey;
    private final Scoreboard scoreboard;
    public TitleManager(SimpleTitles plugin) {
        this.plugin = plugin;
        this.titleKey = new NamespacedKey(plugin, "active_title");
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public void clearTitle(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.remove(titleKey);
        removePlayerFromTeam(player);
    }

    public void setTitle(Player player, String titleId) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(titleKey, PersistentDataType.STRING, titleId);
        applyTitle(player, titleId);
    }

    public String getActiveTitle(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.get(titleKey, PersistentDataType.STRING);
    }

    public void applyTitle(Player player, String titleId) {
        String suffix = null;
        
        for (FileConfiguration catConfig : plugin.getConfigManager().getCategories().values()) {
            if (catConfig.contains("titles." + titleId + ".suffix")) {
                suffix = catConfig.getString("titles." + titleId + ".suffix");
                break;
            }
        }
        
        if (suffix == null) {
            removePlayerFromTeam(player);
            return;
        }

        String teamName = "st_" + titleId;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        
        Component componentSuffix = Utils.parse(suffix);
        team.suffix(componentSuffix);
        team.addEntry(player.getName());
    }

    private void removePlayerFromTeam(Player player) {
        Team team = scoreboard.getEntryTeam(player.getName());
        if (team != null && team.getName().startsWith("st_")) {
            team.removeEntry(player.getName());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String activeTitle = getActiveTitle(player);
        if (activeTitle != null) {
            applyTitle(player, activeTitle);
        }
    }
}
