/*
 * Author: tatp
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private static final Map<String, String> blacklistMap = new HashMap<>();

    public static void addMessage(String text) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(text));
        }
    }

    public static String getTabDisplayName(String playerName) {
        return playerName;
    }

    public static String getPrefix() {
        return EnumChatFormatting.DARK_GRAY + "[" + EnumChatFormatting.RED + "AntiCheat" + EnumChatFormatting.DARK_GRAY + "] " + EnumChatFormatting.RESET;
    }

    public static boolean isBlacklisted(String key) {
        return blacklistMap.containsKey(key);
    }

    public static String getEntry(String key) {
        return blacklistMap.get(key);
    }

    public static void addToBlacklist(String key, String reasons) {
        blacklistMap.put(key, reasons);
    }

    public static void removeFromBlacklist(String key) {
        blacklistMap.remove(key);
    }

    public static boolean isReplay() {
        return false;
    }

}