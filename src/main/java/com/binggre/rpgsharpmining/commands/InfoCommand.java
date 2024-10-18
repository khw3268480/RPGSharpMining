package com.binggre.rpgsharpmining.commands;

import com.binggre.rpgsharpmining.Message;
import com.binggre.rpgsharpmining.objects.PlayerMiner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        PlayerMiner playerMiner = PlayerMiner.get(player);
        Message instance = Message.getInstance();
        for (String s : instance.getInfo(playerMiner)) {
            player.sendMessage(s);
        }

        return false;
    }
}
