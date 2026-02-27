package com.example.item;

import com.example.core.progression.ProgressionData;
import com.example.entity.FireballProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireStaffItem extends ElementalStaffItem {

    private static final float MANA_COST = 20.0f;
    private static final int COOLDOWN_TICKS = 20; // 1 second

    public FireStaffItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!(player instanceof ProgressionData data)) {
            return TypedActionResult.fail(stack);
        }

        // Skill must be unlocked
        if (!data.isFireballSkillUnlocked()) {
            return TypedActionResult.fail(stack);
        }

        // Check mana
        if (!data.hasEnoughMana(MANA_COST)) {
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient) {
            // Consume mana
            data.consumeMana(MANA_COST);

            // Compute shoot direction from player look vector
            Vec3d direction = player.getRotationVec(1.0f);

            // Spawn the fireball
            FireballProjectileEntity fireball = new FireballProjectileEntity(world, player, direction);
            world.spawnEntity(fireball);

            // Sound effect
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }

        // Swing arm animation + apply use cooldown
        player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
        player.swingHand(hand);

        return TypedActionResult.success(stack, world.isClient);
    }
}
