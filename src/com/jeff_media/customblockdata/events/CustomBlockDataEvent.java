/*
 * Copyright (c) 2022 Alexander Majka (mfnalex) / JEFF Media GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * If you need help or have any suggestions, feel free to join my Discord and head to #programming-help:
 *
 * Discord: https://discord.jeff-media.com/
 *
 * If you find this library helpful or if you're using it one of your paid plugins, please consider leaving a donation
 * to support the further development of this project :)
 *
 * Donations: https://paypal.me/mfnalex
 */

package com.jeff_media.customblockdata.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;

import com.jeff_media.customblockdata.CustomBlockData;

import lombok.NonNull;

/**
 * Represents an event that removes, changes or moves CustomBlockData due to regular Bukkit Events.
 *
 * This event gets called during the underlying Bukkit Event's MONITOR listening phase. If the Bukkit Event
 * was already cancelled, this event will not be called.
 *
 * If this event is cancelled, CustomBlockData will not be removed, changed or moved.
 */
public class CustomBlockDataEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    final @NonNull Plugin plugin;
    final @NonNull Block block;
    final @NonNull CustomBlockData cbd;
    final @NonNull Event bukkitEvent;
    boolean isCancelled = false;

    protected CustomBlockDataEvent(@NonNull Plugin plugin, @NonNull Block block, @NonNull Event bukkitEvent) {
        this.plugin = plugin;
        this.block = block;
        this.bukkitEvent = bukkitEvent;
        this.cbd = new CustomBlockData(block, plugin);
    }

    /**
     * Gets the block involved in this event.
     */
    public @NonNull Block getBlock() {
        return block;
    }

    /**
     * Gets the underlying Bukkit Event that has caused this event to be called. The Bukkit Event is currently listened
     * on in MONITOR priority.
     */
    public @NonNull Event getBukkitEvent() {
        return bukkitEvent;
    }

    /**
     * Gets the CustomBlockData involved in this event.
     */
    public @NonNull CustomBlockData getCustomBlockData() {
        return cbd;
    }

    /**
     * Gets the cancellation status of this event.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation status of this event. If the event is cancelled, the CustomBlockData will not be removed, changed or moved.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    /**
     * Gets the reason for this change of CustomBlockData
     */
    public @NonNull Reason getReason() {
        if (bukkitEvent == null) return Reason.UNKNOWN;
        for (Reason reason : Reason.values()) {
            if (reason == Reason.UNKNOWN) continue;
            if (reason.eventClasses.stream().anyMatch(clazz -> clazz.equals(bukkitEvent.getClass()))) return reason;
        }
        return Reason.UNKNOWN;
    }

    @NonNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NonNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Represents the reason for a change of CustomBlockData
     */
    public enum Reason {
        /**
         * Represents a block being broken by a player
         * @see BlockBreakEvent
         */
        BLOCK_BREAK(BlockBreakEvent.class),
        /**
         * Represents a block being replaced by a new block (for example STONE being placed into a TALL_GRASS)
         * @see BlockPlaceEvent
         * @see BlockMultiPlaceEvent
         */
        BLOCK_PLACE(BlockPlaceEvent.class, BlockMultiPlaceEvent.class),
        /**
         * Represents a block being destroyed by an explosion
         * @see BlockExplodeEvent
         * @see EntityExplodeEvent
         */
        EXPLOSION(EntityExplodeEvent.class, BlockExplodeEvent.class),
        /**
         * Represents a block being moved by a piston
         * @see CustomBlockDataMoveEvent
         * @see BlockPistonExtendEvent
         * @see BlockPistonRetractEvent
         */
        PISTON(BlockPistonExtendEvent.class, BlockPistonRetractEvent.class),
        /**
         * Represents a block being destroyed by fire
         * @see BlockBurnEvent
         */
        BURN(BlockBurnEvent.class),
        /**
         * Represents a block being changed by an entity. An {@link EntityChangeBlockEvent} will only trigger removal
         * of CustomBlockData when the block's material changes.
         * <p>
         * Example: When a player steps on REDSTONE_ORE, an EntityChangeBlockEvent is called because the BlockState's
         * "lit" tag changes from false to true. However, this will not lead to removal of CustomBlockData because
         * the block's material is still REDSTONE_ORE.
         *
         */
        ENTITY_CHANGE_BLOCK(EntityChangeBlockEvent.class),
        /**
         * Represents a block being destroyed by melting, etc. A {@link BlockFadeEvent} will only trigger
         * removal of CustomBlockData when the block's material changes. The event will not be called for fire
         * burning out.
         * @see BlockFadeEvent
         */
        FADE(BlockFadeEvent.class),
        /**
         * Represents a block being changed by a structure (Sapling -> Tree, Mushroom -> Huge Mushroom), naturally or using bonemeal.
         * @see StructureGrowEvent
         */
        STRUCTURE_GROW(StructureGrowEvent.class),
        /**
         * Represents a block being changed by fertilizing a given block with bonemeal.
         * @see BlockFertilizeEvent
         */
        FERTILIZE(BlockFertilizeEvent.class),
        /**
         * Represents leaves decaying. This is currently not called because of performance reasons. In future versions,
         * there will be a method to enable listening to this.
         * @deprecated Draft API
         */
        @Deprecated
        LEAVES_DECAY(LeavesDecayEvent.class),

        UNKNOWN((Class<? extends Event>) null);

        private final @NonNull List<Class<? extends Event>> eventClasses;

        @SafeVarargs
        Reason(Class<? extends Event>... eventClasses) {
            this.eventClasses = Arrays.asList(eventClasses);
        }

        /**
         * Gets a list of Bukkit Event classes that are associated with this Reason
         */
        public @NonNull List<Class<? extends Event>> getApplicableEvents() {
            return this.eventClasses;
        }
    }
}
