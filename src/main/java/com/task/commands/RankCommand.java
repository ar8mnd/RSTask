package com.task.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import com.task.form.CreateMenu;
import com.task.commands.base.BaseCommand;

/**
 * 任务排行榜主命令
 * @author SmallasWater
 */
public class RankCommand extends BaseCommand {
    public RankCommand(String name) {
        super(name,"Mission Points Leaderboard");
        this.setPermission("RSTask.command.rank");
        this.usageMessage = "/c-rank";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.isPlayer() && sender.hasPermission(getPermission());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isPlayer()) {
            CreateMenu.sendRankMenu((Player) commandSender);
        }else{
            commandSender.sendMessage("Please do not use the console to execute");
        }
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {}
}
