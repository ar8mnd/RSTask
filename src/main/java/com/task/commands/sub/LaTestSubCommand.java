package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.task.RsTask;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.DataTool;
import com.task.form.CreateMenu;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;

import java.util.Date;
import java.util.LinkedList;

/**
 * @author SmallasWater
 */
public class LaTestSubCommand extends BaseSubCommand {

    public LaTestSubCommand(String name) {
        super(name);
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
                    int group = Integer.parseInt(list);
                    PlayerFile playerFile = PlayerFile.getPlayerFile(player.getName());
                    if (DataTool.existsGroup(group)) {
                        if (canOpenGroup(sender, playerName, player, group)) {
                            return true;
                        }
                        LinkedList<TaskFile> list1 = TaskFile.getDifficultyTasks(group);
                        TaskFile file = null;
                        Date date = null;
                        for (TaskFile file1 : list1) {
                            if (playerFile.isRunning(file1.getTaskName())) {
                                if (date == null) {
                                    date = playerFile.getTaskByName(file1.getTaskName()).getTaskClass().getTime();
                                    file = file1;
                                } else {
                                    if (playerFile.getTaskByName(file1.getTaskName()).getTaskClass().getTime().getTime() > date.getTime()) {
                                        file = file1;
                                    }
                                }
                            }
                        }
                        if (file != null) {
                            RsTask.getTask().getClickTask.put(player, file);
                            CreateMenu.sendTaskMenu(player, RsTask.getTask().getClickTask.get(player));
                            return true;

                        } else {
                            sender.sendMessage("§6[§7Task System§6] Player: " + player.getName() + " did not receive a task in group " + group);
                        }
                    } else {
                        sender.sendMessage("§6[§7Task System§6] Group: " + group + " does not exist");
                        return true;
                    }

                } catch (Exception e) {
                    sender.sendMessage("§6[§7Task System§6] Please enter a valid group (integer)");
                    return true;
                }

            } else {
                sender.sendMessage("§6[§7Task System§6] §cPlayer " + playerName + " is not online");
            }
        } else {
            return false;
        }
        return true;
    }

    static boolean canOpenGroup(CommandSender sender, String playerName, Player player, int group) {
        if (RsTask.canOpen()) {
            int starCount = DataTool.starNeed(group);
            PlayerFile pf = PlayerFile.getPlayerFile(player.getName());
            if (pf.getCount() < starCount) {
                sender.sendMessage(TextFormat.RED + "Player " + playerName + " does not have enough points to open group " + group);

                return true;
            }
        }
        RsTask.getClickStar.put(player, group);
        return false;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
