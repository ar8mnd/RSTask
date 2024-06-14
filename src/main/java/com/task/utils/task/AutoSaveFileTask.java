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
            getOwner().getLogger().info("[Auto Save] Plug-in data being saved");
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
                            getOwner().getLogger().log(LogLevel.ERROR, "player " + file.getPlayerName() + " Task File Save Failure!");
                            errorCount++;
                        }
                    }
                    getOwner().getLogger().info("[Auto Save] Save completed. " + saveTaskCount + " task configurations saved successfully, " + saveCount + " player task data saved successfully.");
                    if (errorCount > 0) {
                        getOwner().getLogger().warning("[Auto Save] " + errorCount + " player task data failed to save.");
                    }
                    saveTaskCount = 0;
                    saveCount = 0;
                    errorCount = 0;
                } catch (Exception e) {
                    getOwner().getLogger().info("[Auto Save] An exception occurred during saving. " + saveTaskCount + " task configurations saved successfully, " + saveCount + " player task data saved successfully, " + errorCount + " player task data failed to save.");
                }
            });
            try {
                Thread.sleep(getOwner().getConfig().getInt("auto-save-task.time") * 60000L);
            } catch (InterruptedException e) {
                getOwner().getLogger().error("[Task] An exception occurred while saving task data.", e);
            }

                Server.getInstance().getPluginManager().callEvent(new TaskStopEvent(getOwner(), this));
                return;
            }
        }
}
