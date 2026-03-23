/*
 * Author: tatp
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import com.anticheat.AnticheatMod;
import com.anticheat.config.Config;
import com.anticheat.utils.ColorUtil;
import com.anticheat.utils.PlaySound;
import com.anticheat.utils.TeamUtil;
import com.anticheat.utils.Utils;

public class AntiCheat {
    private static final Map<String, AntiCheatData> antiCheatDataMap = new HashMap<>();
    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private static final Map<String, Map<String, Integer>> violationLevels = new HashMap<>();
    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!AnticheatMod.isEnabled) return;
        
        if (event.phase == Phase.END && this.mc.theWorld != null && this.mc.thePlayer != null) {
            for (EntityPlayer player : this.mc.theWorld.playerEntities) {
                if (player != this.mc.thePlayer) {
                    PlayerData data = playerDataMap.computeIfAbsent(player.getUniqueID(), k -> new PlayerData());
                    data.update(player);
                }
            }

            for (EntityPlayer playerx : this.mc.theWorld.playerEntities) {
                if (playerx != this.mc.thePlayer && playerx.getName() != null && !TeamUtil.ignoreTeam(playerx.getName())) {
                    AntiCheatData data = antiCheatDataMap.computeIfAbsent(playerx.getName(), k -> new AntiCheatData());
                    data.anticheatCheck(playerx);
                    String playerName = playerx.getName();
                    
                    if (data.failedAutoBlock() && this.incrementViolation(playerName, "AutoBlock")) {
                        this.sendFlagMessage(playerName, "AutoBlock");
                        data.autoBlockCheck.reset();
                        this.tryAutoBlacklist(playerName, "autoblock");
                    }

                    if (data.failedNoSlow() && this.incrementViolation(playerName, "NoSlow")) {
                        this.sendFlagMessage(playerName, "NoSlow");
                        data.noSlowCheck.reset();
                        this.tryAutoBlacklist(playerName, "noslow");
                    }

                    if (data.failedLegitScaffold() && this.incrementViolation(playerName, "Legit Scaffold")) {
                        this.sendFlagMessage(playerName, "Legit Scaffold");
                        data.legitScaffoldCheck.reset();
                        this.tryAutoBlacklist(playerName, "legit scaffold");
                    }

                    if (data.failedKillauraB() && this.incrementViolation(playerName, "KillAura")) {
                        this.sendFlagMessage(playerName, "Killaura");
                        data.killauraBCheck.reset();
                        this.tryAutoBlacklist(playerName, "killaura");
                    }
                }
            }
        }
    }

    private boolean incrementViolation(String playerName, String checkType) {
        Map<String, Integer> playerViolations = violationLevels.computeIfAbsent(playerName, k -> new HashMap<>());
        int newLevel = playerViolations.getOrDefault(checkType, 0) + 1;
        playerViolations.put(checkType, newLevel);
        if (newLevel >= Config.violationLevel) {
            playerViolations.put(checkType, 0);
            return true;
        } else {
            return false;
        }
    }

    private void tryAutoBlacklist(String playerName, String reason) {
        if (Config.antiCheatAutoBlacklist) {
            if (!Utils.isReplay()) {
                for (NetworkPlayerInfo netInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    if (netInfo.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                        String uuid = netInfo.getGameProfile().getId().toString();
                        this.handleBlacklist(uuid, playerName, reason);
                        return;
                    }
                }
                this.handleBlacklist(null, playerName, reason);
            }
        }
    }

    private void handleBlacklist(String uuid, String name, String newReason) {
        String key = uuid != null ? uuid : name;
        boolean alreadyBlacklisted = Utils.isBlacklisted(key);
        String existingEntry = Utils.getEntry(key);
        String combinedReasons;
        if (alreadyBlacklisted && existingEntry != null) {
            String[] parts = existingEntry.split(" ", 2);
            String oldReasons = parts.length > 1 ? parts[1] : "";
            Set<String> reasonSet = new LinkedHashSet<>(Arrays.asList(oldReasons.split(" \\| ")));
            reasonSet.add(newReason);
            combinedReasons = String.join(" | ", reasonSet);
            Utils.removeFromBlacklist(key);
        } else {
            combinedReasons = newReason;
        }
        Utils.addToBlacklist(key, combinedReasons);
    }

    private void sendFlagMessage(String playerName, String checkType) {
        String msg = Utils.getTabDisplayName(playerName)
                + EnumChatFormatting.GRAY
                + " failed "
                + ColorUtil.getColorFromString(Config.flagMessageComponentColor)
                + checkType;
                
        if (Config.flagWDRButton) {
            ChatComponentText message = new ChatComponentText(Utils.getPrefix() + msg + " ");
            ChatComponentText wdrButton = new ChatComponentText(
                    ColorUtil.getColorFromString(Config.flagMessageBracketColor)
                            + "["
                            + ColorUtil.getColorFromString(Config.flagMessageButtonColor)
                            + "WDR"
                            + ColorUtil.getColorFromString(Config.flagMessageBracketColor)
                            + "]"
            );
            wdrButton.setChatStyle(
                    new ChatStyle()
                            .setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wdr " + playerName))
                            .setChatHoverEvent(
                                    new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Click to report this player.")
                                    )
                            )
            );
            message.appendSibling(wdrButton);
            this.mc.thePlayer.addChatMessage(message);
        } else {
            Utils.addMessage(msg);
        }

        if (Config.flagPingSound) {
            PlaySound.getInstance().playPingSound();
        }
        
    }

    public static void clear() {
        antiCheatDataMap.clear();
        playerDataMap.clear();
        violationLevels.clear();
    }
}