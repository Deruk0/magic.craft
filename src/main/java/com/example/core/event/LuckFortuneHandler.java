package com.example.core.event;

import com.example.core.progression.ProgressionData;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Registers Fabric block-break events to give the player a chance
 * to multiply ore drops based on their Luck stat.
 */
public class LuckFortuneHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient() || !(world instanceof ServerWorld serverWorld))
                return;
            if (!(player instanceof ProgressionData data))
                return;

            int luckLevel = data.getLuckLevel();
            if (luckLevel <= 0)
                return;

            // Only apply to ores (commonly defined with "c:ores")
            TagKey<Block> ORES_TAG = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores"));
            if (!state.isIn(ORES_TAG))
                return;

            // 2.5% chance per level to trigger
            float triggerChance = luckLevel * 0.025f;
            if (world.getRandom().nextFloat() >= triggerChance)
                return;

            // Triggered! Generate base drops using the player's current tool
            List<ItemStack> drops = Block.getDroppedStacks(state, serverWorld, pos, blockEntity, player,
                    player.getMainHandStack());

            // Add 1 extra copy of the drops (total drop x2)
            int extraCopies = 1;

            for (ItemStack drop : drops) {
                if (drop.isEmpty())
                    continue;
                ItemStack extraDrop = drop.copy();
                extraDrop.setCount(drop.getCount() * extraCopies);
                Block.dropStack(world, pos, extraDrop);
            }
        });
    }
}
