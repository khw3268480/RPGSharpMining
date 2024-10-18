package com.binggre.rpgsharpmining.objects;

import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.FileUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiningBlock {

    public static final String COLLECTION = MiningBlock.class.getSimpleName();
    private static final Map<Material, MiningBlock> blocks = new HashMap<>();

    public static MiningBlock get(Block block) {
        return blocks.get(block.getType());
    }

    public static void registerAll() {
        List<MiningBlock> read = (List<MiningBlock>) FileUtil.read(MiningBlock.class, COLLECTION, true);
        read.forEach(MiningBlock::register);
    }

    private String blockName;
    private int cooldown;
    private Map<Double, MiningReward> rewards;

    public String getBlockName() {
        return blockName;
    }

    public Map<Double, MiningReward> getRewards() {
        return rewards;
    }

    public void register() {
        blockName = blockName.toUpperCase().replace(" ", "_");
        blocks.put(Material.valueOf(blockName), this);
    }

    public int getCooldown() {
        return cooldown;
    }
}