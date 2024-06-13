package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.API;
import com.task.utils.DataTool;
import com.task.utils.RunValue;

/**
 * @author SmallasWater
 */
public class AddTaskValueSubCommand extends BaseSubCommand {

    public AddTaskValueSubCommand(String name) {
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
        String player = args[1];
        Player p = Server.getInstance().getPlayer(player);
        if (p != null) {
            if (args.length > 4) {
                RunValue v = RunValue.getInstance(args);
                if (v == null) {
                    return false;
                }
                if (API.addPlayerRunTask(p.getName(), v.getTaskName(), v.getLoad(), v.getValue())) {
                    if (args.length > 5 && "true".equalsIgnoreCase(args[5])) {
                        sender.sendMessage("§6[§7Task System§6] §2Successfully added §7" + p.getName() + "§2 §5" + v.getValue() + "§2 points of §r" + v.getTaskName() + "§2 progress");
                    }
                } else {
                    if (args.length > 5 && "true".equalsIgnoreCase(args[5])) {
                        sender.sendMessage("§6[§7Task System§6] §7" + p.getName() + "§c failed to add §r" + v.getTaskName() + " progress of §5" + v.getLoad() + "§c");
                    }
                }
            } else {
                return false;
            }
        } else {
            if (args.length > 5 && "true".equalsIgnoreCase(args[5])) {
                sender.sendMessage("§6[§7Task System§6] §cPlayer " + player + " is not online");
            }

        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                CommandParameter.newType("playerName", false, CommandParamType.TARGET),
                CommandParameter.newEnum("taskName", DataTool.getTaskAllNames()),
                CommandParameter.newType("load", false, CommandParamType.TEXT),
                CommandParameter.newType("value", false, CommandParamType.INT),
        };
    }
}
