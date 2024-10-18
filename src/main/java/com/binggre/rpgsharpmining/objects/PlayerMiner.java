package com.binggre.rpgsharpmining.objects;

import com.binggre.rpgsharpmining.config.Config;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMiner {

    public static final String COLLECTION = PlayerMiner.class.getSimpleName();
    private static final Map<String, PlayerMiner> players = new HashMap<>();


    public static Map<String, PlayerMiner> getPlayers() {
        return players;
    }

    public static PlayerMiner get(Player player) {
        return get(player.getUniqueId());
    }

    public static PlayerMiner get(UUID uuid) {
        return players.get(uuid.toString());
    }

    public static PlayerMiner read(Player player) {
        String uuid = player.getUniqueId().toString();
        PlayerMiner read = (PlayerMiner) FileUtil.read(PlayerMiner.class, COLLECTION, "uuid", uuid);
        if (read == null) {
            read = new PlayerMiner(uuid);
        }
        return read;
    }

    private String uuid;
    private int level;
    private double exp;
    private double maxExp;
    private final Map<Material, PlayerCooldown> cooldowns;
    private String permission;
    private String permissionView;

    public PlayerMiner(String uuid) {
        this.uuid = uuid;
        this.level = 1;
        this.exp = 0;
        this.maxExp = Config.getInstance().getMaxExp(level);
        this.cooldowns = new HashMap<>();

        Config config = Config.getInstance();
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        String permission = config.getPermission(level);
        String permissionView = config.getPermissionView(level);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set " + permission + " true");

        this.permission = permission;
        this.permissionView = permissionView;
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(UUID.fromString(this.uuid));
    }

    public Map<Material, PlayerCooldown> getCooldowns() {
        return cooldowns;
    }

    public PlayerCooldown getCooldown(Block block) {
        return cooldowns.get(block.getType());
    }

    public void addCooldown(PlayerCooldown playerCooldown) {
        cooldowns.put(playerCooldown.getMaterial(), playerCooldown);
    }

    public void removeCooldown(PlayerCooldown cooldown) {
        cooldowns.remove(cooldown.getMaterial());
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        if (exp <= 0) {
            exp = 0;
        }
        this.exp = exp;
        this.checkLevelUp();
    }

    public double getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(double maxExp) {
        this.maxExp = maxExp;
    }

    public void register() {
        players.put(uuid, this);
    }

    public void unregister() {
        players.remove(uuid);
    }

    public void write() {
        FileUtil.write(this, COLLECTION, "uuid", true);
    }

    public void addExp(double exp) {
        setExp(this.exp + exp);
    }

    public String getPermission() {
        return permission;
    }

    public String getPermissionView() {
        return permissionView;
    }

    private void checkLevelUp() {
        Config config = Config.getInstance();
        while (this.exp >= this.maxExp) {
            Double maxExp = config.getMaxExp(++level);

            String permission = config.getPermission(level);
            String permissionView = config.getPermissionView(level);

            if (permission != null && permissionView != null) {
                String nickname = Bukkit.getPlayer(UUID.fromString(this.uuid)).getName();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("lp user %s permission unset %s", nickname, this.permission));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("lp user %s permission set %s true", nickname, permission));
                this.permission = permission;
                this.permissionView = permissionView;
            }
            if (maxExp == null) {
                setExp(0);
                break;
            }
            this.exp -= this.maxExp;
            this.maxExp = maxExp;
        }
        write();
    }
}