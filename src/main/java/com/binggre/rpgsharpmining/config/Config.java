package com.binggre.rpgsharpmining.config;

import com.binggre.rpgsharpmining.Message;
import com.binggre.rpgsharpmining.RPGSharpMining;
import com.binggre.rpgsharpmining.objects.MiningBlock;
import com.binggre.rpgsharpmining.objects.MiningTool;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.FileUtil;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.HexColorUtil;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Config {

    private static Config instance = null;
    private static final Supplier<File> FILE = () -> {
        String filePath = RPGSharpMining.getInstance().getDataFolder() + "\\config.json";
        return new File(filePath);
    };

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private final Map<Integer, Double> exp = new HashMap<>();
    private final Map<Integer, String> permission = new HashMap<>();
    private final Map<String, Double> bonusExp = new HashMap<>();

    public Double getMaxExp(int level) {
        return exp.get(level);
    }

    public String getPermission(int level) {
        String s = permission.get(level);
        if (s == null) {
            return null;
        }
        return s.split(":")[0];
    }

    public String getPermissionView(int level) {
        String s = permission.get(level);
        if (s == null) {
            return null;
        }
        return HexColorUtil.format(s.split(":")[1]).replace("&", "§");
    }

    public Double getBonusExp(Player player) {
        double bonus = 0;
        for (Map.Entry<String, Double> entry : bonusExp.entrySet()) {
            String permission = entry.getKey();
            if (player.hasPermission(permission)) {
                bonus = entry.getValue();
            }
        }
        return bonus;
    }

    public void reload() {
        instance = (Config) FileUtil.read(FILE.get(), Config.class);
        MiningBlock.registerAll();
        MiningTool.registerAll();
        Message.getInstance().reload();
    }
}
