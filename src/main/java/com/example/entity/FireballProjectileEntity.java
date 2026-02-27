package com.example.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FireballProjectileEntity extends ProjectileEntity implements FlyingItemEntity {

    private static final float EXPLOSION_POWER = 2.5f;
    private static final int FIRE_RADIUS = 2;
    private static final int MAX_LIFETIME_TICKS = 100;

    private int lifetimeTicks = 0;

    public FireballProjectileEntity(EntityType<? extends FireballProjectileEntity> type, World world) {
        super(type, world);
    }

    public FireballProjectileEntity(World world, LivingEntity owner, Vec3d direction) {
        super(ModEntityTypes.FIREBALL_PROJECTILE, world);
        setOwner(owner);
        setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        Vec3d velocity = direction.normalize().multiply(1.5);
        setVelocity(velocity.x, velocity.y, velocity.z);
    }

    @Override
    protected void initDataTracker() {
        // No tracked data needed
    }

    @Override
    @SuppressWarnings("resource")
    public void tick() {
        super.tick();

        if (getWorld().isClient)
            return;

        lifetimeTicks++;
        if (lifetimeTicks >= MAX_LIFETIME_TICKS) {
            discard();
            return;
        }

        // Move the projectile
        Vec3d velocity = getVelocity();
        setPosition(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);

        // Check for collisions
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            onCollision(hitResult);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        if (!getWorld().isClient) {
            Entity hitEntity = hitResult.getEntity();
            Entity owner = getOwner();
            if (hitEntity instanceof LivingEntity living && hitEntity != owner) {
                living.setOnFireFor(5);
                // Use generic magic damage source since there's no fireball-projectile overload
                living.damage(getWorld().getDamageSources().magic(), 6.0f);
            }
            explode();
            discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult hitResult) {
        if (!getWorld().isClient) {
            explode();
            discard();
        }
    }

    @SuppressWarnings("resource")
    private void explode() {
        World world = getWorld();
        world.createExplosion(
                this,
                getX(), getY(), getZ(),
                EXPLOSION_POWER,
                true, // creates fire
                World.ExplosionSourceType.TNT);

        // Spread fire blocks around the impact point
        BlockPos center = getBlockPos();
        for (int dx = -FIRE_RADIUS; dx <= FIRE_RADIUS; dx++) {
            for (int dy = -1; dy <= FIRE_RADIUS; dy++) {
                for (int dz = -FIRE_RADIUS; dz <= FIRE_RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > FIRE_RADIUS * FIRE_RADIUS)
                        continue;
                    BlockPos pos = center.add(dx, dy, dz);
                    if (world.getBlockState(pos).isAir()
                            && world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    public ItemStack getStack() {
        return new ItemStack(Items.FIRE_CHARGE);
    }
}
