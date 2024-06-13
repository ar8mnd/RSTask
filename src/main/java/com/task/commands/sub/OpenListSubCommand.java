package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.task.RsTask;
import com.task.utils.DataTool;
import com.task.form.CreateMenu;


/**
 * 打开分组界面子指令
 *
 * @author SmallasWater
 */
public class OpenListSubCommand extends OpenTaskSubCommand {

    public OpenListSubCommand(String name) {
        super(name);
    }

    @Override
    protected boolean canUse(CommandSender sender) {
        return sender.isOp();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length > 2) {
            String playerName = args[1];
            Player player = Server.getInstance().getPlayer(playerName);
            if (player != null) {
                String list = args[2];
                try {
                    int group = Integer.parseInt(list);
                    if (DataTool.existsGroup(group)) {
                        if (LaTestSubCommand.canOpenGroup(sender, playerName, player, group)) {
                            return true;
                        }
                        CreateMenu.sendTaskList(player, RsTask.getClickStar.get(player));
                    } else {

                        sender.sendMessage(TextFormat.RED + "Group does not exist: " + group);
                        return true;
                    }

                } catch (Exception e) {
                    sender.sendMessage(TextFormat.RED + "Please enter a valid group (integer)");
                    return true;
                }

                sender.sendMessage(TextFormat.GREEN + "Successfully triggered click group " + list + " for player " + player.getName());
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
                CommandParameter.newEnum("group", DataTool.getGropAllName())
        };
    }
}
