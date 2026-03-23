/*
 * Author: tatp
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat.core.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import com.anticheat.config.Config;
import com.anticheat.utils.Utils;

public class AutoBlockCheck {
    private int autoBlockTicks = 0;

    public void anticheatCheck(EntityPlayer player) {
        if (Config.detectAutoBlock) {
            if (player.isSwingInProgress && player.isBlocking()) {
                this.autoBlockTicks++;
                if (Config.debugMessages && this.autoBlockTicks > 5) {
                    Utils.addMessage(
                            EnumChatFormatting.YELLOW + "[AntiCheat]: " + EnumChatFormatting.WHITE + player.getName() + " AutoBlock ticks: " + this.autoBlockTicks
                    );
                }
            } else {
                this.autoBlockTicks = 0;
            }
        }
    }

    public boolean failedAutoBlock() {
        return this.autoBlockTicks > 10;
    }

    public void reset() {
        this.autoBlockTicks = 0;
    }
}