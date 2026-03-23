/*
 * Author: tatp
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat.core.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import com.anticheat.config.Config;
import com.anticheat.utils.Utils;

public class NoSlowCheck {
    private int noSlowTicks = 0;
    private double lastPosX;
    private double lastPosZ;

    public void anticheatCheck(EntityPlayer player) {
        if (Config.detectNoSlow) {
            double deltaX = player.posX - this.lastPosX;
            double deltaZ = player.posZ - this.lastPosZ;
            double speed = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            
            if (player.isSprinting() && player.isUsingItem() && !player.isRiding()) {
                double baseThreshold = 0.05;
                PotionEffect speedEffect = player.getActivePotionEffect(Potion.moveSpeed);
                if (speedEffect != null) {
                    int amplifier = speedEffect.getAmplifier();
                    baseThreshold *= 1.0 + 0.2 * (amplifier + 1);
                }

                if (speed > baseThreshold) {
                    this.noSlowTicks++;
                    if (Config.debugMessages && this.noSlowTicks > 5) {
                        Utils.addMessage(
                                EnumChatFormatting.YELLOW
                                        + "[AntiCheat]: "
                                        + EnumChatFormatting.WHITE
                                        + player.getName()
                                        + " NoSlow ticks: "
                                        + this.noSlowTicks
                                        + " | Speed: "
                                        + speed
                                        + " | Threshold: "
                                        + baseThreshold
                        );
                    }
                } else {
                    this.noSlowTicks = 0;
                }
            } else {
                this.noSlowTicks = 0;
            }
            
            this.lastPosX = player.posX;
            this.lastPosZ = player.posZ;
        }
    }

    public boolean failedNoSlow() {
        return this.noSlowTicks > 20;
    }

    public void reset() {
        this.noSlowTicks = 0;
    }
}