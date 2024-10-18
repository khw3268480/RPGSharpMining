package com.binggre.rpgsharpmining.listenrs;

import com.binggre.rpgsharpmining.config.Config;
import com.hj.rpgsharp.rpg.apis.rpgsharp.events.monster.MonsterDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MonsterListener implements Listener {

    @EventHandler
    public void onDeath(MonsterDeathEvent event) {
        Double bonusExp = Config.getInstance().getBonusExp(event.getPlayer());
        if (bonusExp == null) {
            return;
        }
        event.addExpPercentage(bonusExp);
    }
}