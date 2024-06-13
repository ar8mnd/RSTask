package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.tasks.PlayerFile;

/**
 * 设置玩家积分子指令
 *
 * @author SmallasWater
 */
public class CountSubCommand extends BaseSubCommand {

    public CountSubCommand(String name) {
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
        if (args.length > 2) {
            String playerName = args[1];
            Player player = Server.getInstance().getPlayer(playerName);
            if (player != null) {
                String list = args[2];
                try {
                    int count = Integer.parseInt(list);
                    PlayerFile file = PlayerFile.getPlayerFile(player.getName());
                    file.setCount(count);
                } catch (Exception e) {
                    sender.sendMessage(TextFormat.RED + "Please enter a valid score (integer)");
                    return true;
                }
                sender.sendMessage(TextFormat.GREEN + "Successfully set player " + player.getName() + "'s score to " + list);
            } else {
                sender.sendMessage(TextFormat.RED + "Player " + playerName + " is not online");
            }
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                CommandParameter.newType("playerName", false, CommandParamType.TARGET),
                CommandParameter.newType("count", false, CommandParamType.INT)
        };
    }
}
