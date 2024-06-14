package com.task.utils.task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWrittenBook;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import com.task.RsTask;
import com.task.utils.API;
import com.task.events.*;
import com.task.form.CreateMenu;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;
import com.task.utils.DataTool;

import com.task.utils.tasks.taskitems.*;

import java.util.LinkedList;

/**
 * @author SmallasWater
 */
public class ListerEvents implements Listener {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        String s = block.getId() + ":" + block.toItem().getDamage() + "@item";
        defaultUseTask(player.getName(), s, TaskFile.TaskType.BlockBreak, false);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        String s = block.getId() + ":" + block.toItem().getDamage() + "@item";
        defaultUseTask(player.getName(), s, TaskFile.TaskType.BlockPlayer, false);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onUseItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        API.addItem(player, item, TaskFile.TaskType.EatItem);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        API.addItem(player, item, TaskFile.TaskType.DropItem);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void craft(CraftItemEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        Item item = event.getRecipe().getResults().getFirst();
        API.addItem(player, item, TaskFile.TaskType.CraftItem);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRightBook(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        if (item instanceof ItemWrittenBook) {
            if (TaskBook.isBook((ItemWrittenBook) item)) {
                TaskBook book = TaskBook.getTaskBookByItem((ItemWrittenBook) item);
                String taskName = book.title;
                PlayerFile file = PlayerFile.getPlayerFile(player.getName());
                if (file.isSuccess(taskName)) {
                    TaskFile file1 = TaskFile.getTask(taskName);
                    if (file1 != null) {
                        event.setCancelled();
                        PlayerClickTaskEvent event1 = new PlayerClickTaskEvent(file1, player);
                        Server.getInstance().getPluginManager().callEvent(event1);
                    }
                }

            }
        }
        RsTask.executor.execute(() -> API.addItem(player, item, TaskFile.TaskType.Click));


    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!RsTask.getTask().getPlayerFile(player.getName()).exists()) {
            Server.getInstance().broadcastMessage(RsTask.getTask().getLag("join-achievement").replace("%p", player.getName()));
            RsTask.getTask().getPlayerConfig(player.getName());
        }
    }

    @EventHandler
    public void use(UseTaskEvent event) {
        Player player = event.getPlayer();
        PlayerTask task = event.getTaskItem();
        PlayerFile file = PlayerFile.getPlayerFile(player.getName());
        Level level = player.getLevel();
        level.addSound(player.getPosition(), Sound.TILE_PISTON_OUT);
        PlayerTask newTask = file.getTaskByName(task.getTaskName());
        if (RsTask.showLoading) {
            if (!file.isSuccess(newTask.getTaskName())) {
                String send = RsTask.getTask().getLag("run-task").
                        replace("%s", task.getTaskFile().getName()).replace("%c", String.format("%.2f", RsTask.getTask().
                                getTaskLoading(task.getTaskName(), player.getName()))).replace("\\n", "\n");
                DataTool.sendMessage(player, send);
            } else {
                if (!RsTask.taskNames.contains(task.getTaskName())) {
                    level.addSound(player.getPosition(), Sound.BLOCK_COMPOSTER_READY);
                    String send = RsTask.getTask().getLag("success-message")
                            .replace("%d", task.getTaskFile()
                                    .getStar() + "").replace("%s", task.getTaskFile().getName()).replace("\\n", "\n");
                    DataTool.sendMessage(player, send);
                }
            }
        }
        if (RsTask.canSuccess) {
            if (file.isSuccess(task.getTaskFile())) {
                PlayerFile.givePlayerSuccessItems(player, task.getTaskName());
            }
        }

    }

