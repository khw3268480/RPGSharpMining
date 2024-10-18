package com.binggre.rpgsharpmining.listenrs;

import com.binggre.rpgsharpmining.Message;
import com.binggre.rpgsharpmining.objects.MiningTool;
import com.binggre.rpgsharpmining.objects.PlayerCooldown;
import com.binggre.rpgsharpmining.objects.PlayerMiner;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGItemAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGSharpAPI;
import com.hj.rpgsharp.rpg.plugins.rpgitem.objects.RPGItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    private final RPGItemAPI rpgItemAPI = RPGSharpAPI.getRPGItemAPI();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemInMainHand = event.getItem();
        if (itemInMainHand == null || itemInMainHand.getType() == Material.AIR) {
            return;
        }
        RPGItem rpgItem = rpgItemAPI.getRPGItem(itemInMainHand);
        if (rpgItem == null) {
            return;
        }
        String dataCode = rpgItem.getDataCode();
        MiningTool miningTool = MiningTool.get(dataCode);
        if (miningTool == null) {
            return;
        }
        PlayerMiner playerMiner = PlayerMiner.get(player);
        PlayerCooldown cooldown = playerMiner.getCooldown(clickedBlock);
        if (cooldown == null) {
            return;
        }
        String replace = Message.getInstance().CHECK_COOLDOWN.replace("<time>", String.valueOf(cooldown.getCooldown()));
        player.sendMessage(replace);
    }
}