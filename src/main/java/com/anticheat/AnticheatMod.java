/*
 * Author: tatp (Decompiled by Indian OP)
 * Original sourced from: Meowtils 
 * https://github.com/femboytatp/meowtils
 */
package com.anticheat;

import com.anticheat.command.CommandAnticheat;
import com.anticheat.core.AntiCheat;
import com.anticheat.utils.TeamUtil;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "anticheat", name = "Anticheat", version = "1.0", acceptedMinecraftVersions = "[1.8.9]")
public class AnticheatMod {

    public static boolean isEnabled = true;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new AntiCheat());
        MinecraftForge.EVENT_BUS.register(new TeamUtil());
        ClientCommandHandler.instance.registerCommand(new CommandAnticheat());
    }
}