    @EventHandler
    public void onTimeOut(TaskTimeOutEvent event) {
        String playerName = event.getPlayer().getPlayerName();
        Player player = Server.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(RsTask.getTask().getLag("task-time-out", "§d§l[Task system]§c Your task %s timeout.").
                    replace("%s", event.getFile().getName()));

        }

    }

    @EventHandler
    public void onGiveUp(PlayerGiveUpTaskEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        PlayerFile file2 = PlayerFile.getPlayerFile(player.getName());
        if (!file2.closeTask(file.getTaskName())) {
            Server.getInstance().getLogger().warning("player " + player.getName() + " abolish " + file.getTaskName() + " mission anomaly");
            return;
        }
        player.sendMessage(RsTask.getTask().getLag("giveUpTaskMessage", "§d§l[Quest system]§b You abandoned the %s quest.")
                .replace("%s", file.getName()));
        RsTask.getTask().getClickTask.remove(player);

    }


    @EventHandler
    public void onReceive(PlayerClickTaskEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        if (player.getGamemode() == 1) {
            player.sendMessage(RsTask.getTask().getLag("CreateUI", "§d§l[Quest system]§c Creation mode can't wake up UI"));
            return;
        }
        TaskFile file = event.getFile();

        if (TaskFile.runTaskFile(player, file)) {
            addBook(player, file);
            RsTask.getTask().getClickTask.put(player, file);
            RsTask.getClickStar.put(player, file.getGroup());
            if (event.isShow()) {
                CreateMenu.sendTaskMenu(player, file);
            }
        }
    }

    private void addBook(Player player, TaskFile file) {
        if (RsTask.canGiveBook) {
            if (!TaskBook.canInventory(player, file.getTaskName())) {
                ItemWrittenBook written = new ItemWrittenBook();
                TaskBook book = new TaskBook(written);
                book.setTitle(file.getTaskName());
                book.setCustomName(file.getName());
                book.writeIn("\n\n\n\nLoading... Please open again");
                ItemWrittenBook written1 = book.toBook();
                player.getInventory().addItem(written1.clone());
            }
        }
    }

    @EventHandler
    public void useItem(PlayerOpenBookEvent event) {
        Player player = event.getPlayer();
        TaskBook book = event.getBookWritten();
        TaskFile file = TaskFile.getTask(book.title);
        if (file != null) {
            if (!PlayerFile.getPlayerFile(player.getName()).issetTask(file)) {
                PlayerClickTaskEvent events = new PlayerClickTaskEvent(file, player);
                Server.getInstance().getPluginManager().callEvent(events);
            }
        }
        book.upData(file, player);
        ItemWrittenBook written = book.toBook();
        player.getInventory().removeItem(player.getInventory().getItemInHand());
        player.getInventory().setItemInHand(written.clone());


    }

    @EventHandler
    public void onAddTask(PlayerAddTaskEvent event) {
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        if (player.getGamemode() == 1) {
            player.sendMessage(RsTask.getTask().getLag("CreateUI", "§d§l[Quest system]§c Creation mode can't wake up UI"));
            return;
        }

        if (file != null) {
            PlayerFile file1 = PlayerFile.getPlayerFile(player.getName());
            file1.addTask(file);
            addBook(player, file);
        }

    }


    @EventHandler
    public void onDelTask(DelTaskEvent event) {
        TaskFile file = event.getFile();

        RsTask.getTask().tasks.remove(file.getTaskName());
        Server.getInstance().getLogger().info("[Task System] Preparing to delete the task " + file.getTaskName());
        RsTask.getTask().taskConfig.remove(event.getFile().getTaskName());
        Server.getInstance().getLogger().info("[Task System] Starting to search for players");
        LinkedList<String> players = RsTask.getTask().getPlayerNames();
        Server.getInstance().getLogger().info("[Task System] Found " + players.size() + " players");

        int i = 0;
        for (String playerName : players) {
            Player player = Server.getInstance().getPlayer(playerName);
            if (player != null) {
                RsTask.getTask().getClickTask.remove(player);
            }

            PlayerFile file1 = PlayerFile.getPlayerFile(playerName);
            if (file1.issetTask(file)) {
                i++;
                if (!file1.delTask(file.getTaskName())) {
                    Server.getInstance().getLogger().info("[Task System] Player " + playerName + " failed to remove task " + file.getTaskName());
                } else {
                    Server.getInstance().getLogger().info("[Task System] Player " + playerName + " successfully removed task " + file.getTaskName());
                }
            }

        }
        Server.getInstance().getLogger().info("[Task System] Removed task " + file.getTaskName() + " from " + i + " players");
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryChange(EntityInventoryChangeEvent event) {
        Entity player = event.getEntity();
        if (player instanceof Player) {
            RsTask.executor.execute(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    PlayerFile file = PlayerFile.getPlayerFile(player.getName());
                    if (file != null) {
                        LinkedList<PlayerTask> getTasks = file.getInviteTasks();
                        if (getTasks == null || getTasks.size() == 0) {
                            return;
                        }
                        new CheckInventoryTask((Player) player, getTasks, file, event.getOldItem(), event.getNewItem(), event.isCancelled()).run();
                    }
                } catch (Exception ignored) {
                }
            });


        }
    }

    @EventHandler
    public void pickBlock(PlayerBlockPickEvent event) {
        Item item = event.getItem();
        if (event.isCancelled()) {
            return;
        }
        API.addItem(event.getPlayer(), item, TaskFile.TaskType.CollectItem);
    }


    public static void defaultUseTask(String player, String item, TaskFile.TaskType type, boolean echo) {
        defaultUseTask(player, item, type, 1, echo, true);
    }


    /**
     * DIY 任务哦 ~~~
     */

    public static void defaultUseTask(String player, String item, TaskFile.TaskType type, int add, boolean echo, boolean canAdd) {

        PlayerFile file = PlayerFile.getPlayerFile(player);
        LinkedList<PlayerTask> getTasks = file.getInviteTasks();
        for (PlayerTask task : getTasks) {
            if (!task.getTaskClass().issetTaskItem(item)) {
                continue;
            }
            if (task.getTaskClass().getOpen()) {
                if (task.getTaskFile().getType() == type) {
                    if (canAdd) {
                        if (!file.addTaskValue(task.getTaskName(), item, add)) {
                            if (echo) {
                                Server.getInstance().getLogger().warning(player + "fulfillment" + task.getTaskName() + "In-mission spinoffs" + item + "abnormalities");
                                return;
                            }
                        } else {
                            Player player1 = Server.getInstance().getPlayer(player);
                            if (player1 != null) {
                                useTask(player1, task);
                            }
                        }
                    } else {
                        if (file.setTaskValue(task.getTaskName(), item, add)) {
                            Player player1 = Server.getInstance().getPlayer(player);
                            if (player1 != null) {
                                useTask(player1, task);
                            }
                        }
                    }
                }
            }
        }

    }

    private static void useTask(Player player, PlayerTask task) {
        if (player.getGamemode() == 1) {
            player.sendMessage(RsTask.getTask().getLag("CreateTask", "§d§l[Task system]§c Creation mode cannot add tasks"));
            return;
        }
        UseTaskEvent event = new UseTaskEvent(player, task);
        Server.getInstance().getPluginManager().callEvent(event);
    }
}
