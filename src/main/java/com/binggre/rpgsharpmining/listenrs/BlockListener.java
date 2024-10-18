package com.binggre.rpgsharpmining.listenrs;

import com.binggre.rpgsharpmining.Message;
import com.binggre.rpgsharpmining.events.MiningSuccessEvent;
import com.binggre.rpgsharpmining.objects.*;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGItemAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGSharpAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.HexColorUtil;
import com.hj.rpgsharp.rpg.objects.RPGPlayer;
import com.hj.rpgsharp.rpg.plugins.rpgitem.objects.RPGItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class BlockListener implements Listener {

    private final RPGItemAPI rpgItemAPI = RPGSharpAPI.getRPGItemAPI();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == Material.AIR) {
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
        event.setCancelled(true);
        Block block = event.getBlock();
        MiningBlock miningBlock = MiningBlock.get(block);
        if (miningBlock == null) {
            return;
        }
        PlayerMiner playerMiner = PlayerMiner.get(player);
        PlayerCooldown cooldown = playerMiner.getCooldown(block);

        if (cooldown == null) {
            int coolTime = (miningBlock.getCooldown() - miningTool.getCooldownReduction());

            if ( PlayerMiner.get(player).getLevel() <= 3 ){
                coolTime -= 20;
            }
            cooldown = new PlayerCooldown(block, coolTime);
            playerMiner.addCooldown(cooldown);
            playerMiner.addCooldown(cooldown);

            MiningReward miningReward = getReward(miningBlock, playerMiner);

//            if (miningReward == null || miningReward.getDataCodes().length == 0) {
//                player.sendMessage("◇ §c채광에서 아무것도 얻지 못했습니다.");
//                return;
//            }

            if (miningReward == null) {
                player.sendMessage("◇ §c채광에서 아무것도 얻지 못했습니다.");
                return;
            }

            MiningSuccessEvent miningEvent = new MiningSuccessEvent(event, miningReward);
            Bukkit.getServer().getPluginManager().callEvent(miningEvent);

            String rewardCommand = miningReward.getCommand();
            RPGItem rewardRPGItem = miningReward.getRPGItem();

            miningReward.give(player, rewardRPGItem == null ? null : rewardRPGItem.getFakeItem().getItemStack());
            if (rewardCommand != null) {
                if (rewardCommand.contains(":console")) {
                    String replace = rewardCommand.replace(":console", "").replace("<player>", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replace);
                } else {
                    player.performCommand(rewardCommand.replace("<player>", player.getName()));
                }
            }

            List<String> successMessage = Message.getInstance().getSuccess(miningReward);
            successMessage.forEach(player::sendMessage);
            if (player.isOp()) {
                playerMiner.getCooldowns().clear();
            }

        } else {
            String message = HexColorUtil.format(Message.getInstance().getCooldown()
                    .replace("<time>", String.valueOf(cooldown.getCooldown())));
            player.sendMessage(message);
        }
    }

    private MiningReward getReward(MiningBlock miningBlock, PlayerMiner playerMiner) {
        Map<Double, MiningReward> rewardMap = miningBlock.getRewards();
        NavigableMap<Double, MiningReward> cumulativeMap = new TreeMap<>();
        double totalPercentage = 0.0;

        for (Map.Entry<Double, MiningReward> entry : rewardMap.entrySet()) {
            Double percentage = entry.getKey();
            MiningReward reward = entry.getValue();

            if (reward.getLimitLevel() <= playerMiner.getLevel()) {
                totalPercentage += percentage;
                cumulativeMap.put(totalPercentage, reward);
            }
        }
        if (cumulativeMap.isEmpty()) {
            return null;
        }
        double randomValue = Math.random() * totalPercentage;
        return cumulativeMap.higherEntry(randomValue).getValue();
    }
}