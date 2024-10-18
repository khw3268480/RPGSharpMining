package com.binggre.rpgsharpmining.events;

import com.binggre.rpgsharpmining.objects.MiningReward;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;

import javax.annotation.Nonnull;

public class MiningSuccessEvent extends Event {

    private final BlockBreakEvent blockBreakEvent;
    private final MiningReward miningReward;
    private static final HandlerList handlers = new HandlerList();

    public MiningSuccessEvent(BlockBreakEvent blockBreakEvent, MiningReward miningReward) {
        this.blockBreakEvent = blockBreakEvent;
        this.miningReward = miningReward;
    }

    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BlockBreakEvent getBlockBreakEvent() {
        return this.blockBreakEvent;
    }

    public MiningReward getMiningReward() {
        return this.miningReward;
    }
}
