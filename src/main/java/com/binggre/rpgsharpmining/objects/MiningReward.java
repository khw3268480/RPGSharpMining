package com.binggre.rpgsharpmining.objects;

import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGItemAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGPlayerAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGSharpAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.EconomyUtil;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.InventoryUtil;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.NumberUtil;
import com.hj.rpgsharp.rpg.objects.RPGPlayer;
import com.hj.rpgsharp.rpg.plugins.mailbox.objects.Mail;
import com.hj.rpgsharp.rpg.plugins.mailbox.objects.RPGMail;
import com.hj.rpgsharp.rpg.plugins.rpgitem.objects.RPGItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiningReward {

    private int limitLevel;
    private String[] dataCodes;
    private List<String> viewNames;
    private double exp;
    private String money;

    private transient List<ItemStack> itemStacks;

    public String[] getDataCodes() {
        return dataCodes;
    }

    public int getLimitLevel() {
        return limitLevel;
    }

    public double getExp() {
        return exp;
    }

    private String command;
    private String dataCode;

    public String getCommand() {
        return command;
    }

    public RPGItem getRPGItem() {
        if (dataCode == null) {
            return null;
        }
        return RPGSharpAPI.getRPGItemAPI().getRPGItem(dataCode);
    }

    public String getViewName(RPGItem rpgItem) {
        for (String viewName : viewNames) {
            String[] split = viewName.split(":");
            if (split[0].equals(rpgItem.getDataCode())) {
                return split[1];
            }
        }
        return null;
    }

    public List<ItemStack> getItems() {
        if (itemStacks == null) {
            List<ItemStack> list = new ArrayList<>();
            RPGItemAPI rpgItemAPI = RPGSharpAPI.getRPGItemAPI();
            for (String dataCode : dataCodes) {
                ItemStack itemStack;
                if (dataCode.length() == 1) {
                    itemStack = rpgItemAPI.getRPGItem(dataCode).getFakeItem().getItemStack().clone();
                } else {
                    String[] split = dataCode.split(":");
                    String dataCode2 = split[0];
                    int amount = Integer.parseInt(split[1]);
                    itemStack = rpgItemAPI.getRPGItem(dataCode2).getFakeItem().getItemStack().clone();
                    itemStack.setAmount(amount);
                }
                list.add(itemStack);
            }
            itemStacks = list;
        }
        return itemStacks;
    }

    public double getMoney() {
        if (money.length() == 1) {
            return Double.parseDouble(money);
        }
        String[] split = money.split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return NumberUtil.getRandomNumber(min, max);
    }

    public void give(Player player, ItemStack additional) {
        List<ItemStack> items = new ArrayList<>(getItems());
        if (additional != null) {
            items.add(additional);
        }
        if (InventoryUtil.hasEmptySpaceInventory(player, items.size())) {
            items.forEach(itemStack -> player.getInventory().addItem(itemStack));
        } else {
            RPGPlayerAPI rpgPlayerAPI = RPGSharpAPI.getRPGPlayerAPI();
            RPGPlayer rpgPlayer = rpgPlayerAPI.getRPGPlayer(player);
            RPGMail rpgMail = rpgPlayer.getRPGMail();
            Mail mail = Mail.createMail("§f시스템", "§f채광 보상", 0, items);
            rpgMail.addMail(mail);
            rpgPlayer.write(true);
        }
        PlayerMiner playerMiner = PlayerMiner.get(player);
        EconomyUtil.addMoney(player, getMoney());
        playerMiner.addExp(exp);
    }
}