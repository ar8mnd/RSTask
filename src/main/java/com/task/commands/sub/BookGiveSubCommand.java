package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.ItemWrittenBook;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.DataTool;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.TaskBook;

/**
 * 给予玩家书本子指令
 *
 * @author SmallasWater
 */
public class BookGiveSubCommand extends BaseSubCommand {

    public BookGiveSubCommand(String name) {
        super(name);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length > 1) {
            String taskName = args[1];
            TaskFile file = TaskFile.getTask(taskName);
            if (file == null) {
                sender.sendMessage("§cTask " + args[1] + " does not exist");
                return true;
            }
            PlayerFile file1 = PlayerFile.getPlayerFile(sender.getName());
            if (file1.canInvite(file.getTaskName())) {
                ItemWrittenBook written = new ItemWrittenBook();
                TaskBook book = new TaskBook(written);

                book.setTitle(file.getTaskName());
                book.setCustomName(file.getName());
                book.writeIn("\n\n\n\nLoading... Please open it again");
                ((Player) sender).getInventory().setItemInHand(book.toBook().clone());
            } else {
                sender.sendMessage("§cTask " + args[1] + " cannot be received");
                return true;
            }

        } else {
            sender.sendMessage("§c Please fill in the task name");

        }
        return true;
    }

    @Override
    protected boolean canUse(CommandSender sender) {
        return sender.isOp();
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                CommandParameter.newEnum("taskName", DataTool.getTaskAllNames())
        };
    }
}
