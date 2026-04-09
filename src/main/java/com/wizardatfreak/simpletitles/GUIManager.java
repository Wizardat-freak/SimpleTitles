package com.wizardatfreak.simpletitles;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUIManager {

    private final SimpleTitles plugin;
    private final NamespacedKey itemActionKey;

    public GUIManager(SimpleTitles plugin) {
        this.plugin = plugin;
        this.itemActionKey = new NamespacedKey(plugin, "gui_action");
    }

    public void openMainMenu(Player player) {
        String titleStr = plugin.getConfig().getString("main-menu.title", "&8Меню наборов титулов");
        int size = plugin.getConfig().getInt("main-menu.size", 27);
        
        Component inventoryTitle = Utils.parse(titleStr);
        Inventory inventory = Bukkit.createInventory(new TitleGUIHolder(), size, inventoryTitle);

        for (Map.Entry<String, FileConfiguration> entry : plugin.getConfigManager().getCategories().entrySet()) {
            String catId = entry.getKey();
            FileConfiguration catConf = entry.getValue();
            
            String reqPerm = catConf.getString("category.permission");
            boolean hasPerm = reqPerm == null || reqPerm.isEmpty() || player.hasPermission(reqPerm) || player.hasPermission("simpletitles.category.*");
            
            ItemStack item;
            if (hasPerm) {
                item = buildItem(catConf.getConfigurationSection("category.icon"), "category:" + catId);
            } else {
                item = buildItem(plugin.getConfig().getConfigurationSection("locked-item"), "locked");
            }
            
            if (item != null) {
                int slot = catConf.getInt("category.icon.slot", -1);
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, item);
                }
            }
        }
        
        ConfigurationSection clearSec = plugin.getConfig().getConfigurationSection("clear-item");
        if (clearSec != null) {
            ItemStack clearItem = buildItem(clearSec, "clear");
            int slot = clearSec.getInt("slot", size - 1);
            if (clearItem != null && slot >= 0 && slot < size) {
                inventory.setItem(slot, clearItem);
            }
        }

        fillEmptySlots(inventory);
        player.openInventory(inventory);
    }
    
    public void openCategoryMenu(Player player, String categoryId) {
        FileConfiguration catConf = plugin.getConfigManager().getCategory(categoryId);
        if (catConf == null) return;
        
        String titleStr = catConf.getString("category.menu.title", "&8" + categoryId);
        int size = catConf.getInt("category.menu.size", 27);
        
        Component inventoryTitle = Utils.parse(titleStr);
        Inventory inventory = Bukkit.createInventory(new TitleGUIHolder(), size, inventoryTitle);

        ConfigurationSection titlesSec = catConf.getConfigurationSection("titles");
        if (titlesSec != null) {
            for (String key : titlesSec.getKeys(false)) {
                String reqPerm = titlesSec.getString(key + ".permission");
                boolean hasPerm = reqPerm == null || reqPerm.isEmpty() || player.hasPermission(reqPerm) || player.hasPermission("simpletitles.title.*");
                
                ItemStack item;
                if (hasPerm) {
                    item = buildItem(titlesSec.getConfigurationSection(key + ".item"), "title:" + key);
                } else {
                    item = buildItem(plugin.getConfig().getConfigurationSection("locked-item"), "locked");
                }
                
                if (item != null) {
                    int slot = titlesSec.getInt(key + ".item.slot", -1);
                    if (slot >= 0 && slot < size) {
                        inventory.setItem(slot, item);
                    }
                }
            }
        }
        
        ConfigurationSection backSec = plugin.getConfig().getConfigurationSection("back-item");
        if (backSec != null) {
            ItemStack backItem = buildItem(backSec, "mainmenu");
            int slot = backSec.getInt("slot", size - 1);
            if (backItem != null && slot >= 0 && slot < size) {
                inventory.setItem(slot, backItem);
            }
        }

        fillEmptySlots(inventory);
        player.openInventory(inventory);
    }

    private void fillEmptySlots(Inventory inventory) {
        ConfigurationSection fillerSec = plugin.getConfig().getConfigurationSection("filler-item");
        if (fillerSec == null) return;
        
        ItemStack filler = buildItem(fillerSec, "filler_click");
        if (filler == null) return;
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack buildItem(ConfigurationSection section, String actionData) {
        if (section == null) return null;
        
        String matStr = section.getString("material", "DIRT");
        Material material = Material.matchMaterial(matStr);
        if (material == null) material = Material.DIRT;
        if (material == Material.AIR) return null;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (actionData != null) {
                meta.getPersistentDataContainer().set(itemActionKey, PersistentDataType.STRING, actionData);
            }
            
            String nameStr = section.getString("name");
            if (nameStr != null && !nameStr.trim().isEmpty()) {
                meta.displayName(Utils.parse(nameStr));
            } else {
                meta.displayName(Component.empty());
            }
            
            List<String> loreStr = section.getStringList("lore");
            if (loreStr != null && !loreStr.isEmpty()) {
                List<Component> lore = loreStr.stream()
                        .map(Utils::parse)
                        .collect(Collectors.toList());
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
