package com.binggre.rpgsharpmining;

import com.binggre.rpgsharpmining.objects.MiningReward;
import com.binggre.rpgsharpmining.objects.PlayerMiner;
import com.google.gson.annotations.SerializedName;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGItemAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.RPGSharpAPI;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.FileUtil;
import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.HexColorUtil;
import com.hj.rpgsharp.rpg.plugins.rpgitem.objects.RPGItem;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Message {

    private static Message instance = null;
    private static final Supplier<File> FILE = () -> {
        String path = RPGSharpMining.getInstance().getDataFolder() + "\\message.json";
        return new File(path);
    };

    public static Message getInstance() {
        if (instance == null) {
            instance = new Message();
        }
        return instance;
    }

    @SerializedName("채광 성공")
    public List<String> SUCCESS;
    @SerializedName("채광 실패")
    public String FAILED;
    @SerializedName("채광 쿨타임")
    public String COOLDOWN;
    @SerializedName("클릭 쿨타임 확인")
    public String CHECK_COOLDOWN;
    @SerializedName("정보")
    public List<String> CHECK_INFO;

    public List<String> getSuccess(MiningReward reward) {
        double money = reward.getMoney();
        List<ItemStack> items = reward.getItems();
        double exp = reward.getExp();

        List<String> msg = new ArrayList<>(SUCCESS);
        String msgContents = msg.toString();
        if (msgContents.contains("<money>")) {
            String moneyStr = String.valueOf((int) money);
            msg.replaceAll(s -> s.replace("<money>", moneyStr));
        }
        if (msgContents.contains("<exp>")) {
            msg.replaceAll(s -> s.replace("<exp>", String.valueOf(exp)));
        }
        boolean hasItemsPlaceHolder = msgContents.contains("<items>");
        if (!hasItemsPlaceHolder) {
            return msg;
        }
        List<String> newList = new ArrayList<>();
        StringBuilder itemsInOneLine = new StringBuilder();
        for (ItemStack item : items) {
            itemsInOneLine.append(getItemDisplayName(reward, item)).append(", ");
        }
        if (!itemsInOneLine.isEmpty()) {
            itemsInOneLine.setLength(itemsInOneLine.length() - 2);
        }
        for (String s : msg) {
            s = HexColorUtil.format(s).replace("&", "§");
            if (s.contains("<items>")) {
                newList.add(s.replace("<items>", itemsInOneLine.toString()));
            } else {
                newList.add(s);
            }
        }
        return newList;
    }

    public List<String> getInfo(PlayerMiner playerMiner) {
        List<String> msg = new ArrayList<>(CHECK_INFO);
        String msgContents = msg.toString();
        if (msgContents.contains("<level>")) {
            msg.replaceAll(s -> s.replace("<level>", String.valueOf(playerMiner.getLevel())));
        }
        if (msgContents.contains("<min_exp>")) {
            msg.replaceAll(s -> s.replace("<min_exp>", String.valueOf((int) playerMiner.getExp())));
        }
        if (msgContents.contains("<max_exp>")) {
            msg.replaceAll(s -> s.replace("<max_exp>", String.valueOf((int) playerMiner.getMaxExp())));
        }
        if (msgContents.contains("<permission>")) {
            msg.replaceAll(s -> s.replace("<permission>", playerMiner.getPermissionView()));
        }
        msg.replaceAll(s -> HexColorUtil.format(s).replace("&", "§"));
        return msg;
    }

    private String getItemDisplayName(MiningReward reward, ItemStack item) {
        RPGItemAPI rpgItemAPI = RPGSharpAPI.getRPGItemAPI();
        RPGItem rpgItem = rpgItemAPI.getRPGItem(item);
        String viewName = reward.getViewName(rpgItem);
        return viewName == null ? item.getItemMeta().getDisplayName() : viewName;
    }

    public String getFailed() {
        return FAILED;
    }

    public String getCooldown() {
        return COOLDOWN;
    }

    public void reload() {
        instance = (Message) FileUtil.read(FILE.get(), Message.class);
        instance.CHECK_COOLDOWN = HexColorUtil.format(instance.CHECK_COOLDOWN);
        instance.FAILED = HexColorUtil.format(instance.FAILED);
        instance.COOLDOWN = HexColorUtil.format(instance.COOLDOWN);
    }
}
