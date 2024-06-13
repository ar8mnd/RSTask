package com.task.commands;

import cn.nukkit.command.CommandSender;
import com.task.commands.base.BaseCommand;
import com.task.commands.sub.BookGiveSubCommand;
import com.task.commands.sub.BookUpSubCommand;


/**
 * 书本主命令
 * @author SmallasWater
 */
public class BookCommand extends BaseCommand {
    public BookCommand(String name) {
        super(name, "§aDirective to obtain a mission statement");
        this.addSubCommand(new BookUpSubCommand("up"));
        this.addSubCommand(new BookGiveSubCommand("give"));
        this.loadCommandBase();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(args.length > 0) {
           super.execute(sender, s, args);
        }else{
            sendHelp(sender);
        }

        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.isPlayer();
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§c=======================");
        sender.sendMessage("§e/cbook give <task name> Get a task letter for this task");
        sender.sendMessage("§e/cbook up Update the mission statement in hand");
        sender.sendMessage("§c=======================");
    }
}
