package com.wizardatfreak.simpletitles;

import org.bukkit.plugin.java.JavaPlugin;

public class SimpleTitles extends JavaPlugin {
    
    private ConfigManager configManager;
    private TitleManager titleManager;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.configManager.load();
        
        this.titleManager = new TitleManager(this);
        this.guiManager = new GUIManager(this);
        
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(this.titleManager, this);
        
        if (getCommand("titles") != null) {
            getCommand("titles").setExecutor(new Commands(this));
        }
        
        getLogger().info("SimpleTitles enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SimpleTitles disabled!");
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public TitleManager getTitleManager() {
        return titleManager;
    }
    
    public GUIManager getGuiManager() {
        return guiManager;
    }
}
