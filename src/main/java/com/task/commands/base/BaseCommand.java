package com.task.commands.base;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.data.CommandParamType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 指令工具类
 *
 * @author SmallasWater
 */
abstract public class BaseCommand extends Command {

    private ArrayList<BaseSubCommand> subCommand = new ArrayList<>();

    private final ConcurrentHashMap<String, Integer> subCommands = new ConcurrentHashMap<>();

    public BaseCommand(String name, String description) {
        super(name, description);
    }


    /**
     * 获取权限
     *
     * @param sender 玩家
     * @return 是否拥有权限
     */
    abstract public boolean hasPermission(CommandSender sender);


    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (hasPermission(sender)) {
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();
                if (subCommands.containsKey(subCommand)) {
                    BaseSubCommand command = this.subCommand.get(subCommands.get(subCommand));
                    boolean canUse = command.canUse(sender);
                    if (canUse) {
                        if (!command.execute(sender, s, args)) {
                            sendHelp(sender);
                            return true;
                        }
                    } else if (sender instanceof Player) {
                        return true;
                    } else {
                        sender.sendMessage("Do not execute this command on the console");
                    }
                } else {
                    sendHelp(sender);
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * 发送帮助
     *
     * @param sender 玩家
     */
    abstract public void sendHelp(CommandSender sender);

    protected void addSubCommand(BaseSubCommand cmd) {
        subCommand.add(cmd);
        int commandId = (subCommand.size()) - 1;
        subCommands.put(cmd.getName().toLowerCase(), commandId);
        for (String alias : cmd.getAliases()) {
            subCommands.put(alias.toLowerCase(), commandId);
        }
    }

    protected void loadCommandBase() {
        this.commandParameters.clear();
        for (BaseSubCommand subCommand : subCommand) {
            LinkedList<CommandParameter> parameters = new LinkedList<>();
            parameters.add(
                    CommandParameter.newType(
                            subCommand.getName(), CommandParamType.STRING
                    )
            );

            parameters.addAll(Arrays.asList(subCommand.getParameters()));
            this.commandParameters.put(subCommand.getName(), parameters.toArray(new CommandParameter[0]));
        }
    }
}
