package com.wizardatfreak.simpletitles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component parse(String text) {
        if (text == null) return Component.empty();
        
        text = text.replace("&0", "<black>")
                   .replace("&1", "<dark_blue>")
                   .replace("&2", "<dark_green>")
                   .replace("&3", "<dark_aqua>")
                   .replace("&4", "<dark_red>")
                   .replace("&5", "<dark_purple>")
                   .replace("&6", "<gold>")
                   .replace("&7", "<gray>")
                   .replace("&8", "<dark_gray>")
                   .replace("&9", "<blue>")
                   .replace("&a", "<green>")
                   .replace("&b", "<aqua>")
                   .replace("&c", "<red>")
                   .replace("&d", "<light_purple>")
                   .replace("&e", "<yellow>")
                   .replace("&f", "<white>")
                   .replace("&k", "<obfuscated>")
                   .replace("&l", "<bold>")
                   .replace("&m", "<strikethrough>")
                   .replace("&n", "<underlined>")
                   .replace("&o", "<italic>")
                   .replace("&r", "<reset>");
        text = text.replaceAll("&#([a-fA-F0-9]{6})", "<#$1>");

        try {
            return MINI_MESSAGE.deserialize(text);
        } catch (Exception e) {
            return LEGACY.deserialize(text);
        }
    }
}
