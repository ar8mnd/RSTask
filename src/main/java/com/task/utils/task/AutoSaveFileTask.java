package com.task.utils.task;

import cn.nukkit.Server;
import cn.nukkit.utils.LogLevel;
import com.task.RsTask;
import com.task.events.TaskStopEvent;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;


/**
 * @author SmallasWater
 * Create on 2021/1/23 22:18
 */
public class AutoSaveFileTask implements Runnable {

    private final RsTask owner;

    private RsTask getOwner() {
        return owner;
    }

    public AutoSaveFileTask(RsTask owner) {
        this.owner = owner;
    }

    private int saveTaskCount = 0;

    private int saveCount = 0;

    private int errorCount = 0;


    @Override
    public void run() {
        while (true) {
            getOwner().getLogger().info("[自动保存] 正在保存插件数据");
            RsTask.executor.submit(() -> {
                try {
                    for (TaskFile file : getOwner().tasks.values()) {
                        file.toSaveConfig();
                        saveTaskCount++;
                    }
                    for (PlayerFile file : getOwner().playerFiles.values()) {
                        if (file.toSave()) {
                            saveCount++;
                        } else {
                            getOwner().getLogger().log(LogLevel.ERROR, "玩家 " + file.getPlayerName() + " 任务文件保存失败!");
                            errorCount++;
                        }
                    }
                    getOwner().getLogger().info("[自动保存] 保存完成 " + saveTaskCount + "个任务配置保存成功" + saveCount + "个玩家任务数据保存成功 ");
                    if (errorCount > 0) {
                        getOwner().getLogger().warning("[自动保存] " + errorCount + " 个玩家任务数据保存失败");
                    }
                    saveTaskCount = 0;
                    saveCount = 0;
                    errorCount = 0;
                } catch (Exception e) {
                    getOwner().getLogger().info("[自动保存] 保存出现异常 " + saveTaskCount + "个任务配置保存成功" + saveCount + "个玩家任务数据保存成功 " + errorCount + " 个玩家任务数据保存失败");
                }
            });
            try {
                Thread.sleep(getOwner().getConfig().getInt("auto-save-task.time") * 60000L);
            } catch (InterruptedException e) {
                getOwner().getLogger().error("[任务] 保存任务数据出现异常", e);
                Server.getInstance().getPluginManager().callEvent(new TaskStopEvent(getOwner(), this));
                return;
            }
        }

    }
}
