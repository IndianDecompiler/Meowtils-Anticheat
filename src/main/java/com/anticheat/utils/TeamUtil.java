package com.anticheat.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import com.anticheat.config.Config;

public class TeamUtil {
    private static boolean spectator = false;
    private long spectatorTime = 0L;

    public static boolean inSpectator() {
        return spectator;
    }

    public static boolean ignoreSelf(String playerName) {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && playerName != null && playerName.equalsIgnoreCase(mc.thePlayer.getName());
    }

    public static boolean ignoreTeam(String playerName) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer target = (EntityPlayer) mc.theWorld.playerEntities.stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst()
                .orElse(null);
        if (mc.thePlayer == null || mc.theWorld == null || playerName == null) {
            return false;
        } else if (!Config.ignoreTeam) {
            return false;
        } else if (target == null) {
            return false;
        } else if (target.capabilities.isCreativeMode) {
            return true;
        } else {
            try {
                if (mc.thePlayer.isOnSameTeam(target)) {
                    return true;
                }

                String selfFormatted = mc.thePlayer.getDisplayName().getFormattedText();
                String targetFormatted = target.getDisplayName().getFormattedText();
                String selfColor = getMostFrequentColor(selfFormatted);
                String targetColor = getMostFrequentColor(targetFormatted);
                
                if (selfColor != null && selfColor.equals(targetColor)) {
                    return true;
                }
            } catch (Exception var7) {
            }

            return false;
        }
    }

    private static String getMostFrequentColor(String text) {
        if (text == null) {
            return null;
        } else {
            Map<String, Integer> colorCounts = new HashMap<>();

            for (int i = 0; i < text.length() - 1; i++) {
                char c = text.charAt(i);
                if (c == 167) {
                    char code = text.charAt(i + 1);
                    if (code >= '0' && code <= '9' || code >= 'a' && code <= 'f') {
                        String currentColor = "§" + code;
                        colorCounts.put(currentColor, colorCounts.getOrDefault(currentColor, 0) + 1);
                    }
                }
            }

            return colorCounts.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.theWorld != null && event.phase == Phase.END) {
            long now = System.currentTimeMillis();
            if (mc.thePlayer.capabilities.isCreativeMode) {
                spectator = true;
                this.spectatorTime = now;
            } else {
                spectator = now - this.spectatorTime < 8000L;
            }
        }
    }
}