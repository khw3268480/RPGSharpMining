package com.binggre.rpgsharpmining.objects;

import com.hj.rpgsharp.rpg.apis.rpgsharp.utils.FileUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiningTool {

    public static final String COLLECTION = MiningTool.class.getSimpleName();
    private static final Map<String, MiningTool> tools = new HashMap<>();

    public static MiningTool get(String dataCode) {
        return tools.get(dataCode);
    }

    public static void registerAll() {
        List<MiningTool> read = (List<MiningTool>) FileUtil.read(MiningTool.class, COLLECTION, true);
        read.forEach(MiningTool::register);
    }

    private String dataCode;
    private int cooldownReduction;

    public int getCooldownReduction() {
        return cooldownReduction;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void register() {
        tools.put(dataCode, this);
    }
}
