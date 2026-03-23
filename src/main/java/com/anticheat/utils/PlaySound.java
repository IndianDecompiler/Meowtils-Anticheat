package com.anticheat.utils;

import net.minecraft.client.Minecraft;

public class PlaySound {
    private static final PlaySound INSTANCE = new PlaySound();
    Minecraft mc = Minecraft.getMinecraft();

    private PlaySound() {
    }

    public static PlaySound getInstance() {
        return INSTANCE;
    }

    public void playPingSound() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("random.orb", 1.0F, 1.0F);
        }
    }

    public void playPingSoundDeep() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("random.orb", 1.0F, 0.2F);
        }
    }

    public void playPingSoundMedium() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("random.orb", 1.0F, 0.5F);
        }
    }

    public void playPingSoundLevel() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("random.levelup", 1.0F, 2.0F);
        }
    }

    public void playAnvilSound() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("random.anvil_land", 0.7F, 1.8F);
        }
    }

    public void playCritSound() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("meowtils:crit", 1.0F, 1.0F);
        }
    }

    public void playMeowSound() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("mob.cat.meow", 1.0F, 1.0F);
        }
    }

    public void playAnvilBreakSound() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.playSound("random.anvil_use", 1.0F, 1.0F);
        }
    }
}