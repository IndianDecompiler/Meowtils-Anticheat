package com.anticheat.command;

import com.anticheat.AnticheatMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandAnticheat extends CommandBase {
    @Override
    public String getCommandName() {
        return "anticheat";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/anticheat <true/false>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("true")) {
                AnticheatMod.isEnabled = true;
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Anticheat enabled."));
            } else if (args[0].equalsIgnoreCase("false")) {
                AnticheatMod.isEnabled = false;
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Anticheat disabled."));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
            }
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
        }
    }
}