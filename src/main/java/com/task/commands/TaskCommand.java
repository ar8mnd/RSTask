package com.task.commands;

import cn.nukkit.command.CommandSender;
import com.task.commands.base.BaseCommand;
import com.task.commands.sub.*;


/**
 * 任务主命令 （管理）
 *
 * @author SmallasWater
 */
public class TaskCommand extends BaseCommand {
    public TaskCommand(String name) {
        super(name, "Task system master command");
        this.setPermission("RSTask.command.ic");
        this.addSubCommand(new IcSubCommand("ic"));
        this.addSubCommand(new DelSubCommand("del"));
        this.addSubCommand(new ReloadSubCommand("reload"));
        this.addSubCommand(new OpenTaskSubCommand("c"));
        this.addSubCommand(new OpenListSubCommand("clist"));
        this.addSubCommand(new CountSubCommand("count"));
        this.addSubCommand(new LaTestSubCommand("latest"));
        this.loadCommandBase();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length > 0) {
            return super.execute(sender, s, args);
        }
        sendHelp(sender);
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(getPermission());
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§6[§7Task System§6] §e->§2 /task ic §7Create a task command");
        sender.sendMessage("§6[§7Task System§6] §e->§2 /task del <name> §7Delete the specified task");
        sender.sendMessage("§6[§7Task System§6] §e->§2 /task reload §7Reload the configuration");
        sender.sendMessage("§6[§7Task System§6] §e->§2 /task c <player> <task name> §7Let the player open the task");
        sender.sendMessage("§6[§7Task System§6] §e->§2 /task clist <player> <group> §7Open the specified group GUI");
        sender.sendMessage("§6[§7Task System§6] §e->§2 /task latest <player> <group> §7Open the latest task received by the player in this group");
        sender.sendMessage("§6[§7Task System§6] §e->§2 /task count <player> <points> §7Set the player's task points");
    }
}
