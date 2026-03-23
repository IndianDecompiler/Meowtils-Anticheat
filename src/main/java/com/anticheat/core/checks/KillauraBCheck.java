/*
 * Author: tatp
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat.core.checks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import com.anticheat.config.Config;
import com.anticheat.utils.Utils;

public class KillauraBCheck {
    Minecraft mc = Minecraft.getMinecraft();
    private final Map<UUID, Integer> useItemTicks = new HashMap<>();
    private final Map<UUID, Integer> lastEatTicks = new HashMap<>();
    private final Map<UUID, Integer> violationLevels = new HashMap<>();
    private static final int EAT_TIMEOUT = 33;
    private static final int MIN_USE_TIME = 6;
    private boolean failedKillauraB = false;

    public void anticheatCheck(EntityPlayer player) {
        if (Config.detectKillaura) {
            if (player != this.mc.thePlayer && player.ridingEntity == null) {
                UUID uuid = player.getUniqueID();
                long tick = this.mc.theWorld.getTotalWorldTime();
                ItemStack heldItem = player.getHeldItem();
                boolean isUsingItem = player.isUsingItem();
                boolean isConsumable = heldItem != null && this.isConsumable(heldItem.getItem());
                boolean isAttacking = player.swingProgressInt > 0;
                int useTime = this.useItemTicks.getOrDefault(uuid, 0);
                
                if (isUsingItem && isConsumable) {
                    this.useItemTicks.put(uuid, ++useTime);
                } else {
                    if (useTime > 0) {
                        this.lastEatTicks.put(uuid, (int) tick);
                    }
                    this.useItemTicks.put(uuid, 0);
                }

                int lastEatTick = this.lastEatTicks.getOrDefault(uuid, 0);
                int sinceLastEat = (int) (tick - lastEatTick);
                
                if (isAttacking && useTime > 6 && sinceLastEat < 33 && isConsumable) {
                    int vl = this.violationLevels.getOrDefault(uuid, 0) + 1;
                    this.violationLevels.put(uuid, vl);
                    if (Config.debugMessages) {
                        Utils.addMessage(
                                EnumChatFormatting.YELLOW
                                        + "[AntiCheat]: "
                                        + EnumChatFormatting.WHITE
                                        + player.getName()
                                        + " swinging while using item | Use Time="
                                        + useTime
                                        + " | Last Ate="
                                        + sinceLastEat
                                        + " | vl="
                                        + vl
                                        + " | Item="
                                        + (heldItem != null ? heldItem.getItem().getRegistryName() : "none")
                        );
                    }

                    if (vl >= 8) {
                        this.failedKillauraB = true;
                    }
                } else {
                    int vlx = this.violationLevels.getOrDefault(uuid, 0);
                    if (vlx > 0) {
                        this.violationLevels.put(uuid, vlx - 1);
                    }
                }
            }
        }
    }

    public boolean failedKillauraB() {
        return this.failedKillauraB;
    }

    public void reset() {
        this.failedKillauraB = false;
        this.useItemTicks.clear();
        this.lastEatTicks.clear();
        this.violationLevels.clear();
    }

    private boolean isConsumable(Item item) {
        return item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemBucketMilk;
    }
}