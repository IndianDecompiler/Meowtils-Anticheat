/*
 * Author: tatp
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat.core.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import com.anticheat.config.Config;

public class LegitScaffoldCheck {
    private static final Map<UUID, Long> lastCrouchStart = new HashMap<>();
    private static final Map<UUID, Long> lastCrouchEnd = new HashMap<>();
    private static final Map<UUID, Boolean> wasSneaking = new HashMap<>();
    private static final Map<UUID, Long> lastSwingTick = new HashMap<>();
    private static final Map<UUID, List<Integer>> crouchDurations = new HashMap<>();
    private static final Map<UUID, Long> lastFlagTick = new HashMap<>();
    private final long cooldownTicks = 60L;
    private boolean flagged = false;

    public void anticheatCheck(EntityPlayer player) {
        if (Config.detectLegitScaffold && player != null && player != Minecraft.getMinecraft().thePlayer) {
            UUID uuid = player.getUniqueID();
            long tick = player.ticksExisted;
            boolean currSneak = player.isSneaking();
            boolean prevSneak = wasSneaking.getOrDefault(uuid, false);
            
            if (currSneak && !prevSneak) {
                lastCrouchStart.put(uuid, tick);
            } else if (!currSneak && prevSneak) {
                lastCrouchEnd.put(uuid, tick);
                long start = lastCrouchStart.getOrDefault(uuid, tick - 1L);
                int duration = (int) (tick - start);
                crouchDurations.computeIfAbsent(uuid, k -> new ArrayList<>()).add(0, duration);
                if (crouchDurations.get(uuid).size() > 5) {
                    crouchDurations.get(uuid).remove(5);
                }
            }

            wasSneaking.put(uuid, currSneak);
            
            if (player.isSwingInProgress && player.swingProgress != player.prevSwingProgress) {
                lastSwingTick.put(uuid, tick);
            }

            if (player.rotationPitch >= 60.0F
                    && player.getHeldItem() != null
                    && player.getHeldItem().getItem() instanceof ItemBlock
                    && player.onGround) {
                
                long end = lastCrouchEnd.getOrDefault(uuid, 0L);
                long swing = lastSwingTick.getOrDefault(uuid, Long.MIN_VALUE);
                int crouchDuration = (int) (end - lastCrouchStart.getOrDefault(uuid, end - 1L));
                boolean quickCrouch = crouchDuration >= 1 && crouchDuration <= 2;
                boolean swingTiming = swing >= end && swing <= end + 1L;
                List<Integer> durations = crouchDurations.getOrDefault(uuid, Collections.emptyList());
                boolean consistent = durations.size() >= 3 && durations.get(0) <= 2 && durations.get(1) <= 2 && durations.get(2) <= 2;
                
                if (quickCrouch && swingTiming && consistent) {
                    long lastFlag = lastFlagTick.getOrDefault(uuid, 0L);
                    if (tick - lastFlag >= 60L) {
                        this.flagged = true;
                        lastFlagTick.put(uuid, tick);
                    } else {
                        this.flagged = false;
                    }
                } else {
                    this.flagged = false;
                }
            } else {
                this.flagged = false;
            }
        }
    }

    public boolean failedLegitScaffold() {
        return this.flagged;
    }

    public void reset() {
        this.flagged = false;
    }

    public static void clear() {
        lastCrouchStart.clear();
        lastCrouchEnd.clear();
        wasSneaking.clear();
        lastSwingTick.clear();
        crouchDurations.clear();
        lastFlagTick.clear();
    }
}