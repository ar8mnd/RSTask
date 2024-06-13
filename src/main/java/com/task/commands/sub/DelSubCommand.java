package com.task.commands.sub;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import com.task.events.DelTaskEvent;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.DataTool;
import com.task.utils.tasks.TaskFile;

/**
 * 删除玩家任务指令
 *
 * @author SmallasWater
 */
public class DelSubCommand extends BaseSubCommand {

    public DelSubCommand(String name) {
        super(name);
    }

    @Override
    protected boolean canUse(CommandSender sender) {
        return sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /task del <task name>");
            return false;
        }
        String taskName = args[1];
        if (TaskFile.isFileTask(taskName)) {
            TaskFile file = TaskFile.getTask(taskName);
            if (file != null) {
                DelTaskEvent event = new DelTaskEvent(file);
                Server.getInstance().getPluginManager().callEvent(event);
                if (!file.close()) {
                    sender.sendMessage("Task deletion failed");
                } else {
                    sender.sendMessage("Task deleted successfully");
                }
            }
        } else {
            sender.sendMessage("Task " + taskName + " does not exist");
            return false;
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                CommandParameter.newEnum("taskName", DataTool.getTaskAllNames())};
    }
}
