package com.binggre.rpgsharpmining.objects;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class PlayerCooldown {

    private Material material;
    private int cooldown;

    public PlayerCooldown(Block block, int cooldown) {
        this.material = block.getType();
        this.cooldown = cooldown;
    }

    public int minus() {
        return --cooldown;
    }

    public Material getMaterial() {
        return material;
    }

    public int getCooldown() {
        return cooldown;
    }
}