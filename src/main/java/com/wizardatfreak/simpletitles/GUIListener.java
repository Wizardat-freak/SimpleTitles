package com.wizardatfreak.simpletitles;


import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    private final SimpleTitles plugin;
    private final NamespacedKey itemActionKey;

    public GUIListener(SimpleTitles plugin) {
        this.plugin = plugin;
        this.itemActionKey = new NamespacedKey(plugin, "gui_action");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getHolder() == null) return;
        
        if (event.getInventory().getHolder() instanceof TitleGUIHolder) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            
            ItemMeta meta = clicked.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            
            if (data.has(itemActionKey, PersistentDataType.STRING)) {
                String action = data.get(itemActionKey, PersistentDataType.STRING);
                if (action == null) return;

                if (action.equals("clear")) {
                    plugin.getTitleManager().clearTitle(player);
                    String msg = plugin.getConfig().getString("messages.title-cleared", "&aВаш титул успешно сброшен.");
                    player.sendMessage(Utils.parse(msg));
                    player.closeInventory();
                } else if (action.equals("mainmenu")) {
                    plugin.getGuiManager().openMainMenu(player);
                } else if (action.startsWith("category:")) {
                    String categoryId = action.substring("category:".length());
                    plugin.getGuiManager().openCategoryMenu(player, categoryId);
                } else if (action.startsWith("title:")) {
                    String titleId = action.substring("title:".length());
                    plugin.getTitleManager().setTitle(player, titleId);
                    
                    String titleName = titleId;
                    for (FileConfiguration catConf : plugin.getConfigManager().getCategories().values()) {
                        if (catConf.contains("titles." + titleId + ".suffix")) {
                            titleName = catConf.getString("titles." + titleId + ".item.name");
                            if (titleName == null || titleName.trim().isEmpty()) {
                                titleName = catConf.getString("titles." + titleId + ".suffix", titleId);
                                titleName = titleName.trim(); // remove leading space
                            }
                            break;
                        }
                    }
                    
                    String msg = plugin.getConfig().getString("messages.title-equipped", "&aВы успешно установили титул %title%&a!");
                    msg = msg.replace("%title%", titleName);
                    
                    player.sendMessage(Utils.parse(msg));
                    player.closeInventory();
                } else if (action.equals("filler_click")) {
                    boolean enabled = plugin.getConfig().getBoolean("filler-item.action-enabled", false);
                    if (enabled) {
                        String msg = plugin.getConfig().getString("filler-item.action-message", "");
                        if (!msg.isEmpty()) {
                            player.sendMessage(Utils.parse(msg));
                        }
                    }
                }
            }
        }
    }
}
