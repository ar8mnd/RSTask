package com.task.utils;

import angga7togk.economyapi.EconomyAPI;
import angga7togk.economyapi.database.EconomyDB;
import cn.nukkit.Player;
import cn.nukkit.Server;

/**
 * 使用 EconomyAPI 或 Money
 *
 * @author SmallasWater
 */
public class LoadMoney {
    public static final int ECONOMY_API = 0;

    private int money;

    public LoadMoney() {
        if (Server.getInstance().getPluginManager().getPlugin("EconomyAPI") != null) {
            money = ECONOMY_API;
        } else {
            money = -1;
        }
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getMonetaryUnit() {
        if (this.money == ECONOMY_API) {
            return EconomyAPI.getInstance().getDescription().getPrefix();
        }
        return "$";
    }

    public double myMoney(Player player) {
        return myMoney(player.getName());
    }

    public double myMoney(String player) {
        if (this.money == ECONOMY_API) {
            return EconomyDB.myMoney(player);
        }
        return 0;
    }

    public void addMoney(Player player, int money) {
        addMoney(player.getName(), money);
    }

    public void addMoney(String player, int money) {
        if (this.money == ECONOMY_API) {
            EconomyDB.addMoney(player, money);
        }
    }

    public void reduceMoney(Player player, int money) {
        reduceMoney(player.getName(), money);
    }

    public void reduceMoney(String player, int money) {
        if (this.money == ECONOMY_API) {
            EconomyDB.reduceMoney(player, money);
        }
    }
}
