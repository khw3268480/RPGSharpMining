package com.binggre.rpgsharpmining.listenrs;

import com.binggre.rpgsharpmining.config.Config;
import com.hj.rpgsharp.rpg.apis.rpgsharp.events.RPGSharpReloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RPGSharpReloadListener implements Listener {

    @EventHandler
    public void onReload(RPGSharpReloadEvent event) {
        Config.getInstance().reload();
    }
}