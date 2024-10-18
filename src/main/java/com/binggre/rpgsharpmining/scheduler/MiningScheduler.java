package com.binggre.rpgsharpmining.scheduler;

import com.binggre.rpgsharpmining.objects.PlayerCooldown;
import com.binggre.rpgsharpmining.objects.PlayerMiner;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MiningScheduler extends BukkitRunnable {

    @Override
    public void run() {
        for (PlayerMiner miner : PlayerMiner.getPlayers().values()) {
            for (PlayerCooldown cooldown : miner.getCooldowns().values()) {
                if (cooldown.minus() <= 0) {
                    miner.removeCooldown(cooldown);
                    String uuid = miner.getUuid();
                    Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                    System.out.println(miner.getUuid());
                    player.sendActionBar("§f◇ §a채광할 수 있습니다!");
                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        }
    }
}
