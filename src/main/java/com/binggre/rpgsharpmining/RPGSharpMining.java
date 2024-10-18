package com.binggre.rpgsharpmining;

import com.binggre.rpgsharpmining.commands.InfoCommand;
import com.binggre.rpgsharpmining.config.Config;
import com.binggre.rpgsharpmining.listenrs.*;
import com.binggre.rpgsharpmining.objects.MiningBlock;
import com.binggre.rpgsharpmining.objects.MiningTool;
import com.binggre.rpgsharpmining.objects.PlayerMiner;
import com.binggre.rpgsharpmining.scheduler.MiningScheduler;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGSharpAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.DBCollection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGSharpMining extends JavaPlugin {

    private static RPGSharpMining instance = null;

    public static RPGSharpMining getInstance() {
        if (instance == null) {
            instance = new RPGSharpMining();
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        DBCollection.addCollection(MiningTool.COLLECTION, true);
        DBCollection.addCollection(MiningBlock.COLLECTION, true);
        DBCollection.addCollection(PlayerMiner.COLLECTION, true);

        saveResource("blocks/example.json", true);
        saveResource("blocks/example2.json", true);
        saveResource("tools/example.json", false);
        saveResource("config.json", false);
        saveResource("message.json", false);

        Config.getInstance().reload();

        registerEvents();

        getCommand("채광").setExecutor(new InfoCommand());

        new MiningScheduler().runTaskTimer(this, 20, 20);
        RPGSharpAPI.getRPGPlayerAPI().getOnlineRPGPlayers().forEach(rpgPlayer -> {
            PlayerMiner.read(rpgPlayer.toPlayer()).register();
        });
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new CharacterListener(), this);
        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new RPGSharpReloadListener(), this);
        pluginManager.registerEvents(new PlayerInteractListener(), this);
        pluginManager.registerEvents(new MonsterListener(), this);
    }

    @Override
    public void onDisable() {
        PlayerMiner.getPlayers().values().forEach(PlayerMiner::write);
    }
}
