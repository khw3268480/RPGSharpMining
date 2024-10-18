package com.binggre.rpgsharpmining.listenrs;

import com.binggre.rpgsharpmining.objects.PlayerMiner;
import com.hj.rpgsharp.rpg.apis.rpgsharp.events.character.CharacterCreateEvent;
import com.hj.rpgsharp.rpg.apis.rpgsharp.events.character.CharacterLoadEvent;
import com.hj.rpgsharp.rpg.apis.rpgsharp.events.character.CharacterQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CharacterListener implements Listener {

    @EventHandler
    public void onLoad(CharacterCreateEvent event) {
        PlayerMiner.read(event.getPlayer()).register();
    }

    @EventHandler
    public void onLoad(CharacterLoadEvent event) {
        PlayerMiner.read(event.getPlayer()).register();
    }

    @EventHandler
    public void onQuit(CharacterQuitEvent event) {
        PlayerMiner playerMiner = PlayerMiner.get(event.getPlayer());
        playerMiner.write();
        playerMiner.unregister();
    }
}