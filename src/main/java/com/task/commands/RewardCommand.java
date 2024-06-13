package com.task.commands;

import cn.nukkit.command.CommandSender;
import com.task.commands.base.BaseCommand;

public class RewardCommand extends BaseCommand {
    public RewardCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("RSTask.command.reward");
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§6[§7Task System§6] §e->§f /rd c §7Create a bounty task");
        sender.sendMessage("§6[§7Task System§6] §e->§f /rd del <name> §7Delete the specified task");
        sender.sendMessage("§6[§7Task System§6] §e->§f /rd reload §7Reload configuration");
    }
}
