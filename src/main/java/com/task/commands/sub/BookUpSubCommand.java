package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWrittenBook;
import com.task.RsTask;
import com.task.commands.base.BaseSubCommand;
import com.task.events.PlayerOpenBookEvent;
import com.task.utils.task.CollectItemTask;
import com.task.utils.tasks.taskitems.TaskBook;


/**
 * 书本更新子指令
 *
 * @author SmallasWater
 */
public class BookUpSubCommand extends BaseSubCommand {

    public BookUpSubCommand(String name) {
        super(name);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"update"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Item item = ((Player) sender).getInventory().getItemInHand();
        if (item instanceof ItemWrittenBook) {
            if (TaskBook.isBook((ItemWrittenBook) item)) {
                RsTask.executor.submit(new CollectItemTask(RsTask.getTask(), (Player) sender));
                PlayerOpenBookEvent event = new PlayerOpenBookEvent(
                        (Player) sender, TaskBook.getTaskBookByItem(((ItemWrittenBook) item)));
                Server.getInstance().getPluginManager().callEvent(event);
                sender.sendMessage("§a==============");
                sender.sendMessage("§eMission statement updated successfully");
                sender.sendMessage("§a==============");
                return true;
            }
        }
        sender.sendMessage("§cPlease hold the mission statement in your hand");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
