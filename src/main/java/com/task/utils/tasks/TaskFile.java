package com.task.utils.tasks;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.task.RsTask;
import com.task.events.PlayerAddTaskEvent;
import com.task.utils.DataTool;
import com.task.utils.tasks.taskitems.SuccessItem;
import com.task.utils.tasks.taskitems.TaskButton;
import com.task.utils.tasks.taskitems.TaskItem;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * ____  ____ _____         _
 * |  _ \/ ___|_   _|_ _ ___| | __
 * | |_) \___ \ | |/ _` / __| |/ /
 * |  _ < ___) || | (_| \__ \   <
 * |_| \_\____/ |_|\__,_|___/_|\_\
 *
 * @author 若水
 */
public class TaskFile {


    private String showName;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务难度
     */
    private int star;

    /**
     * 任务分组
     */
    private int group;

    /**
     * 任务介绍
     */
    private String taskMessage;

    /**
     * 完成次数限制
     */
    private int successCount = 1;

    /**
     * 奖励物品
     */
    private SuccessItem successItem;

    /**
     * 首次完成奖励
     */
    private SuccessItem firstSuccessItem;

    /**
     * 任务内容
     */
    private TaskItem[] taskItem;

    /**
     * 任务类型
     */
    private TaskType type;

    /**
     * 上一个任务
     */
    private String task;

    private LinkedList<String> notInviteTasks = new LinkedList<>();

    private LinkedList<String> notToInviteTasks = new LinkedList<>();
    /**
     * 刷新时间
     */
    private int day;

    /**
     * 持续时间
     */
    private int loadDay = -1;

    /**
     * 完成公告类型(0/1)
     */
    private int messageType;

    /**
     * 公告内容
     */
    private String broadcastMessage;

    /**
     * 按键图片
     */
    private TaskButton button;


    public enum TaskType {
        /**
         * 破坏任务
         */
        BlockBreak("break"), // 破坏
        /**
         * 放置任务
         */
        BlockPlayer("place"), // 放置
        /**
         * 丢弃任务
         */
        DropItem("drop"), // 丢弃
        /**
         * 收集任务
         */
        CollectItem("collect"), // 收集
        /**
         * 合成任务
         */
        CraftItem("craft"), // 合成
        /**
         * 获得 任务
         */
        GetItem("get"), // 获得

        EatItem("eat"), // 吃

        GetWater("water"), // 打水

        Click("click"), // 点击

        DIY("customizable"); // 自定义

        protected String taskType;

        TaskType(String taskType) {

            this.taskType = taskType;
        }

        public String getTaskType() {
            return taskType;
        }
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem) {
        this(taskName, type, taskItem, "not have");
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage) {
        this(taskName, type, taskItem, taskMessage, 1);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star) {
        this(taskName, type, taskItem, taskMessage, star, star - 1, null);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, int group, SuccessItem item) {
        this(taskName, type, taskItem, taskMessage, star, group, item, null);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, int group, SuccessItem item, String task) {
        this(taskName, type, taskItem, taskMessage, star, group, item, task, 0);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, int group, SuccessItem item, String task, int day) {
        this(taskName, type, taskItem, taskMessage, star, group, item, null, task, day, 0);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, int group, SuccessItem item, SuccessItem firstItem, String task, int day, int messageType) {
        this(taskName, type, taskItem, taskMessage, star, group, item, firstItem, task, day, messageType, "§l§c[§bTask system§c]§eCongratulations §a%p §eCompletion of §d[ %s]§eTasks");
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, int group, SuccessItem item, SuccessItem firstItem, String task, int day, int messageType, String broadcastMessage) {
        this(taskName, type, taskItem, taskMessage, star, group, item, firstItem, task, day, messageType, broadcastMessage, new TaskButton(""));
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, int group, SuccessItem item, SuccessItem firstItem, String task, int day, int messageType, String broadcastMessage, TaskButton button) {
        this.type = type;
        this.taskItem = taskItem;
        this.taskName = taskName;
        this.taskMessage = taskMessage;
        this.star = star;
        this.group = group;
        this.successItem = item;
        this.task = task;
        this.day = day;
        this.firstSuccessItem = firstItem;
        this.messageType = messageType;
        this.button = button;
        this.showName = "It's a mission.";
        this.broadcastMessage = broadcastMessage;
    }

    public TaskType getType() {
        return type;
    }

    public int getStar() {
        return star;
    }

    public String getTaskMessage() {
        return taskMessage;
    }

    public String getTaskName() {
        return taskName;
    }

    public SuccessItem getSuccessItem() {
        return successItem;
    }

    public SuccessItem getFirstSuccessItem() {
        return firstSuccessItem;
    }

    public TaskItem[] getTaskItem() {
        return taskItem;
    }

    public String getLastTask() {
        return task;
    }

    public TaskFile getLastTaskFile() {
        return TaskFile.getTask(task);
    }

    public void toSaveConfig() {
        if (taskName == null) {
            return;
        }
        if (!isFileTask(taskName)) {
            RsTask.getTask().saveResource("Task.yml", "/Tasks/" + taskName + ".yml", false);
        }
        LinkedHashMap<String, Object> taskitems = new LinkedHashMap<>();
        if (taskItem != null) {
            for (TaskItem taskItem : taskItem) {
                taskitems.putAll(taskItem.toSaveConfig());
            }
        }
        Config config = RsTask.getTask().getTaskConfig(taskName);
        if (showName == null) {
            showName = config.getString("task-display-name", "It's a mission.");
        }
        config.set("task-display-name", showName);
        config.set("task-difficulty", star);
        config.set("task-group", group);
        config.set("task-introduction", taskMessage == null ? "none" : taskMessage);
        config.set("refresh-time", day);
        config.set("duration-time", loadDay);
        config.set("task-type", type.getTaskType());
        config.set("completion-limit", successCount);
        if (task != null && !"null".equals(task)) {
            config.set("need-to-complete-this-task-before", task);
        }
        config.set("cannot-accept-this-task-after-completing", notInviteTasks);
        config.set("cannot-accept-this-task-after-receiving", notToInviteTasks);
        config.set("task-content", taskitems);
        if (firstSuccessItem != null) {
            config.set("first-completion-rewards", firstSuccessItem.toSaveConfig());
        } else {
            config.set("first-completion-rewards", successItem.toSaveConfig());
        }
        config.set("rewards", successItem.toSaveConfig());
        config.set("completion-announcement-type", messageType);
        config.set("announcement-content", broadcastMessage);
        config.set("custom-button-image", button.toSaveConfig());

        config.save();
        RsTask.getTask().taskConfig.put(taskName, config);
    }

    private void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowName() {
        return showName;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void setButton(TaskButton button) {
        this.button = button;
    }

    public TaskButton getButton() {
        return button;
    }

    public void setBroadcastMessage(String broadcastMessage) {
        this.broadcastMessage = broadcastMessage;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setFristSuccessItem(SuccessItem fristSuccessItem) {
        this.firstSuccessItem = fristSuccessItem;
    }

    public void setSuccessItem(SuccessItem successItem) {
        this.successItem = successItem;
    }

    public void setTaskItem(TaskItem[] taskItem) {
        this.taskItem = taskItem;
    }

    public void setLastTask(String task) {
        this.task = task;
    }

    public void setTaskMessage(String taskMessage) {
        taskMessage = taskMessage;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public int getDay() {
        return day;
    }


    public void addTaskItem(TaskItem item) {
        TaskItem[] items = getTaskItem();
        TaskItem[] newItem = new TaskItem[items.length + 1];
        System.arraycopy(items, 0, newItem, 0, newItem.length);
        newItem[items.length] = item;
        this.setTaskItem(newItem);
    }


    public void removeTaskItem(TaskItem item) {
        if (canInArrayTaskItem(item)) {
            TaskItem[] taskItems = taskItem;
            TaskItem[] newItem = new TaskItem[taskItems.length - 1];
            int i = 1;
            for (TaskItem item1 : taskItems) {
                if (!item1.equals(item)) {
                    newItem[i] = item1;
                }
                i++;
            }
            taskItem = newItem;
        }
    }

    public boolean canInArrayTaskItem(TaskItem item) {
        for (TaskItem item1 : taskItem) {
            if (item1.equals(item)) {
                return true;
            }
        }
        return false;
    }


    public int getMessageType() {
        return messageType;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public static boolean isFileTask(String taskName) {
        File file = new File(RsTask.getTask().getDataFolder() + "/Tasks/" + taskName + ".yml");
        return file.exists();
    }

    public static TaskFile getTask(String taskName) {
        return RsTask.getTask().tasks.get(taskName);
    }

    public String getName() {
        if ("null".equalsIgnoreCase(showName) || showName == null || "".equalsIgnoreCase(showName)) {
            return getTaskName();
        }
        return getShowName();
    }

    private static TaskFile toTask(String taskName) {
        try {
            if (isFileTask(taskName)) {
                Config config = RsTask.getTask().getTaskConfig(taskName);
                String sType = config.getString("task-type");
                int succount = config.getInt("completion-limit", 1);
                TaskType type = null;
                for (TaskType taskType : TaskType.values()) {
                    if (taskType.getTaskType().equals(sType)) {
                        type = taskType;
                        break;
                    }
                }
                Map map = (Map) config.get("task-content");
                TaskItem[] taskItems;
                if (map != null) {
                    taskItems = new TaskItem[map.size()];
                    int i = 0;
                    for (Object os : map.keySet()) {
                        if (os instanceof String) {
                            taskItems[i] = TaskItem.toTaskItem(taskName, new LinkedHashMap<String, Integer>() {{
                                put((String) os, Integer.parseInt(String.valueOf(map.get(os))));
                            }});
                            i++;
                        }
                    }
                } else {
                    return null;
                }
                Map firstSuccess = (Map) config.get("first-completion-rewards");
                SuccessItem first = SuccessItem.toSuccessItem(firstSuccess);
                Map success = (Map) config.get("rewards");
                SuccessItem second = SuccessItem.toSuccessItem(success);
                if (type == null) {
                    return null;
                }
                TaskFile file = new TaskFile(
                        taskName,
                        type,
                        taskItems,
                        config.getString("task-introduction"),
                        config.getInt("task-difficulty"),
                        config.getInt("task-group",  config.getInt("task-difficulty") - 1),
                        second,
                        first,
                        config.getString("need-to-complete-this-task-before"),
                        config.getInt("refresh-time", 0),
                        config.getInt("completion-notification-type"), config.getString("announcement-content"),
                        TaskButton.toTaskButton((Map) config.get("custom-button-image"))
                );
                file.setShowName(config.getString("task-display-name"));
                file.setSuccessCount(succount);
                file.setNotInviteTasks(new LinkedList<>(config.getStringList("cannot-accept-this-task-after-completing")));
                file.setNotToInviteTasks(new LinkedList<>(config.getStringList("cannot-accept-this-task-after-receiving")));
                file.setLoadDay(config.getInt("duration-time", 1440));
                return file;
            }
        } catch (Exception e) {
            Server.getInstance().getLogger().error("Read " + taskName + "Errors in the task file may occur because it no longer exists or because there is a configuration problem.", e);
            File file = new File(RsTask.getTask().getDataFolder() + "/Tasks/" + taskName + ".yml");
            if (file.exists()) {
                Server.getInstance().getLogger().error("Update Error: Detected " + taskName + " Existing, deleted " + taskName + ".yml file");
                if (!file.delete()) {
                    Server.getInstance().getLogger().error("removing" + taskName + ".yml file");
                }
            }
            return null;
        }
        return null;
    }

    public void setLoadDay(int loadDay) {
        this.loadDay = loadDay;
    }

    public int getLoadDay() {
        return loadDay;
    }

    public void setNotToInviteTasks(LinkedList<String> notToInviteTasks) {
        this.notToInviteTasks = notToInviteTasks;
    }

    public LinkedList<String> getNotToInviteTasks() {
        return notToInviteTasks;
    }

    public LinkedList<String> getNotInviteTasks() {
        return notInviteTasks;
    }

    public void setNotInviteTasks(LinkedList<String> notInviteTasks) {
        this.notInviteTasks = notInviteTasks;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public boolean close() {
        if (isFileTask(this.taskName)) {
            File file = new File(RsTask.getTask().getDataFolder() + "/Tasks/" + taskName + ".yml");
            return file.delete();
        }
        return true;
    }


    /**
     * 根据TaskItem 获取count
     *
     * @param item 任务进度类
     * @return 获取进度
     */
    public int getCountByTaskItem(TaskItem item) {
        for (TaskItem item1 : taskItem) {
            if (item1.equals(item)) {
                return item1.getEndCount();
            }
        }
        return 0;
    }

    /**
     * 获取所有任务文件
     *
     * @return 任务类
     */
    public static LinkedHashMap<String, TaskFile> getTasks() {
        int sCount = 0;
        int errorCount = 0;
        File file = new File(RsTask.getTask().getDataFolder() + "/Tasks");
        LinkedHashMap<String, TaskFile> names = new LinkedHashMap<>();
        File[] files = file.listFiles();
        if (files != null) {
            Arrays.sort(files);
            for (File file1 : files) {
                if (file1.isFile()) {
                    String name = file1.getName().substring(0, file1.getName().lastIndexOf("."));
                    TaskFile file2 = TaskFile.toTask(name);
                    if (file2 != null) {
                        names.put(name, file2);
                        sCount++;
                    } else {
                        errorCount++;
                    }
                } else {
                    errorCount++;
                }
            }
            RsTask.getTask().getLogger().info(TextFormat.colorize('&', "Task loading complete &a" + sCount + "&r task loaded successfully &c" + errorCount + "&r tasks failed to load"));

        }
        return names;
    }

    /**
     * 获取任务分组中的全部任务
     *
     * @param group 分组
     * @return 任务文件列表
     */
    public static LinkedList<TaskFile> getDifficultyTasks(int group) {
        LinkedList<TaskFile> files = new LinkedList<>();
        for (TaskFile file : RsTask.getTask().tasks.values()) {
            if (file.getGroup() == group) {
                files.add(file);
            }
        }
        DataTool.sortTaskFiles(files);
        return files;
    }

    /**
     * 玩家点击任务后进行的一系列判断
     *
     * @param player 玩家
     * @param file   任务文件
     * @return 是否成功
     */
    public static boolean runTaskFile(Player player, TaskFile file) {
        PlayerFile file1 = PlayerFile.getPlayerFile(player.getName());
        PlayerFile.PlayerTaskType type = file1.getTaskType(file);

        if (type == PlayerFile.PlayerTaskType.can_Invite || type == PlayerFile.PlayerTaskType.isSuccess_canInvite) {
            int starCount = DataTool.starNeed(file.getGroup());
            if (RsTask.canOpen() && file1.getCount() < starCount) {
                player.sendMessage(RsTask.getTask().getLag("not-add-task", "§c [Task System] Sorry, this task is not available."));
                return false;
            } else {
                PlayerAddTaskEvent event1 = new PlayerAddTaskEvent(player, file);
                Server.getInstance().getPluginManager().callEvent(event1);
                return true;
            }
        }
        if (type == PlayerFile.PlayerTaskType.Running || type == PlayerFile.PlayerTaskType.Success) {
            return true;
        }
        if ((file.getLastTask() != null && !"null".equals(file.getLastTask()) && !"".equals(file.getLastTask()))) {
            if (!file1.isSuccessed(file.getLastTask())) {
                player.sendMessage(RsTask.getTask().getLag("useLastTask").replace("%s", file.getLastTask()));
                return false;
            }
        }
        if (file1.isSuccessed(file.getTaskName())) {
            if (file.getSuccessCount() != -1) {
                if (file.getSuccessCount() <= file1.getSuccessedCount(file.getTaskName())) {
                    player.sendMessage(RsTask.getTask().getLag("repeat-collection"));
                    return false;
                }
            }
        }

        if (!file1.inDay(file.getTaskName())) {
            //任务刷新时长 分钟
            int day = file.getDay();
            int hours = 60;
            int dayTime = 1440;
            //流逝的时间 (分钟)
            int out = DataTool.getTime(file1.getTaskByName(file.getTaskName()).getTaskClass().getTime());
            int dayM = ((day > out) ? (day - out) : 0);
            String dayMs = dayM + " minutes";
            if (dayM / hours >= 1) {
                if (dayM / dayTime >= 1) {
                    dayMs = (dayM / dayTime) + " days";
                } else {
                    dayMs = (dayM / hours) + " hours";
                }
            }
            player.sendMessage(RsTask.getTask().getLag("repeat-inDay").
                    replace("%c", dayMs + ""));
            return false;
        }
        if (type == PlayerFile.PlayerTaskType.No_Invite) {
            player.sendMessage(RsTask.getTask().getLag("not-add-task", "§c [Task System] Sorry, this task is not available."));
            return false;
        }

        return true;
    }


}
