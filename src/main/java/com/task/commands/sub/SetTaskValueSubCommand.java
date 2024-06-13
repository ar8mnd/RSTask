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
public class SetTaskValueSubCommand extends BaseSubCommand {

    public SetTaskValueSubCommand(String name) {
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
                if (API.setPlayerRunTask(p.getName(), v.getTaskName(), v.getLoad(), v.getValue())) {
                    if (args.length > 5 && "true".equalsIgnoreCase(args[5])) {
                        sender.sendMessage("§6[§7Task System§6] §2Successfully set §7" + p.getName() + "§2's §5" + v.getValue() + "§r points of §2" + v.getTaskName() + "§2 progress");
                    }
                } else {
                    if (args.length > 5 && "true".equalsIgnoreCase(args[5])) {
                        sender.sendMessage("§6[§7Task System§6] §7" + p.getName() + "§c failed to set the progress of " + v.getTaskName() + " to " + v.getLoad());
                    }
                }
            } else {
                return false;
            }
        } else {
            if (args.length > 5 && "true".equalsIgnoreCase(args[5])) {
                sender.sendMessage("Player " + player + " is not online");

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
