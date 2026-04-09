package com.wizardatfreak.simpletitles;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {

    private final SimpleTitles plugin;
    private final Map<String, FileConfiguration> categories = new HashMap<>();

    public ConfigManager(SimpleTitles plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        categories.clear();
        
        File dir = new File(plugin.getDataFolder(), "categories");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String[] defaultCategories = {"myths.yml", "legends.yml", "games.yml", "admin.yml"};
        for (String cat : defaultCategories) {
            File catFile = new File(dir, cat);
            if (!catFile.exists() && plugin.getResource("categories/" + cat) != null) {
                plugin.saveResource("categories/" + cat, false);
            }
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File f : files) {
                String id = f.getName().replace(".yml", "");
                try {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(f);
                    categories.put(id, config);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load category: " + f.getName(), e);
                }
            }
        }
        plugin.getLogger().info("Loaded " + categories.size() + " categories!");
    }

    public Map<String, FileConfiguration> getCategories() {
        return categories;
    }

    public FileConfiguration getCategory(String id) {
        return categories.get(id);
    }
}
