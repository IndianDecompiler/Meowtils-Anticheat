/*
 * Author: tatp
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat.core;

import net.minecraft.entity.player.EntityPlayer;
import com.anticheat.core.checks.AutoBlockCheck;
import com.anticheat.core.checks.KillauraBCheck;
import com.anticheat.core.checks.LegitScaffoldCheck;
import com.anticheat.core.checks.NoSlowCheck;

public class AntiCheatData {
    public PlayerData playerData = new PlayerData();
    public AutoBlockCheck autoBlockCheck = new AutoBlockCheck();
    public NoSlowCheck noSlowCheck = new NoSlowCheck();
    public LegitScaffoldCheck legitScaffoldCheck = new LegitScaffoldCheck();
    public KillauraBCheck killauraBCheck = new KillauraBCheck();

    public void anticheatCheck(EntityPlayer player) {
        this.playerData.update(player);
        this.autoBlockCheck.anticheatCheck(player);
        this.noSlowCheck.anticheatCheck(player);
        this.legitScaffoldCheck.anticheatCheck(player);
        this.killauraBCheck.anticheatCheck(player);
    }

    public boolean failedAutoBlock() {
        return this.autoBlockCheck.failedAutoBlock();
    }

    public boolean failedNoSlow() {
        return this.noSlowCheck.failedNoSlow();
    }

    public boolean failedLegitScaffold() {
        return this.legitScaffoldCheck.failedLegitScaffold();
    }

    public boolean failedKillauraB() {
        return this.killauraBCheck.failedKillauraB();
    }
}